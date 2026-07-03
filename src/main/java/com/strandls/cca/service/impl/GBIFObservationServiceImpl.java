package com.strandls.cca.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strandls.cca.CCAConfig;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.dao.CCADataDao;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.FieldType;
import com.strandls.cca.pojo.GBIFObservation;
import com.strandls.cca.pojo.IUCNAggregation;
import com.strandls.cca.pojo.SpeciesAggregation;
import com.strandls.cca.pojo.SpeciesGroupAggregation;
import com.strandls.cca.pojo.fields.value.GeometryFieldValue;
import com.strandls.cca.pojo.geometry.FeatureCollection;
import com.strandls.cca.pojo.response.GBIFObservationResponse;
import com.strandls.cca.pojo.response.IUCNAggregationResponse;
import com.strandls.cca.pojo.response.SpeciesGroupAggregationResponse;
import com.strandls.cca.service.GBIFObservationService;

public class GBIFObservationServiceImpl implements GBIFObservationService {

	private final Logger logger = LoggerFactory.getLogger(GBIFObservationServiceImpl.class);
	private static final double GBIF_POINT_PADDING;

	static {
		try {
			// Explicitly load the DuckDB JDBC driver
			Class.forName("org.duckdb.DuckDBDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to load DuckDB JDBC driver", e);
		}

		// Load padding value from config
		String paddingStr = CCAConfig.getProperty("gbif_point_padding");
		GBIF_POINT_PADDING = (paddingStr != null && !paddingStr.isEmpty()) ? Double.parseDouble(paddingStr) : 0.1;
	}

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private CCADataDao ccaDataDao;

	private static String buildCountQueryTemplate(double padding) {
		return "WITH input AS (" + "    SELECT ? AS geojson" + "), "
			+ "geom AS (" + "    SELECT" + "        ST_GeomFromGeoJSON("
			+ "            json_extract(geojson, '$.features[0].geometry')::VARCHAR" + "        ) AS shape,"
			+ "        json_extract(geojson, '$.features[0].geometry.type')::VARCHAR AS geom_type"
			+ "    FROM input" + "), " + "bbox AS (" + "    SELECT"
			+ "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMin(shape) - " + padding + "            ELSE ST_YMin(shape)"
			+ "        END AS min_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMax(shape) + " + padding + "            ELSE ST_YMax(shape)"
			+ "        END AS max_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMin(shape) - " + padding + "            ELSE ST_XMin(shape)"
			+ "        END AS min_lon," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMax(shape) + " + padding + "            ELSE ST_XMax(shape)" + "        END AS max_lon"
			+ "    FROM geom" + ") " + "SELECT COUNT(DISTINCT o.scientificName) as total FROM '%s' o, bbox"
			+ " WHERE o.decimalLatitude  BETWEEN bbox.min_lat AND bbox.max_lat"
			+ "  AND o.decimalLongitude BETWEEN bbox.min_lon AND bbox.max_lon"
			+ "  AND o.decimalLatitude  IS NOT NULL" + "  AND o.decimalLongitude IS NOT NULL"
			+ "  AND o.scientificName IS NOT NULL";
	}

	private static String buildAggregationQueryTemplate(double padding) {
		return "WITH input AS (" + "    SELECT ? AS geojson"
			+ "), " + "geom AS (" + "    SELECT" + "        ST_GeomFromGeoJSON("
			+ "            json_extract(geojson, '$.features[0].geometry')::VARCHAR" + "        ) AS shape,"
			+ "        json_extract(geojson, '$.features[0].geometry.type')::VARCHAR AS geom_type"
			+ "    FROM input" + "), " + "bbox AS (" + "    SELECT"
			+ "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMin(shape) - " + padding + "            ELSE ST_YMin(shape)"
			+ "        END AS min_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMax(shape) + " + padding + "            ELSE ST_YMax(shape)"
			+ "        END AS max_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMin(shape) - " + padding + "            ELSE ST_XMin(shape)"
			+ "        END AS min_lon," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMax(shape) + " + padding + "            ELSE ST_XMax(shape)" + "        END AS max_lon"
			+ "    FROM geom" + ") "
			+ "SELECT o.scientificName, COUNT(*) as count, FIRST(o.iucnRedListCategory) as iucnRedListCategory, FIRST(o.species_group) as speciesGroup, FIRST(o.taxonKey) as taxonKey, FIRST(o.iucn_link) as iucnLink FROM '%s' o, bbox"
			+ " WHERE o.decimalLatitude  BETWEEN bbox.min_lat AND bbox.max_lat"
			+ "  AND o.decimalLongitude BETWEEN bbox.min_lon AND bbox.max_lon"
			+ "  AND o.decimalLatitude  IS NOT NULL" + "  AND o.decimalLongitude IS NOT NULL"
			+ "  AND o.scientificName IS NOT NULL" + " GROUP BY o.scientificName" + " ORDER BY count DESC"
			+ " LIMIT ? OFFSET ?";
	}

