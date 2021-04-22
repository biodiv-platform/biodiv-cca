/**
 * 
 */
package com.strandls.cca.pojo;

import java.io.Serializable;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.vz.mongodb.jackson.ObjectId;

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

	@ObjectId
	private String _id;

	private Description description;
	private OriginAndEstablishment originAndEstablishment;
	private GovernananceManagmentAndConservation governananceManagmentAndConservation;
	private LegalAndOtherRecognitions legalAndOtherRecognitions;
	private ImpactOpportunityChallenges impactOpportunityChallenges;
	private DataContributor dataContributor;
	private ReferenceAndGlossary referenceAndGlossary;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	public OriginAndEstablishment getOriginAndEstablishment() {
		return originAndEstablishment;
	}

	public void setOriginAndEstablishment(OriginAndEstablishment originAndEstablishment) {
		this.originAndEstablishment = originAndEstablishment;
	}

	public GovernananceManagmentAndConservation getGovernananceManagmentAndConservation() {
		return governananceManagmentAndConservation;
	}

	public void setGovernananceManagmentAndConservation(
			GovernananceManagmentAndConservation governananceManagmentAndConservation) {
		this.governananceManagmentAndConservation = governananceManagmentAndConservation;
	}

	public LegalAndOtherRecognitions getLegalAndOtherRecognitions() {
		return legalAndOtherRecognitions;
	}

	public void setLegalAndOtherRecognitions(LegalAndOtherRecognitions legalAndOtherRecognitions) {
		this.legalAndOtherRecognitions = legalAndOtherRecognitions;
	}

	public ImpactOpportunityChallenges getImpactOpportunityChallenges() {
		return impactOpportunityChallenges;
	}

	public void setImpactOpportunityChallenges(ImpactOpportunityChallenges impactOpportunityChallenges) {
		this.impactOpportunityChallenges = impactOpportunityChallenges;
	}

	public DataContributor getDataContributor() {
		return dataContributor;
	}

	public void setDataContributor(DataContributor dataContributor) {
		this.dataContributor = dataContributor;
	}

	public ReferenceAndGlossary getReferenceAndGlossary() {
		return referenceAndGlossary;
	}

	public void setReferenceAndGlossary(ReferenceAndGlossary referenceAndGlossary) {
		this.referenceAndGlossary = referenceAndGlossary;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
