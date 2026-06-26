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
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.FieldType;
import com.strandls.cca.pojo.GBIFObservation;
import com.strandls.cca.pojo.fields.value.GeometryFieldValue;
import com.strandls.cca.pojo.geometry.FeatureCollection;
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
			+ "    FROM geom" + ") " + "SELECT" + "    o.gbifID," + "    o.species,"
			+ "    o.decimalLatitude," + "    o.decimalLongitude," + "    o.stateProvince," + "    o.eventDate,"
			+ "    o.locality" + " FROM '%s' o, bbox"
			+ " WHERE o.decimalLatitude  BETWEEN bbox.min_lat AND bbox.max_lat"
			+ "  AND o.decimalLongitude BETWEEN bbox.min_lon AND bbox.max_lon"
			+ "  AND o.decimalLatitude  IS NOT NULL" + "  AND o.decimalLongitude IS NOT NULL";

	@Override
	public List<GBIFObservation> getObservationsForCCA(CCAData ccaData) {
		List<GBIFObservation> observations = new ArrayList<>();

		if (ccaData == null || ccaData.getCcaFieldValues() == null) {
			return observations;
		}

		// Extract geometry from CCAData
		FeatureCollection featureCollection = extractGeometry(ccaData);
		if (featureCollection == null || featureCollection.getFeatures().isEmpty()) {
			logger.warn("No geometry found in CCAData with id: {}", ccaData.getId());
			return observations;
		}

		try {
			// Convert FeatureCollection to GeoJSON string
			String geoJson = objectMapper.writeValueAsString(featureCollection);
			logger.debug("Generated GeoJSON: {}", geoJson);

			// Get parquet file path from configuration
			String parquetPath = CCAConfig.getProperty("gbif_parquet_path");
			if (parquetPath == null || parquetPath.isEmpty()) {
				logger.error("GBIF parquet file path not configured");
				return observations;
			}

			// Execute DuckDB query
			observations = executeQuery(geoJson, parquetPath);

			logger.info("Found {} GBIF observations for CCA id: {}", observations.size(), ccaData.getId());

		} catch (Exception e) {
			logger.error("Error querying GBIF observations for CCA id: {}", ccaData.getId(), e);
		}

		return observations;
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

	private List<GBIFObservation> executeQuery(String geoJson, String parquetPath) {
		List<GBIFObservation> observations = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection("jdbc:duckdb:")) {

			// Install and load spatial extension
			conn.createStatement().execute("INSTALL spatial;");
			conn.createStatement().execute("LOAD spatial;");

			// Build the query with parquet path
			String query = String.format(DUCKDB_QUERY_TEMPLATE, parquetPath);

			logger.debug("Executing DuckDB query with parquet path: {}", parquetPath);

			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				// Set parameters
				stmt.setString(1, geoJson);

				// Execute query
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						GBIFObservation obs = new GBIFObservation();
						obs.setGbifID(rs.getString("gbifID"));
						obs.setSpecies(rs.getString("species"));
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
}
