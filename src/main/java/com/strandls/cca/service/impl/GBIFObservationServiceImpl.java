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
import com.strandls.cca.pojo.SpeciesAggregation;
import com.strandls.cca.pojo.fields.value.GeometryFieldValue;
import com.strandls.cca.pojo.geometry.FeatureCollection;
import com.strandls.cca.pojo.response.GBIFObservationResponse;
import com.strandls.cca.service.GBIFObservationService;

public class GBIFObservationServiceImpl implements GBIFObservationService {

	private final Logger logger = LoggerFactory.getLogger(GBIFObservationServiceImpl.class);

	static {
		try {
			// Explicitly load the DuckDB JDBC driver
			Class.forName("org.duckdb.DuckDBDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to load DuckDB JDBC driver", e);
		}
	}

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private CCADataDao ccaDataDao;

	private static final String DUCKDB_COUNT_QUERY_TEMPLATE = "WITH input AS (" + "    SELECT ? AS geojson" + "), "
			+ "geom AS (" + "    SELECT" + "        ST_GeomFromGeoJSON("
			+ "            json_extract(geojson, '$.features[0].geometry')::VARCHAR" + "        ) AS shape,"
			+ "        json_extract(geojson, '$.features[0].geometry.type')::VARCHAR AS geom_type"
			+ "    FROM input" + "), " + "bbox AS (" + "    SELECT"
			+ "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMin(shape) - 0.2" + "            ELSE ST_YMin(shape)"
			+ "        END AS min_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMax(shape) + 0.2" + "            ELSE ST_YMax(shape)"
			+ "        END AS max_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMin(shape) - 0.2" + "            ELSE ST_XMin(shape)"
			+ "        END AS min_lon," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMax(shape) + 0.2" + "            ELSE ST_XMax(shape)" + "        END AS max_lon"
			+ "    FROM geom" + ") " + "SELECT COUNT(DISTINCT o.scientificName) as total FROM '%s' o, bbox"
			+ " WHERE o.decimalLatitude  BETWEEN bbox.min_lat AND bbox.max_lat"
			+ "  AND o.decimalLongitude BETWEEN bbox.min_lon AND bbox.max_lon"
			+ "  AND o.decimalLatitude  IS NOT NULL" + "  AND o.decimalLongitude IS NOT NULL"
			+ "  AND o.scientificName IS NOT NULL";

	private static final String DUCKDB_AGGREGATION_QUERY_TEMPLATE = "WITH input AS (" + "    SELECT ? AS geojson"
			+ "), " + "geom AS (" + "    SELECT" + "        ST_GeomFromGeoJSON("
			+ "            json_extract(geojson, '$.features[0].geometry')::VARCHAR" + "        ) AS shape,"
			+ "        json_extract(geojson, '$.features[0].geometry.type')::VARCHAR AS geom_type"
			+ "    FROM input" + "), " + "bbox AS (" + "    SELECT"
			+ "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMin(shape) - 0.2" + "            ELSE ST_YMin(shape)"
			+ "        END AS min_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMax(shape) + 0.2" + "            ELSE ST_YMax(shape)"
			+ "        END AS max_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMin(shape) - 0.2" + "            ELSE ST_XMin(shape)"
			+ "        END AS min_lon," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMax(shape) + 0.2" + "            ELSE ST_XMax(shape)" + "        END AS max_lon"
			+ "    FROM geom" + ") "
			+ "SELECT o.scientificName, COUNT(*) as count, FIRST(o.iucnRedListCategory) as iucnRedListCategory, FIRST(o.speciesGroup) as speciesGroup FROM '%s' o, bbox"
			+ " WHERE o.decimalLatitude  BETWEEN bbox.min_lat AND bbox.max_lat"
			+ "  AND o.decimalLongitude BETWEEN bbox.min_lon AND bbox.max_lon"
			+ "  AND o.decimalLatitude  IS NOT NULL" + "  AND o.decimalLongitude IS NOT NULL"
			+ "  AND o.scientificName IS NOT NULL" + " GROUP BY o.scientificName" + " ORDER BY count DESC"
			+ " LIMIT ? OFFSET ?";

