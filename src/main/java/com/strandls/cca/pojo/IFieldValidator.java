package com.strandls.cca.pojo;

public interface IFieldValidator {
	public abstract void validate();

	public abstract boolean validate(CCAFieldValue fieldValue);
}
