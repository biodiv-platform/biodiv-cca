/**
 * 
 */
package com.strandls.cca.pojo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author vilay
 *
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class CCAData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4876131476114881785L;
	
	@Id
	private Long id;
	
	private Description description;
	private OriginAndEstablishment originAndEstablishment;
	private GovernananceManagmentAndConservation governananceManagmentAndConservation;
	private LegalAndOtherRecognitions legalAndOtherRecognitions;
	private ImpactOpportunityChallenges impactOpportunityChallenges;
	private DataContributor dataContributor;
	private ReferenceAndGlossary referenceAndGlossary;
	
}
