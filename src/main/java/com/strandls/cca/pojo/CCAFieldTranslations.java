package com.strandls.cca.pojo;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator()
public class CCAFieldTranslations {

	private String name;
	private String question;
	private String helpText;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public CCAField translate(CCAField ccaField) {
		ccaField.setName(name);
		ccaField.setQuestion(question);
		ccaField.setHelpText(helpText);
		return ccaField;
	}

	public void addUpdateTranslation(CCAField ccaField) {
		this.setName(ccaField.getName());
		this.setQuestion(ccaField.getQuestion());
		this.setHelpText(ccaField.getHelpText());
	}

	public boolean equalsTo(CCAField field) {
		return getName().equals(field.getName()) && getQuestion().equals(field.getQuestion())
				&& getHelpText().equals(field.getHelpText());
	}

}
