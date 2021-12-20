package com.strandls.cca;

/**
 * Interface for all the mongodb entities POJO
 * We are using this for the manual ID creation on back-end side.
 * @author 	
 *
 */
public interface IdInterface {

	public void setId(Long id);

	public Long getId();

}