	private static String buildQueryTemplate(double padding) {
		return "WITH input AS (" + "    SELECT ? AS geojson" + "), "
			+ "geom AS (" + "    SELECT" + "        ST_GeomFromGeoJSON("
			+ "            json_extract(geojson, '$.features[0].geometry')::VARCHAR" + "        ) AS shape,"
			+ "        json_extract(geojson, '$.features[0].geometry.type')::VARCHAR AS geom_type"
			+ "    FROM input" + "), " + "bbox AS (" + "    SELECT"
			+ "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMin(shape) - " + padding + "            ELSE ST_YMin(shape)"
			+ "        END AS min_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMax(shape) + " + padding + "            ELSE ST_YMax(shape)"
			+ "        END AS max_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMin(shape) - " + padding + "            ELSE ST_XMin(shape)"
			+ "        END AS min_lon," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMax(shape) + " + padding + "            ELSE ST_XMax(shape)" + "        END AS max_lon"
			+ "    FROM geom" + ") " + "SELECT" + "    o.gbifID," + "    o.scientificName,"
			+ "    o.decimalLatitude," + "    o.decimalLongitude," + "    o.stateProvince," + "    o.eventDate,"
			+ "    o.locality" + " FROM '%s' o, bbox"
			+ " WHERE o.decimalLatitude  BETWEEN bbox.min_lat AND bbox.max_lat"
			+ "  AND o.decimalLongitude BETWEEN bbox.min_lon AND bbox.max_lon"
			+ "  AND o.decimalLatitude  IS NOT NULL" + "  AND o.decimalLongitude IS NOT NULL"
			+ " LIMIT ? OFFSET ?";
	}

	private static String buildSpeciesGroupAggregationQueryTemplate(double padding) {
		return "WITH input AS ("
			+ "    SELECT ? AS geojson" + "), " + "geom AS (" + "    SELECT" + "        ST_GeomFromGeoJSON("
			+ "            json_extract(geojson, '$.features[0].geometry')::VARCHAR" + "        ) AS shape,"
			+ "        json_extract(geojson, '$.features[0].geometry.type')::VARCHAR AS geom_type"
			+ "    FROM input" + "), " + "bbox AS (" + "    SELECT"
			+ "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMin(shape) - " + padding + "            ELSE ST_YMin(shape)"
			+ "        END AS min_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMax(shape) + " + padding + "            ELSE ST_YMax(shape)"
			+ "        END AS max_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMin(shape) - " + padding + "            ELSE ST_XMin(shape)"
			+ "        END AS min_lon," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMax(shape) + " + padding + "            ELSE ST_XMax(shape)" + "        END AS max_lon"
			+ "    FROM geom" + ") "
			+ "SELECT o.species_group, COUNT(*) as totalCount, COUNT(DISTINCT o.scientificName) as uniqueSpeciesCount FROM '%s' o, bbox"
			+ " WHERE o.decimalLatitude  BETWEEN bbox.min_lat AND bbox.max_lat"
			+ "  AND o.decimalLongitude BETWEEN bbox.min_lon AND bbox.max_lon"
			+ "  AND o.decimalLatitude  IS NOT NULL" + "  AND o.decimalLongitude IS NOT NULL"
			+ "  AND o.species_group IS NOT NULL" + " GROUP BY o.species_group"
			+ " ORDER BY CASE WHEN o.species_group = 'Others' THEN 1 ELSE 0 END, totalCount DESC";
	}

