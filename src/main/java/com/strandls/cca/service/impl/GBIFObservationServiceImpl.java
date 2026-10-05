package com.strandls.cca.service.impl;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.dao.CCADataDao;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.FieldType;
import com.strandls.cca.pojo.fields.value.GeometryFieldValue;
import com.strandls.cca.pojo.geometry.FeatureCollection;
import com.strandls.cca.service.GBIFObservationService;
import com.strandls.externalOccurrences.controllers.GbifObservationsApi;
import com.strandls.externalOccurrences.pojo.GBIFObservationResponse;
import com.strandls.externalOccurrences.pojo.IUCNAggregationResponse;
import com.strandls.externalOccurrences.pojo.OccurrenceLocationResponse;
import com.strandls.externalOccurrences.pojo.SpeciesGroupAggregationResponse;

/**
 * Resolves a CCA's geometry and queries GBIF observations for it through the
 * externalOccurrences service.
 */
public class GBIFObservationServiceImpl implements GBIFObservationService {

	private final Logger logger = LoggerFactory.getLogger(GBIFObservationServiceImpl.class);

	/** Buffer around the CCA geometry; null uses the externalOccurrences default (gbif_buffer_km) */
	private static final Double BUFFER_KM = null;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private CCADataDao ccaDataDao;

	@Inject
	private GbifObservationsApi gbifObservationsApi;

	@Override
	public GBIFObservationResponse getObservationsForCCA(Long ccaId, Integer offset, Integer limit, String speciesGroup, String iucnCategory) {
		// Set default values
		if (offset == null || offset < 0) {
			offset = 0;
		}
		if (limit == null || limit <= 0) {
			limit = 10;
		}

		try {
			String geoJson = getGeoJson(ccaId);
			if (geoJson == null) {
				return emptyObservationResponse(offset, limit);
			}

			GBIFObservationResponse response = gbifObservationsApi.getGBIFObservations(offset, limit, speciesGroup, iucnCategory, BUFFER_KM, geoJson);

			logger.info("Found {} species aggregations (total species: {}, total occurrence records: {}) for CCA id: {} with speciesGroup filter: {}, iucnCategory filter: {}",
					response.getAggregations() == null ? 0 : response.getAggregations().size(), response.getTotalCount(),
					response.getTotalOccurrenceRecords(), ccaId, speciesGroup, iucnCategory);

			return response;

		} catch (Exception e) {
			logger.error("Error querying GBIF observations for CCA id: {}", ccaId, e);
			return emptyObservationResponse(offset, limit);
		}
	}

	@Override
	public SpeciesGroupAggregationResponse getSpeciesGroupAggregationForCCA(Long ccaId) {
		try {
			String geoJson = getGeoJson(ccaId);
			if (geoJson == null) {
				return new SpeciesGroupAggregationResponse().aggregations(new ArrayList<>());
			}

			SpeciesGroupAggregationResponse response = gbifObservationsApi.getSpeciesGroupAggregation(BUFFER_KM, geoJson);

			logger.info("Found {} species group aggregations for CCA id: {}",
					response.getAggregations() == null ? 0 : response.getAggregations().size(), ccaId);

			return response;

		} catch (Exception e) {
			logger.error("Error querying species group aggregations for CCA id: {}", ccaId, e);
			return new SpeciesGroupAggregationResponse().aggregations(new ArrayList<>());
		}
	}

	@Override
	public IUCNAggregationResponse getIUCNAggregationForCCA(Long ccaId) {
		try {
			String geoJson = getGeoJson(ccaId);
			if (geoJson == null) {
				return new IUCNAggregationResponse().aggregations(new ArrayList<>());
			}

			IUCNAggregationResponse response = gbifObservationsApi.getIUCNAggregation(BUFFER_KM, geoJson);

			logger.info("Found {} IUCN category aggregations for CCA id: {}",
					response.getAggregations() == null ? 0 : response.getAggregations().size(), ccaId);

			return response;

		} catch (Exception e) {
			logger.error("Error querying IUCN aggregations for CCA id: {}", ccaId, e);
			return new IUCNAggregationResponse().aggregations(new ArrayList<>());
		}
	}

	@Override
	public OccurrenceLocationResponse getOccurrenceLocationsForCCA(Long ccaId, Integer limit, String speciesGroup, String iucnCategory) {
		try {
			String geoJson = getGeoJson(ccaId);
			if (geoJson == null) {
				return emptyOccurrenceLocationResponse();
			}

			OccurrenceLocationResponse response = gbifObservationsApi.getOccurrenceLocations(limit, speciesGroup, iucnCategory, BUFFER_KM, geoJson);

			logger.info("Found {} occurrence locations (total records: {}, inside records: {}) for CCA id: {} with speciesGroup filter: {}, iucnCategory filter: {}",
					response.getTotalLocations(), response.getTotalRecords(), response.getInsideRecords(), ccaId,
					speciesGroup, iucnCategory);

			return response;

		} catch (Exception e) {
			logger.error("Error querying GBIF occurrence locations for CCA id: {}", ccaId, e);
			return emptyOccurrenceLocationResponse();
		}
	}

	/**
	 * @return the CCA's geometry as a GeoJSON FeatureCollection string, or null if
	 *         the CCA or its geometry doesn't exist
	 */
	private String getGeoJson(Long ccaId) throws Exception {
		// Fetch CCA data
		CCAData ccaData = ccaDataDao.findByProperty(CCAConstants.ID, ccaId, false);
		if (ccaData == null || ccaData.getCcaFieldValues() == null) {
			logger.warn("CCA data not found for id: {}", ccaId);
			return null;
		}

		// Extract geometry from CCAData
		FeatureCollection featureCollection = extractGeometry(ccaData);
		if (featureCollection == null || featureCollection.getFeatures().isEmpty()) {
			logger.warn("No geometry found in CCAData with id: {}", ccaId);
			return null;
		}

		// Convert FeatureCollection to GeoJSON string
		String geoJson = objectMapper.writeValueAsString(featureCollection);
		logger.debug("Generated GeoJSON: {}", geoJson);
		return geoJson;
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

	private static GBIFObservationResponse emptyObservationResponse(Integer offset, Integer limit) {
		return new GBIFObservationResponse().totalCount(0L).totalOccurrenceRecords(0L).offset(offset).limit(limit)
				.aggregations(new ArrayList<>());
	}

	private static OccurrenceLocationResponse emptyOccurrenceLocationResponse() {
		return new OccurrenceLocationResponse().searchArea(null).totalRecords(0L).insideRecords(0L).polygonRecords(0L)
				.totalLocations(0L).truncated(false).locations(new ArrayList<>());
	}
}
