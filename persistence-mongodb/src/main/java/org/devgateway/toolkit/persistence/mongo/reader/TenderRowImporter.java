package org.devgateway.toolkit.persistence.mongo.reader;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.poi.ss.usermodel.DateUtil;
import org.devgateway.ocvn.persistence.mongo.ocds.Identifier;
import org.devgateway.ocvn.persistence.mongo.ocds.Organization;
import org.devgateway.ocvn.persistence.mongo.ocds.Period;
import org.devgateway.ocvn.persistence.mongo.ocds.Release;
import org.devgateway.ocvn.persistence.mongo.ocds.Value;
import org.devgateway.toolkit.persistence.mongo.dao.VNPlanning;
import org.devgateway.toolkit.persistence.mongo.dao.VNTender;
import org.devgateway.toolkit.persistence.mongo.repository.ReleaseRepository;

public class TenderRowImporter extends RowImporter {

	SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM.yy", new Locale("en"));

	public TenderRowImporter(ReleaseRepository releaseRepository, int skipRows) {
		super(releaseRepository, skipRows);
	}

	@Override
	public boolean importRow(String[] row) throws ParseException {

		Release release = releaseRepository.findByPlanningBidNo(row[0]);

		if (release == null) {
			release = new Release();
			VNPlanning planning = new VNPlanning();
			release.setPlanning(planning);
			planning.setBidNo(row[0]);
		}
		releases.add(release);

		VNTender tender = (VNTender) release.getTender();
		if (tender == null) {
			tender = new VNTender();
			release.setTender(tender);
		}

		String status = null;
		if (row[1].equals("Y") && (row[2].equals("N") || row[2].isEmpty()) && (row[3].equals("N") || row[3].isEmpty()))
			status = "active";

		if (row[1].isEmpty() && (row[2].isEmpty()) && (row[3].isEmpty()))
			status = "planned";

		if (row[1].isEmpty() && (row[2].equals("N")) && (row[3].equals("N") || row[3].isEmpty()))
			status = "planned";

		if (row[1].equals("Y") && (row[2].equals("Y")) && (row[3].equals("N") || row[3].isEmpty()))
			status = "cancelled";

		if (row[1].isEmpty() && (row[2].equals("Y")) && (row[3].equals("N") || row[3].isEmpty()))
			status = "cancelled";
		tender.setStatus(status);
		tender.setApproveState(row[1]);
		tender.setCancelYN(row[2]);
		tender.setModYn(row[3]);
		tender.setBidMethod(Integer.parseInt(row[4]));

		String procurementMethod = null;
		switch (Integer.parseInt(row[5])) {
		case 1:
			procurementMethod = "open";
			break;
		case 2:
			procurementMethod = "selective";
			break;
		case 3:
			procurementMethod = "limited";
			break;
		case 4:
			procurementMethod = "limited";
			break;
		case 5:
			procurementMethod = "open";
			break;
		case 6:
			procurementMethod = "limited";
			break;
		case 7:
			procurementMethod = "selective";
			break;

		}
		tender.setProcurementMethod(procurementMethod);
		tender.setContrMethod(Integer.parseInt(row[6]));

		Period period = new Period();

		period.setStartDate(row[7].isEmpty() ? null : DateUtil.getJavaCalendar(Double.parseDouble(row[7])).getTime());
		period.setEndDate(row[8].isEmpty() ? null : DateUtil.getJavaCalendar(Double.parseDouble(row[8])).getTime());
		tender.setTenderPeriod(period);
		tender.setBidOpenDt(row[9].isEmpty() ? null : DateUtil.getJavaCalendar(Double.parseDouble(row[9])).getTime());

		Organization procuringEntity = new Organization();
		Identifier procuringEntityIdentifier = new Identifier();
		procuringEntityIdentifier.setId(row[10]);
		procuringEntity.setIdentifier(procuringEntityIdentifier);
		tender.setProcuringEntity(procuringEntity);

		Organization orderInstituCd = new Organization();
		Identifier orderInstituCdIdentifier = new Identifier();
		orderInstituCdIdentifier.setId(row[11]);
		orderInstituCd.setIdentifier(orderInstituCdIdentifier);
		tender.setOrderIntituCd(orderInstituCd);

		if (row.length > 12) {
			Value value = new Value();
			value.setCurrency("VND");
			value.setAmount(new BigDecimal(row[12]));
			tender.setValue(value);
		}

		return true;
	}
}