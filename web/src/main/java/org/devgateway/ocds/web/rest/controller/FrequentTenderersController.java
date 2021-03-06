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
package org.devgateway.ocds.web.rest.controller;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 *
 * @author mpostelnicu
 *
 */
@RestController
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "genericPagingRequestJson")
@Cacheable
public class FrequentTenderersController extends GenericOCDSController {

    private class TendererPair {
        private String tendererId1;
        private String tendererId2;

        public String getTendererId1() {
            return tendererId1;
        }

        public void setTendererId1(String tenderer1) {
            this.tendererId1 = tenderer1;
        }

        public String getTendererId2() {
            return tendererId2;
        }

        public void setTendererId2(String tenderer2) {
            this.tendererId2 = tenderer2;
        }

    }

    private class ValueObject {
        private TendererPair id;
        private Integer value;

        public TendererPair getId() {
            return id;
        }

        public void setId(TendererPair id) {
            this.id = id;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    @ApiOperation(value = "Detect frequent pairs of tenderers that apply together to bids."
            + "We are only showing pairs if they applied to more than one bid together."
            + "We are sorting the results after the number of occurences, descending."
            + "You can use all the filters that are available along with pagination options.")
    @RequestMapping(value = "/api/frequentTenderers", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<ValueObject> frequentTenderers(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        return StreamSupport
                .stream(mongoTemplate.mapReduce(
                        new Query(where("tender.tenderers.1").exists(true)
                                .andOperator(getYearDefaultFilterCriteria(filter, "tender.tenderPeriod.startDate"))),
                        "release", "classpath:frequent-tenderers-map.js", "classpath:frequent-tenderers-reduce.js",
                        ValueObject.class).spliterator(), false)
                .filter(vo -> vo.getValue() > 1).sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                .skip(filter.getPageNumber() * filter.getPageSize()).limit(filter.getPageSize())
                .collect(Collectors.toList());
    }

}