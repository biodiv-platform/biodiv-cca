package com.strandls.cca.service;

import com.strandls.cca.pojo.response.GBIFObservationResponse;
import com.strandls.cca.pojo.response.IUCNAggregationResponse;
import com.strandls.cca.pojo.response.SpeciesGroupAggregationResponse;

public interface GBIFObservationService {

	/**
	 * Query GBIF observations from parquet file based on CCA geometry with pagination
	 *
	 * @param ccaId The CCA data ID
	 * @param offset The offset for pagination (default 0)
	 * @param limit The limit for pagination (default 10)
	 * @return Paginated response containing GBIF observations
	 */
	GBIFObservationResponse getObservationsForCCA(Long ccaId, Integer offset, Integer limit);

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
}
