package org.devgateway.ocds.persistence.mongo.flags.processors.release.vietnam;

import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.Flag;
import org.devgateway.ocds.persistence.mongo.flags.ReleaseFlags;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.FlaggedReleasePredicates;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.NamedPredicate;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.VietnamFlaggedReleasePredicates;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author mpostelnicu
 * 
 * i007 This awarded competitive tender only featured a single bid
 */
public class ReleaseFlagI007Processor extends AbstractFlaggedReleaseFlagProcessor {

    public static final ReleaseFlagI007Processor INSTANCE = new ReleaseFlagI007Processor();

    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        if (flaggable.getFlags() == null) {
            flaggable.setFlags(new ReleaseFlags());
        }
        flaggable.getFlags().setI007(flag);
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {
        long countAwards = flaggable.getAwards().size();

        rationale.append("Number of bids: ").append(countAwards);
        return countAwards == 1;
    }

    @Override
    protected Collection<NamedPredicate<FlaggedRelease>> getPreconditionsPredicates() {
        return Collections.unmodifiableList(Arrays.asList(FlaggedReleasePredicates.ACTIVE_AWARD,
                FlaggedReleasePredicates.OPEN_PROCUREMENT_METHOD,
                VietnamFlaggedReleasePredicates.ELECTRONIC_SUBMISSION));
    }

}