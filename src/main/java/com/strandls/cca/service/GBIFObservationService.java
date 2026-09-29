package com.strandls.cca.service;

import com.strandls.externalOccurrences.pojo.GBIFObservationResponse;
import com.strandls.externalOccurrences.pojo.IUCNAggregationResponse;
import com.strandls.externalOccurrences.pojo.OccurrenceLocationResponse;
import com.strandls.externalOccurrences.pojo.SpeciesGroupAggregationResponse;

public interface GBIFObservationService {

	/**
	 * Query GBIF observations from parquet file based on CCA geometry with pagination
	 *
	 * @param ccaId The CCA data ID
	 * @param offset The offset for pagination (default 0)
	 * @param limit The limit for pagination (default 10)
	 * @param speciesGroup Optional species group filter
	 * @param iucnCategory Optional IUCN Red List Category filter
	 * @return Paginated response containing GBIF observations
	 */
	GBIFObservationResponse getObservationsForCCA(Long ccaId, Integer offset, Integer limit, String speciesGroup, String iucnCategory);

	/**
	 * Query GBIF observations aggregated by species group for a CCA
	 *
	 * @param ccaId The CCA data ID
	 * @return Response containing species group aggregations with total and unique counts
	 */
	SpeciesGroupAggregationResponse getSpeciesGroupAggregationForCCA(Long ccaId);

	/**
	 * Query GBIF observations aggregated by IUCN Red List Category for a CCA
	 *
	 * @param ccaId The CCA data ID
	 * @return Response containing IUCN category aggregations with total and unique counts
	 */
	IUCNAggregationResponse getIUCNAggregationForCCA(Long ccaId);

	/**
	 * Query GBIF occurrences for a CCA grouped by location, flagging locations
	 * that fall inside the CCA geometry
	 *
	 * @param ccaId The CCA data ID
	 * @param limit Max number of locations to return
	 * @param speciesGroup Optional species group filter
	 * @param iucnCategory Optional IUCN Red List Category filter
	 * @return Response containing occurrence locations, record totals and search bbox
	 */
	OccurrenceLocationResponse getOccurrenceLocationsForCCA(Long ccaId, Integer limit, String speciesGroup, String iucnCategory);
}
