/**
 *
 */
package org.devgateway.toolkit.persistence.mongo.aggregate;

import com.mongodb.BasicDBObject;

/**
 * @author mpostelnicu
 *
 */
public class CustomUnwindOperation extends CustomOperation {

    /**
     * @param operation
     */
    public CustomUnwindOperation(final String field) {
        super(new BasicDBObject("$unwind", field));
    }

}
