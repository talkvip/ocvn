package org.devgateway.ocvn.persistence.mongo.reader;

import org.devgateway.ocds.persistence.mongo.Amount;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.Tag;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.persistence.mongo.reader.ReleaseRowImporter;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.VNBudget;
import org.devgateway.ocvn.persistence.mongo.dao.VNLocation;
import org.devgateway.ocvn.persistence.mongo.dao.VNPlanning;
import org.devgateway.ocvn.persistence.mongo.repository.VNLocationRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * @author mihai Specific {@link RowImporter} for Procurement Plans, in the
 *         custom Excel format provided by Vietnam
 * @see VNPlanning
 */
public class ProcurementPlansRowImporter extends ReleaseRowImporter {
    private VNLocationRepository locationRepository;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", new Locale("en"));

    public ProcurementPlansRowImporter(final ReleaseRepository releaseRepository, final ImportService importService,
                                       final VNLocationRepository locationRepository, final int skipRows) {
        super(releaseRepository, importService, skipRows);
        this.locationRepository = locationRepository;
    }

    @Override
    public Release createReleaseFromReleaseRow(final String[] row) throws ParseException {

        String projectID = row[0];
        Release oldRelease = repository.findByBudgetProjectId(projectID);
        if (oldRelease != null) {
            throw new RuntimeException("Duplicate planning.budget.projectID");
        }

        Release release = new Release();
        release.setOcid(MongoConstants.OCDS_PREFIX + "prjid-" + projectID);
        release.getTag().add(Tag.planning);
        VNPlanning planning = new VNPlanning();
        VNBudget budget = new VNBudget();
        release.setPlanning(planning);
        planning.setBudget(budget);

        // search for locations

        String[] locations = row[3].split(",");
        for (int i = 0; i < locations.length; i++) {
            VNLocation location = locationRepository.findByDescription(locations[i].trim());
            if (location == null) {
                location = new VNLocation();
                location.setDescription(locations[i]);
            }

            budget.getProjectLocation().add(location);
        }

        planning.setBidPlanProjectDateIssue(row[4].isEmpty() ? null : getDateFromString(sdf, row[4]));

        planning.setBidPlanProjectCompanyIssue(row[6]);

        planning.setBidPlanProjectFund(getInteger(row[8]));
        if (!row[9].trim().isEmpty()) {
            budget.getProjectClassification().setDescription(row[9].trim());
        }
        planning.setBidPlanProjectDateApprove(row[10].isEmpty() ? null : getDateFromString(sdf, row[10]));
        budget.getProjectClassification().setId(row[12]);
        if (row.length > 13) {
            planning.setBidNo(row[13]);
        }

        budget.setProjectID(row[0]);
        budget.setBidPlanProjectStyle(row[5]);
        budget.setBidPlanProjectType(row[7]);
        budget.setProject(row[1]);
        budget.setDescription(row[11]);

        Amount value = new Amount();
        budget.setAmount(value);
        value.setCurrency("VND");
        value.setAmount(getDecimal(row[2]));
        return release;
    }
}