/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * https://opensource.org/licenses/MIT
 *
 * Contributors:
 * Development Gateway - initial API and implementation
 *******************************************************************************/
package org.devgateway.ocvn.web.rest.controller.selector;

import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocvn.persistence.mongo.dao.VNOrganization;
import org.devgateway.ocvn.persistence.mongo.repository.VNOrganizationRepository;
import org.devgateway.ocvn.web.rest.controller.GenericOcvnController;
import org.devgateway.ocvn.web.rest.controller.request.TextSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author mpostelnicu
 * 
 */
@RestController
public class ProcuringEntitySearchController extends GenericOcvnController {

	@Autowired
	private VNOrganizationRepository organizationRepository;

	/**
	 * Searches organizations based on ID. Returns only one result, if the id
	 * exactly matches
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/api/ocds/organization/id/{id:^[a-zA-Z0-9]*$}", method = RequestMethod.GET,
			produces = "application/json")
	public VNOrganization organizationId(@PathVariable final String id) {

		VNOrganization org = organizationRepository.findOne(id);
		return org;
	}

	/**
	 * Searches the {@link VNOrganization} based on a given text. The text has
	 * to have minimum 3 characters and max 30
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/api/ocds/organization/procuringEntity/all", method = RequestMethod.GET, 
			produces = "application/json")
	public List<VNOrganization> procuringEntitySearchText(@ModelAttribute @Valid final TextSearchRequest request) {
		return genericSearchRequest(request, Criteria.where("procuringEntity").is(true), VNOrganization.class);
	}

}