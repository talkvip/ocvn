package org.devgateway.ocvn.web.rest.controller;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocvn.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 
 * @author mpostelnicu
 *
 */
@RestController
public class TenderPriceByTypeYearController extends GenericOcvnController {

	@RequestMapping(value = "/api/tenderPriceByOcdsTypeYear", method = RequestMethod.GET,
			 produces = "application/json")
	public List<DBObject> tenderPriceByOcdsTypeYear(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

		DBObject project = new BasicDBObject();
		project.put("year", new BasicDBObject("$year", "$tender.tenderPeriod.endDate"));
		project.put("tender.procurementMethod", 1);
		project.put("tender.value", 1);

		Aggregation agg = newAggregation(
				match(where("awards").elemMatch(where("status").is("active")).and("tender.value").exists(true)),
				getMatchDefaultFilterOperation(filter), new CustomProjectionOperation(project),
				group("year", "tender.procurementMethod").sum("$tender.value.amount").as("totalTenderAmount"),
				sort(Direction.DESC, "totalTenderAmount"), skip(filter.getSkip()), limit(filter.getPageSize()));

		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
		List<DBObject> tagCount = results.getMappedResults();
		return tagCount;

	}

	@RequestMapping(value = "/api/tenderPriceByVnTypeYear", method = RequestMethod.GET,
			 produces = "application/json")
	public List<DBObject> tenderPriceByVnTypeYear(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

		DBObject project = new BasicDBObject();
		project.put("year", new BasicDBObject("$year", "$tender.tenderPeriod.endDate"));
		project.put("tender.procurementMethodDetails", 1);
		project.put("tender.value", 1);

		Aggregation agg = newAggregation(
				match(where("awards").elemMatch(where("status").is("active")).and("tender.value").exists(true)),
				getMatchDefaultFilterOperation(filter), new CustomProjectionOperation(project),
				group("year", "tender.procurementMethodDetails").sum("$tender.value.amount").as("totalTenderAmount"),
				sort(Direction.ASC, "year"));

		AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
		List<DBObject> tagCount = results.getMappedResults();
		return tagCount;

	}

}