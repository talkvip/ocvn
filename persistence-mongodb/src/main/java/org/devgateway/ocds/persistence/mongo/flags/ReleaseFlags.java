/**
 *
 */
package org.devgateway.ocds.persistence.mongo.flags;

/**
 * @author mpostelnicu Represents the list of red flags at the Release level
 */
public class ReleaseFlags implements FlagsWrappable {

    // i038: Competitive tender w/ short bidding period
    private Flag i038;

    // i003: Only winning bidder was eligible for a tender that had multiple
    // bidders.
    private Flag i003;

    //i007 This awarded competitive tender only featured a single bid`
    private Flag i007;

    public Flag getI038() {
        return i038;
    }

    public void setI038(Flag i038) {
        this.i038 = i038;
    }

    public Flag getI003() {
        return i003;
    }

    public void setI003(Flag i003) {
        this.i003 = i003;
    }

    public Flag getI007() {
        return i007;
    }

    public void setI007(Flag i007) {
        this.i007 = i007;
    }
}
