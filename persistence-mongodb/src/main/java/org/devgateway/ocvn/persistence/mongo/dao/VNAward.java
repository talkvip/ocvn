/**
 *
 */
package org.devgateway.ocvn.persistence.mongo.dao;

import org.devgateway.ocds.persistence.mongo.Amount;
import org.devgateway.ocds.persistence.mongo.Award;

/**
 * @author mihai
 * Extension of {@link Award} to allow extra Vietnam-specific fields
 */
public class VNAward extends Award {
    private String inelibigleYN;

    private String ineligibleRson;

    private Integer bidOpenRank;

    private Integer bidType;

    private Integer bidSuccMethod;

    private Amount bidPrice;

    private String contractTime;

    public String getInelibigleYN() {
        return inelibigleYN;
    }

    public void setInelibigleYN(String inelibigleYN) {
        this.inelibigleYN = inelibigleYN;
    }

    public Integer getBidOpenRank() {
        return bidOpenRank;
    }

    public void setBidOpenRank(Integer bidOpenRank) {
        this.bidOpenRank = bidOpenRank;
    }

    public Integer getBidType() {
        return bidType;
    }

    public void setBidType(Integer bidType) {
        this.bidType = bidType;
    }

    public Integer getBidSuccMethod() {
        return bidSuccMethod;
    }

    public void setBidSuccMethod(Integer bidSuccMethod) {
        this.bidSuccMethod = bidSuccMethod;
    }

    public Amount getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Amount bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getContractTime() {
        return contractTime;
    }

    public void setContractTime(String contractTime) {
        this.contractTime = contractTime;
    }

    public String getIneligibleRson() {
        return ineligibleRson;
    }

    public void setIneligibleRson(String ineligibleRson) {
        this.ineligibleRson = ineligibleRson;
    }

}