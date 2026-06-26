package com.strandls.cca.service;

import com.strandls.cca.pojo.response.GBIFObservationResponse;

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
}
