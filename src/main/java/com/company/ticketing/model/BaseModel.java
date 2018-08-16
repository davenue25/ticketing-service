package com.company.ticketing.model;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Common functionality for model classes should be put here.
 * 
 * @author daniel
 */
public class BaseModel {

	@Override
	public String toString() {
		ObjectMapper om = new ObjectMapper();
		String s = null;
		try {
			s = om.writeValueAsString(this);
		}
		catch(Exception ex) {
			s = super.toString();
		}
		return s;
	}
}