	private static String buildIUCNAggregationQueryTemplate(double padding) {
		return "WITH input AS ("
			+ "    SELECT ? AS geojson" + "), " + "geom AS (" + "    SELECT" + "        ST_GeomFromGeoJSON("
			+ "            json_extract(geojson, '$.features[0].geometry')::VARCHAR" + "        ) AS shape,"
			+ "        json_extract(geojson, '$.features[0].geometry.type')::VARCHAR AS geom_type"
			+ "    FROM input" + "), " + "bbox AS (" + "    SELECT"
			+ "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMin(shape) - " + padding + "            ELSE ST_YMin(shape)"
			+ "        END AS min_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMax(shape) + " + padding + "            ELSE ST_YMax(shape)"
			+ "        END AS max_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMin(shape) - " + padding + "            ELSE ST_XMin(shape)"
			+ "        END AS min_lon," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMax(shape) + " + padding + "            ELSE ST_XMax(shape)" + "        END AS max_lon"
			+ "    FROM geom" + "), "
			+ "all_categories AS ("
			+ "    SELECT unnest(['CR', 'EN', 'VU', 'NT', 'LC', 'DD', 'NE']) as category"
			+ "), "
			+ "observed_counts AS ("
			+ "    SELECT o.iucnRedListCategory, COUNT(*) as totalCount, COUNT(DISTINCT o.scientificName) as uniqueSpeciesCount"
			+ "    FROM '%s' o, bbox"
			+ "    WHERE o.decimalLatitude BETWEEN bbox.min_lat AND bbox.max_lat"
			+ "      AND o.decimalLongitude BETWEEN bbox.min_lon AND bbox.max_lon"
			+ "      AND o.decimalLatitude IS NOT NULL"
			+ "      AND o.decimalLongitude IS NOT NULL"
			+ "      AND o.iucnRedListCategory IS NOT NULL"
			+ "    GROUP BY o.iucnRedListCategory"
			+ ") "
			+ "SELECT ac.category as iucnRedListCategory, "
			+ "    COALESCE(oc.totalCount, 0) as totalCount, "
			+ "    COALESCE(oc.uniqueSpeciesCount, 0) as uniqueSpeciesCount "
			+ "FROM all_categories ac "
			+ "LEFT JOIN observed_counts oc ON ac.category = oc.iucnRedListCategory "
			+ "ORDER BY totalCount DESC";
	}

	@Override
	public GBIFObservationResponse getObservationsForCCA(Long ccaId, Integer offset, Integer limit, String speciesGroup, String iucnCategory) {
		// Set default values
		if (offset == null || offset < 0) {
			offset = 0;
		}
		if (limit == null || limit <= 0) {
			limit = 10;
		}

		// Fetch CCA data
		CCAData ccaData = ccaDataDao.findByProperty(CCAConstants.ID, ccaId, false);
		if (ccaData == null || ccaData.getCcaFieldValues() == null) {
			logger.warn("CCA data not found for id: {}", ccaId);
			return new GBIFObservationResponse(0L, offset, limit, new ArrayList<>(), new ArrayList<>());
		}

		// Extract geometry from CCAData
		FeatureCollection featureCollection = extractGeometry(ccaData);
		if (featureCollection == null || featureCollection.getFeatures().isEmpty()) {
			logger.warn("No geometry found in CCAData with id: {}", ccaId);
			return new GBIFObservationResponse(0L, offset, limit, new ArrayList<>(), new ArrayList<>());
		}

		try {
			// Convert FeatureCollection to GeoJSON string
			String geoJson = objectMapper.writeValueAsString(featureCollection);
			logger.debug("Generated GeoJSON: {}", geoJson);

			// Get parquet file path from configuration
			String parquetPath = CCAConfig.getProperty("gbif_parquet_path");
			if (parquetPath == null || parquetPath.isEmpty()) {
				logger.error("GBIF parquet file path not configured");
				return new GBIFObservationResponse(0L, offset, limit, new ArrayList<>(), new ArrayList<>());
			}

			// Execute queries
			Long totalCount = executeCountQuery(geoJson, parquetPath, speciesGroup, iucnCategory);
			List<SpeciesAggregation> aggregations = executeAggregationQuery(geoJson, parquetPath, limit, offset, speciesGroup, iucnCategory);
			List<GBIFObservation> observations = executeQuery(geoJson, parquetPath, limit, offset, speciesGroup, iucnCategory);

			logger.info("Found {} species aggregations and {} GBIF observations (total species: {}) for CCA id: {} with speciesGroup filter: {}, iucnCategory filter: {}",
					aggregations.size(), observations.size(), totalCount, ccaId, speciesGroup, iucnCategory);

			return new GBIFObservationResponse(totalCount, offset, limit, aggregations, observations);

		} catch (Exception e) {
			logger.error("Error querying GBIF observations for CCA id: {}", ccaId, e);
			return new GBIFObservationResponse(0L, offset, limit, new ArrayList<>(), new ArrayList<>());
		}
	}

