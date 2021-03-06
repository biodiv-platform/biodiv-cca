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

	public String equalsTo(CCAField field) {
		String diff = "";
		diff += getName().equals(field.getName()) ? "" : "Name : " + getName() + "→" + field.getName() + "\n";
		diff += getQuestion().equals(field.getQuestion()) ? ""
				: "Question : " + getQuestion() + "→" + field.getQuestion() + "\n";
		diff += getHelpText().equals(field.getHelpText()) ? ""
				: "Helptext : " + getHelpText() + "→" + field.getHelpText() + "\n";
		return diff;
	}

}
