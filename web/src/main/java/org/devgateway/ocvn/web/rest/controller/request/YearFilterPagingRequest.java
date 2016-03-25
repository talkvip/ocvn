/**
 * 
 */
package org.devgateway.ocvn.web.rest.controller.request;

import java.util.List;

import cz.jirutka.validator.collection.constraints.EachRange;

/**
 * @author mpostelnicu
 *
 */
public class YearFilterPagingRequest extends DefaultFilterPagingRequest {

	@EachRange(min = 1900, max = 2200)
	List<Integer> year;

	/**
	 * 
	 */
	public YearFilterPagingRequest() {
		super();
	}

	public List<Integer> getYear() {
		return year;
	}

	public void setYear(List<Integer> year) {
		this.year = year;
	}

}