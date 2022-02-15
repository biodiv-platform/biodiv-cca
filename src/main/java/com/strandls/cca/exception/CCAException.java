package com.strandls.cca.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.hibernate.ObjectNotFoundException;

public class CCAException extends Exception {
	
	/**
	 * @author Guddu Sharma
	 */
	private static final long serialVersionUID = 1L;

	public CCAException(Exception e){
		if(e instanceof IllegalArgumentException) {
			throw new WebApplicationException(
					Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build());
		} else if(e instanceof ObjectNotFoundException) {
			throw new WebApplicationException(
					Response.status(Response.Status.NOT_FOUND).entity("Data Not found").build());
		} else if(e instanceof Exception) {
			throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}
}