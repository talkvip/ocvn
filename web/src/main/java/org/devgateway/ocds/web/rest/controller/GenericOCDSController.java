/**
 *
 */
package org.devgateway.ocds.web.rest.controller;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.GroupingFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.TextSearchRequest;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;

/**
 * @author mpostelnicu
 *
 */
public abstract class GenericOCDSController {

    private static final int LAST_DAY = 31;

    private static final int LAST_MONTH_ZERO = 11;

    protected Map<String, Object> filterProjectMap;

    protected final Logger logger = LoggerFactory.getLogger(GenericOCDSController.class);

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * Gets the date of the first day of the year (01.01.year)
     *
     * @param year
     * @return
     */
    protected Date getStartDate(final int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date start = cal.getTime();
        return start;
    }

    /**
     * Gets the date of the last date of the year (31.12.year)
     *
     * @param year
     * @return
     */
    protected Date getEndDate(final int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, LAST_MONTH_ZERO);
        cal.set(Calendar.DAY_OF_MONTH, LAST_DAY);
        Date end = cal.getTime();
        return end;
    }

    protected String ref(String field) {
        return "$" + field;
    }


    /**
     * Appends the procuring bid type id for this filter, this will fitler based
     * on tender.items.classification._id
     *
     * @param filter
     * @return the {@link Criteria} for this filter
     */
    protected Criteria getBidTypeIdFilterCriteria(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("tender.items.classification._id", filter.getBidTypeId(), filter);
    }

    protected Criteria getNotBidTypeIdFilterCriteria(final DefaultFilterPagingRequest filter) {
        return createNotFilterCriteria("tender.items.classification._id", filter.getNotBidTypeId(), filter);
    }
    

    /**
     * Creates a mongodb query for searching based on text index, sorts the results by score
     * 
     * @param request
     * @return
     */
    protected Query textSearchQuery(final TextSearchRequest request) {
        PageRequest pageRequest = new PageRequest(request.getPageNumber(), request.getPageSize());

        Query query = null;

        if (request.getText() == null) {
            query = new Query();
        } else {
            query = TextQuery.queryText(new TextCriteria().matching(request.getText())).sortByScore();
        }

        query.with(pageRequest);

        return query;
    }
    
    /**
     * Appends the tender.items.deliveryLocation._id
     *
     * @param filter
     * @return the {@link Criteria} for this filter
     */
    protected Criteria getByTenderDeliveryLocationIdentifier(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("tender.items.deliveryLocation._id",
                filter.getTenderLoc(), filter);
    }

    /**
     * Creates a search criteria filter based on tender.value.amount and uses
     * {@link DefaultFilterPagingRequest#getMinTenderValue()} and
     * {@link DefaultFilterPagingRequest#getMaxTenderValue()} to create
     * interval search
     *
     * @param filter
     * @return
     */
    private Criteria getByTenderAmountIntervalCriteria(final DefaultFilterPagingRequest filter) {
        if (filter.getMaxTenderValue() == null && filter.getMinTenderValue() == null) {
            return new Criteria();
        }
        Criteria criteria = where("tender.value.amount");
        if (filter.getMinTenderValue() != null) {
            criteria = criteria.gte(filter.getMinTenderValue().doubleValue());
        }
        if (filter.getMaxTenderValue() != null) {
            criteria = criteria.lte(filter.getMaxTenderValue().doubleValue());
        }
        return criteria;
    }

    /**
     * Creates a search criteria filter based on awards.value.amount and uses
     * {@link DefaultFilterPagingRequest#getMinAwardValue()} and
     * {@link DefaultFilterPagingRequest#getMaxAwardValue()} to create
     * interval search
     *
     * @param filter
     * @return
     */
    private Criteria getByAwardAmountIntervalCriteria(final DefaultFilterPagingRequest filter) {
        if (filter.getMaxAwardValue() == null && filter.getMinAwardValue() == null) {
            return new Criteria();
        }
        Criteria criteria = where("awards.value.amount");
        if (filter.getMinAwardValue() != null) {
            criteria = criteria.gte(filter.getMinAwardValue().doubleValue());
        }
        if (filter.getMaxAwardValue() != null) {
            criteria = criteria.lte(filter.getMaxAwardValue().doubleValue());
        }
        return criteria;
    }


    /**
     * Appends the contrMethod filter, based on tender.contrMethod
     *
     * @param filter
     * @return the {@link Criteria} for this filter
     */
    protected Criteria getContrMethodFilterCriteria(final DefaultFilterPagingRequest filter) {
        return filter.getContrMethod() == null ? new Criteria()
                : createFilterCriteria("tender.contrMethod._id",
                filter.getContrMethod().stream().map(s -> new ObjectId(s)).collect(Collectors.toList()),
                filter);
    }


    private <S> Criteria createFilterCriteria(final String filterName, final List<S> filterValues,
                                              final DefaultFilterPagingRequest filter) {
        if (filterValues == null) {
            return new Criteria();
        }
        return where(filterName).in(filterValues.toArray());
    }

    private <S> Criteria createNotFilterCriteria(final String filterName, final List<S> filterValues,
            final DefaultFilterPagingRequest filter) {
        if (filterValues == null) {
            return new Criteria();
        }
        return where(filterName).not().in(filterValues.toArray());
    }

    /**
     * Appends the procuring entity id for this filter, this will fitler based
     * on tender.procuringEntity._id
     *
     * @param filter
     * @return the {@link Criteria} for this filter
     */
    protected Criteria getProcuringEntityIdCriteria(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("tender.procuringEntity._id", filter.getProcuringEntityId(), filter);
    }
    
    protected Criteria getProcuringEntityGroupIdCriteria(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("tender.procuringEntity.group._id", filter.getProcuringEntityGroupId(), filter);
    }
    
    protected Criteria getProcuringEntityDepartmentIdCriteria(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("tender.procuringEntity.department._id", 
                filter.getProcuringEntityDepartmentId(), filter);
    }
    
    protected Criteria getProcuringEntityCityIdCriteria(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("tender.procuringEntity.address.postalCode", 
                filter.getProcuringEntityCityId(), filter);
    }

    protected Criteria getNotProcuringEntityIdCriteria(final DefaultFilterPagingRequest filter) {
        return createNotFilterCriteria("tender.procuringEntity._id", filter.getNotProcuringEntityId(), filter);
    }

    
    
    /**
     * Appends the supplier entity id for this filter, this will fitler based
     * on tender.procuringEntity._id
     *
     * @param filter
     * @return the {@link Criteria} for this filter
     */
    protected Criteria getSupplierIdCriteria(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("awards.suppliers._id", filter.getSupplierId(), filter);
    }

    @PostConstruct
    protected void init() {
        Map<String, Object> tmpMap = new HashMap<>();
        tmpMap.put("tender.procuringEntity._id", 1);
        tmpMap.put("tender.procuringEntity.group._id", 1);
        tmpMap.put("tender.procuringEntity.department._id", 1);
        tmpMap.put("tender.procuringEntity.address.postalCode", 1);
        tmpMap.put("awards.suppliers._id", 1);
        tmpMap.put("tender.items.classification._id", 1);
        tmpMap.put("tender.items.deliveryLocation._id", 1);
        tmpMap.put("tender.procurementMethodDetails", 1);
        tmpMap.put("tender.contrMethod", 1);
        tmpMap.put("tender.value.amount", 1);
        tmpMap.put("awards.value.amount", 1);
        filterProjectMap = Collections.unmodifiableMap(tmpMap);
    }

    protected Criteria getYearFilterCriteria(final YearFilterPagingRequest filter, final String dateProperty) {
        Criteria[] yearCriteria = null;
        Criteria criteria = new Criteria();

        if (filter.getYear() == null) {
            yearCriteria = new Criteria[1];
            yearCriteria[0] = new Criteria();
        } else {
            yearCriteria = new Criteria[filter.getYear().size()];
            for (int i = 0; i < filter.getYear().size(); i++) {
                yearCriteria[i] = where(dateProperty).gte(getStartDate(filter.getYear().get(i)))
                        .lte(getEndDate(filter.getYear().get(i)));
            }
        }

        return criteria.orOperator(yearCriteria);
    }

    /**
     * Appends the bid selection method to the filter, this will filter based on
     * tender.procurementMethodDetails. It accepts multiple elements
     *
     * @param filter
     * @return the {@link Criteria} for this filter
     */
    protected Criteria getBidSelectionMethod(final DefaultFilterPagingRequest filter) {
        return createFilterCriteria("tender.procurementMethodDetails", filter.getBidSelectionMethod(), filter);
    }
    
    protected Criteria getNotBidSelectionMethod(final DefaultFilterPagingRequest filter) {
        return createNotFilterCriteria("tender.procurementMethodDetails", filter.getNotBidSelectionMethod(), filter);
    }


    protected Criteria getDefaultFilterCriteria(final DefaultFilterPagingRequest filter) {
        return new Criteria().andOperator(
                getBidTypeIdFilterCriteria(filter), 
                getNotBidTypeIdFilterCriteria(filter),
                getProcuringEntityIdCriteria(filter),
                getNotProcuringEntityIdCriteria(filter),
                getProcuringEntityCityIdCriteria(filter),
                getProcuringEntityGroupIdCriteria(filter),
                getProcuringEntityDepartmentIdCriteria(filter),
                getBidSelectionMethod(filter), 
                getContrMethodFilterCriteria(filter),
                getSupplierIdCriteria(filter),
                getByTenderDeliveryLocationIdentifier(filter), 
                getByTenderAmountIntervalCriteria(filter),
                getByAwardAmountIntervalCriteria(filter));
    }

    protected Criteria getYearDefaultFilterCriteria(final YearFilterPagingRequest filter, final String dateProperty) {
        return new Criteria().andOperator(
                getBidTypeIdFilterCriteria(filter), 
                getNotBidTypeIdFilterCriteria(filter),
                getProcuringEntityIdCriteria(filter),
                getNotProcuringEntityIdCriteria(filter),
                getProcuringEntityCityIdCriteria(filter),
                getProcuringEntityGroupIdCriteria(filter),
                getProcuringEntityDepartmentIdCriteria(filter),
                getBidSelectionMethod(filter), 
                getNotBidSelectionMethod(filter), 
                getContrMethodFilterCriteria(filter),
                getSupplierIdCriteria(filter),
                getByTenderDeliveryLocationIdentifier(filter), 
                getByTenderAmountIntervalCriteria(filter),
                getYearFilterCriteria(filter, dateProperty));
    }

    protected MatchOperation getMatchDefaultFilterOperation(final DefaultFilterPagingRequest filter) {
        return match(getDefaultFilterCriteria(filter));
    }

    /**
     * Creates a groupby expression that takes into account the filter. It will
     * only use one of the filter options as groupby and ignores the rest.
     *
     * @param filter
     * @param existingGroupBy
     * @return
     */
    protected GroupOperation getTopXFilterOperation(final GroupingFilterPagingRequest filter,
                                                    final String... existingGroupBy) {
        List<String> groupBy = new ArrayList<>();
        if (filter.getGroupByCategory() == null) {
            groupBy.addAll(Arrays.asList(existingGroupBy));
        }

        if (filter.getGroupByCategory() != null) {
            groupBy.add(getGroupByCategory(filter));
        }

        return group(groupBy.toArray(new String[0]));
    }

    private String getGroupByCategory(final GroupingFilterPagingRequest filter) {
        if ("bidSelectionMethod".equals(filter.getGroupByCategory())) {
            return "tender.procurementMethodDetails".replace(".", "");
        } else if ("bidTypeId".equals(filter.getGroupByCategory())) {
            return "tender.items.classification._id".replace(".", "");
        } else if ("procuringEntityId".equals(filter.getGroupByCategory())) {
            return "tender.procuringEntity._id".replace(".", "");
        }
        return null;
    }

}