	private FeatureCollection extractGeometry(CCAData ccaData) {
		Map<String, CCAFieldValue> fieldValues = ccaData.getCcaFieldValues();

		for (Map.Entry<String, CCAFieldValue> entry : fieldValues.entrySet()) {
			if (entry.getValue().getType().equals(FieldType.GEOMETRY)) {
				GeometryFieldValue geometryFieldValue = (GeometryFieldValue) entry.getValue();
				return geometryFieldValue.getValue();
			}
		}
		return null;
	}

	private Long executeCountQuery(String geoJson, String parquetPath, String speciesGroup, String iucnCategory) {
		Long count = 0L;

		try (Connection conn = DriverManager.getConnection("jdbc:duckdb:")) {

			// Install and load spatial extension
			conn.createStatement().execute("INSTALL spatial;");
			conn.createStatement().execute("LOAD spatial;");

			// Build the count query with parquet path and optional filters
			String baseQuery = String.format(buildCountQueryTemplate(GBIF_POINT_PADDING), parquetPath);
			int paramIndex = 2;
			if (speciesGroup != null && !speciesGroup.isEmpty()) {
				baseQuery = baseQuery + " AND o.species_group = ?";
			}
			if (iucnCategory != null && !iucnCategory.isEmpty()) {
				baseQuery = baseQuery + " AND o.iucnRedListCategory = ?";
			}

			logger.debug("Executing DuckDB count query with speciesGroup: {}, iucnCategory: {}", speciesGroup, iucnCategory);

			try (PreparedStatement stmt = conn.prepareStatement(baseQuery)) {
				// Set parameters
				stmt.setString(1, geoJson);
				if (speciesGroup != null && !speciesGroup.isEmpty()) {
					stmt.setString(paramIndex++, speciesGroup);
				}
				if (iucnCategory != null && !iucnCategory.isEmpty()) {
					stmt.setString(paramIndex++, iucnCategory);
				}

				// Execute query
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						count = rs.getLong("total");
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error executing DuckDB count query", e);
		}

		return count;
	}

	private List<GBIFObservation> executeQuery(String geoJson, String parquetPath, Integer limit, Integer offset, String speciesGroup, String iucnCategory) {
		List<GBIFObservation> observations = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection("jdbc:duckdb:")) {

			// Install and load spatial extension
			conn.createStatement().execute("INSTALL spatial;");
			conn.createStatement().execute("LOAD spatial;");

			// Build the query with parquet path and optional filters
			String baseQuery = String.format(buildQueryTemplate(GBIF_POINT_PADDING), parquetPath);
			// Remove the LIMIT/OFFSET to add filters before them
			baseQuery = baseQuery.replace(" LIMIT ? OFFSET ?", "");
			if (speciesGroup != null && !speciesGroup.isEmpty()) {
				baseQuery = baseQuery + " AND o.species_group = ?";
			}
			if (iucnCategory != null && !iucnCategory.isEmpty()) {
				baseQuery = baseQuery + " AND o.iucnRedListCategory = ?";
			}
			baseQuery = baseQuery + " LIMIT ? OFFSET ?";

			logger.debug("Executing DuckDB query with parquet path: {}, limit: {}, offset: {}, speciesGroup: {}, iucnCategory: {}", parquetPath, limit,
					offset, speciesGroup, iucnCategory);

			try (PreparedStatement stmt = conn.prepareStatement(baseQuery)) {
				// Set parameters
				int paramIndex = 1;
				stmt.setString(paramIndex++, geoJson);
				if (speciesGroup != null && !speciesGroup.isEmpty()) {
					stmt.setString(paramIndex++, speciesGroup);
				}
				if (iucnCategory != null && !iucnCategory.isEmpty()) {
					stmt.setString(paramIndex++, iucnCategory);
				}
				stmt.setInt(paramIndex++, limit);
				stmt.setInt(paramIndex++, offset);

				// Execute query
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						GBIFObservation obs = new GBIFObservation();
						obs.setGbifID(rs.getString("gbifID"));
						obs.setScientificName(rs.getString("scientificName"));
						obs.setDecimalLatitude(rs.getDouble("decimalLatitude"));
						obs.setDecimalLongitude(rs.getDouble("decimalLongitude"));
						obs.setStateProvince(rs.getString("stateProvince"));
						obs.setEventDate(rs.getString("eventDate"));
						obs.setLocality(rs.getString("locality"));
						observations.add(obs);
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error executing DuckDB query", e);
		}

		return observations;
	}

	private List<SpeciesAggregation> executeAggregationQuery(String geoJson, String parquetPath, Integer limit,
			Integer offset, String speciesGroup, String iucnCategory) {
		List<SpeciesAggregation> aggregations = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection("jdbc:duckdb:")) {

			// Install and load spatial extension
			conn.createStatement().execute("INSTALL spatial;");
			conn.createStatement().execute("LOAD spatial;");

			// Build the aggregation query with parquet path and optional filters
			String baseQuery = String.format(buildAggregationQueryTemplate(GBIF_POINT_PADDING), parquetPath);
			// Remove the LIMIT/OFFSET to add filters before them
			baseQuery = baseQuery.replace(" LIMIT ? OFFSET ?", "");
			String filterClause = "";
			if (speciesGroup != null && !speciesGroup.isEmpty()) {
				filterClause += " AND o.species_group = ?";
			}
			if (iucnCategory != null && !iucnCategory.isEmpty()) {
				filterClause += " AND o.iucnRedListCategory = ?";
			}
			if (!filterClause.isEmpty()) {
				baseQuery = baseQuery.replace(" GROUP BY o.scientificName", filterClause + " GROUP BY o.scientificName");
			}
			baseQuery = baseQuery + " LIMIT ? OFFSET ?";

			logger.debug("Executing DuckDB aggregation query with parquet path: {}, limit: {}, offset: {}, speciesGroup: {}, iucnCategory: {}",
					parquetPath, limit, offset, speciesGroup, iucnCategory);

			try (PreparedStatement stmt = conn.prepareStatement(baseQuery)) {
				// Set parameters
				int paramIndex = 1;
				stmt.setString(paramIndex++, geoJson);
				if (speciesGroup != null && !speciesGroup.isEmpty()) {
					stmt.setString(paramIndex++, speciesGroup);
				}
				if (iucnCategory != null && !iucnCategory.isEmpty()) {
					stmt.setString(paramIndex++, iucnCategory);
				}
				stmt.setInt(paramIndex++, limit);
				stmt.setInt(paramIndex++, offset);

				// Execute query
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						SpeciesAggregation agg = new SpeciesAggregation();
						agg.setScientificName(rs.getString("scientificName"));
						agg.setCount(rs.getLong("count"));
						agg.setIucnRedListCategory(rs.getString("iucnRedListCategory"));
						agg.setSpeciesGroup(rs.getString("speciesGroup"));
						agg.setTaxonKey(rs.getLong("taxonKey"));
						agg.setIucnLink(rs.getString("iucnLink"));
						aggregations.add(agg);
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error executing DuckDB aggregation query", e);
		}

		return aggregations;
	}

	@Override
	public SpeciesGroupAggregationResponse getSpeciesGroupAggregationForCCA(Long ccaId) {
		// Fetch CCA data
		CCAData ccaData = ccaDataDao.findByProperty(CCAConstants.ID, ccaId, false);
		if (ccaData == null || ccaData.getCcaFieldValues() == null) {
			logger.warn("CCA data not found for id: {}", ccaId);
			return new SpeciesGroupAggregationResponse(new ArrayList<>());
		}

		// Extract geometry from CCAData
		FeatureCollection featureCollection = extractGeometry(ccaData);
		if (featureCollection == null || featureCollection.getFeatures().isEmpty()) {
			logger.warn("No geometry found in CCAData with id: {}", ccaId);
			return new SpeciesGroupAggregationResponse(new ArrayList<>());
		}

		try {
			// Convert FeatureCollection to GeoJSON string
			String geoJson = objectMapper.writeValueAsString(featureCollection);
			logger.debug("Generated GeoJSON for species group aggregation: {}", geoJson);

			// Get parquet file path from configuration
			String parquetPath = CCAConfig.getProperty("gbif_parquet_path");
			if (parquetPath == null || parquetPath.isEmpty()) {
				logger.error("GBIF parquet file path not configured");
				return new SpeciesGroupAggregationResponse(new ArrayList<>());
			}

			// Execute species group aggregation query
			List<SpeciesGroupAggregation> aggregations = executeSpeciesGroupAggregationQuery(geoJson, parquetPath);

			logger.info("Found {} species group aggregations for CCA id: {}", aggregations.size(), ccaId);

			return new SpeciesGroupAggregationResponse(aggregations);

		} catch (Exception e) {
			logger.error("Error querying species group aggregations for CCA id: {}", ccaId, e);
			return new SpeciesGroupAggregationResponse(new ArrayList<>());
		}
	}

	private List<SpeciesGroupAggregation> executeSpeciesGroupAggregationQuery(String geoJson, String parquetPath) {
		List<SpeciesGroupAggregation> aggregations = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection("jdbc:duckdb:")) {

			// Install and load spatial extension
			conn.createStatement().execute("INSTALL spatial;");
			conn.createStatement().execute("LOAD spatial;");

			// Build the species group aggregation query with parquet path
			String query = String.format(buildSpeciesGroupAggregationQueryTemplate(GBIF_POINT_PADDING), parquetPath);

			logger.debug("Executing DuckDB species group aggregation query with parquet path: {}", parquetPath);

			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				// Set parameters
				stmt.setString(1, geoJson);

				// Execute query
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						SpeciesGroupAggregation agg = new SpeciesGroupAggregation();
						agg.setSpeciesGroup(rs.getString("species_group"));
						agg.setTotalCount(rs.getLong("totalCount"));
						agg.setUniqueSpeciesCount(rs.getLong("uniqueSpeciesCount"));
						aggregations.add(agg);
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error executing DuckDB species group aggregation query", e);
		}

		return aggregations;
	}