	private static final String DUCKDB_QUERY_TEMPLATE = "WITH input AS (" + "    SELECT ? AS geojson" + "), "
			+ "geom AS (" + "    SELECT" + "        ST_GeomFromGeoJSON("
			+ "            json_extract(geojson, '$.features[0].geometry')::VARCHAR" + "        ) AS shape,"
			+ "        json_extract(geojson, '$.features[0].geometry.type')::VARCHAR AS geom_type"
			+ "    FROM input" + "), " + "bbox AS (" + "    SELECT"
			+ "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMin(shape) - 0.2" + "            ELSE ST_YMin(shape)"
			+ "        END AS min_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_YMax(shape) + 0.2" + "            ELSE ST_YMax(shape)"
			+ "        END AS max_lat," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMin(shape) - 0.2" + "            ELSE ST_XMin(shape)"
			+ "        END AS min_lon," + "        CASE WHEN geom_type = '\"Point\"'"
			+ "            THEN ST_XMax(shape) + 0.2" + "            ELSE ST_XMax(shape)" + "        END AS max_lon"
			+ "    FROM geom" + ") " + "SELECT" + "    o.gbifID," + "    o.scientificName,"
			+ "    o.decimalLatitude," + "    o.decimalLongitude," + "    o.stateProvince," + "    o.eventDate,"
			+ "    o.locality" + " FROM '%s' o, bbox"
			+ " WHERE o.decimalLatitude  BETWEEN bbox.min_lat AND bbox.max_lat"
			+ "  AND o.decimalLongitude BETWEEN bbox.min_lon AND bbox.max_lon"
			+ "  AND o.decimalLatitude  IS NOT NULL" + "  AND o.decimalLongitude IS NOT NULL"
			+ " LIMIT ? OFFSET ?";

	@Override
	public GBIFObservationResponse getObservationsForCCA(Long ccaId, Integer offset, Integer limit) {
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
			Long totalCount = executeCountQuery(geoJson, parquetPath);
			List<SpeciesAggregation> aggregations = executeAggregationQuery(geoJson, parquetPath, limit, offset);
			List<GBIFObservation> observations = executeQuery(geoJson, parquetPath, limit, offset);

			logger.info("Found {} species aggregations and {} GBIF observations (total species: {}) for CCA id: {}",
					aggregations.size(), observations.size(), totalCount, ccaId);

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

	private Long executeCountQuery(String geoJson, String parquetPath) {
		Long count = 0L;

		try (Connection conn = DriverManager.getConnection("jdbc:duckdb:")) {

			// Install and load spatial extension
			conn.createStatement().execute("INSTALL spatial;");
			conn.createStatement().execute("LOAD spatial;");

			// Build the count query with parquet path
			String query = String.format(DUCKDB_COUNT_QUERY_TEMPLATE, parquetPath);

			logger.debug("Executing DuckDB count query");

			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				// Set parameters
				stmt.setString(1, geoJson);

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

	private List<GBIFObservation> executeQuery(String geoJson, String parquetPath, Integer limit, Integer offset) {
		List<GBIFObservation> observations = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection("jdbc:duckdb:")) {

			// Install and load spatial extension
			conn.createStatement().execute("INSTALL spatial;");
			conn.createStatement().execute("LOAD spatial;");

			// Build the query with parquet path
			String query = String.format(DUCKDB_QUERY_TEMPLATE, parquetPath);

			logger.debug("Executing DuckDB query with parquet path: {}, limit: {}, offset: {}", parquetPath, limit,
					offset);

			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				// Set parameters
				stmt.setString(1, geoJson);
				stmt.setInt(2, limit);
				stmt.setInt(3, offset);

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
			Integer offset) {
		List<SpeciesAggregation> aggregations = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection("jdbc:duckdb:")) {

			// Install and load spatial extension
			conn.createStatement().execute("INSTALL spatial;");
			conn.createStatement().execute("LOAD spatial;");

			// Build the aggregation query with parquet path
			String query = String.format(DUCKDB_AGGREGATION_QUERY_TEMPLATE, parquetPath);

			logger.debug("Executing DuckDB aggregation query with parquet path: {}, limit: {}, offset: {}",
					parquetPath, limit, offset);

			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				// Set parameters
				stmt.setString(1, geoJson);
				stmt.setInt(2, limit);
				stmt.setInt(3, offset);

				// Execute query
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						SpeciesAggregation agg = new SpeciesAggregation();
						agg.setScientificName(rs.getString("scientificName"));
						agg.setCount(rs.getLong("count"));
						agg.setIucnRedListCategory(rs.getString("iucnRedListCategory"));
						agg.setSpeciesGroup(rs.getString("speciesGroup"));
						aggregations.add(agg);
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error executing DuckDB aggregation query", e);
		}

		return aggregations;
	}
}
