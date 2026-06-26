package com.strandls.cca.service;

import java.util.List;

import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.GBIFObservation;

public interface GBIFObservationService {

	/**
	 * Query GBIF observations from parquet file based on CCA geometry
	 *
	 * @param ccaData The CCA data containing geometry information
	 * @return List of GBIF observations within the bounding box
	 */
	List<GBIFObservation> getObservationsForCCA(CCAData ccaData);
}