	@Override
	public IUCNAggregationResponse getIUCNAggregationForCCA(Long ccaId) {
		// Fetch CCA data
		CCAData ccaData = ccaDataDao.findByProperty(CCAConstants.ID, ccaId, false);
		if (ccaData == null || ccaData.getCcaFieldValues() == null) {
			logger.warn("CCA data not found for id: {}", ccaId);
			return new IUCNAggregationResponse(new ArrayList<>());
		}

		// Extract geometry from CCAData
		FeatureCollection featureCollection = extractGeometry(ccaData);
		if (featureCollection == null || featureCollection.getFeatures().isEmpty()) {
			logger.warn("No geometry found in CCAData with id: {}", ccaId);
			return new IUCNAggregationResponse(new ArrayList<>());
		}

		try {
			// Convert FeatureCollection to GeoJSON string
			String geoJson = objectMapper.writeValueAsString(featureCollection);
			logger.debug("Generated GeoJSON for IUCN aggregation: {}", geoJson);

			// Get parquet file path from configuration
			String parquetPath = CCAConfig.getProperty("gbif_parquet_path");
			if (parquetPath == null || parquetPath.isEmpty()) {
				logger.error("GBIF parquet file path not configured");
				return new IUCNAggregationResponse(new ArrayList<>());
			}

			// Execute IUCN aggregation query
			List<IUCNAggregation> aggregations = executeIUCNAggregationQuery(geoJson, parquetPath);

			logger.info("Found {} IUCN category aggregations for CCA id: {}", aggregations.size(), ccaId);

			return new IUCNAggregationResponse(aggregations);

		} catch (Exception e) {
			logger.error("Error querying IUCN aggregations for CCA id: {}", ccaId, e);
			return new IUCNAggregationResponse(new ArrayList<>());
		}
	}

	private List<IUCNAggregation> executeIUCNAggregationQuery(String geoJson, String parquetPath) {
		List<IUCNAggregation> aggregations = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection("jdbc:duckdb:")) {

			// Install and load spatial extension
			conn.createStatement().execute("INSTALL spatial;");
			conn.createStatement().execute("LOAD spatial;");

			// Build the IUCN aggregation query with parquet path
			String query = String.format(buildIUCNAggregationQueryTemplate(GBIF_POINT_PADDING), parquetPath);

			logger.debug("Executing DuckDB IUCN aggregation query with parquet path: {}", parquetPath);

			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				// Set parameters
				stmt.setString(1, geoJson);

				// Execute query
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						IUCNAggregation agg = new IUCNAggregation();
						agg.setIucnRedListCategory(rs.getString("iucnRedListCategory"));
						agg.setTotalCount(rs.getLong("totalCount"));
						agg.setUniqueSpeciesCount(rs.getLong("uniqueSpeciesCount"));
						aggregations.add(agg);
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error executing DuckDB IUCN aggregation query", e);
		}

		return aggregations;
	}
}
