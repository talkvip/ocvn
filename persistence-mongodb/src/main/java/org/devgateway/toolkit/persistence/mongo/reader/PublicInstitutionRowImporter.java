/**
 * 
 */
package org.devgateway.toolkit.persistence.mongo.reader;

import java.text.ParseException;

import org.devgateway.ocvn.persistence.mongo.ocds.Address;
import org.devgateway.ocvn.persistence.mongo.ocds.ContactPoint;
import org.devgateway.ocvn.persistence.mongo.ocds.Identifier;
import org.devgateway.ocvn.persistence.mongo.ocds.Organization;
import org.devgateway.toolkit.persistence.mongo.repository.OrganizationRepository;

/**
 * @author mihai
 *
 */
public class PublicInstitutionRowImporter extends RowImporter<Organization, OrganizationRepository> {

	public PublicInstitutionRowImporter(OrganizationRepository repository, int skipRows) {
		super(repository, skipRows);
	}

	@Override
	public boolean importRow(String[] row) throws ParseException {
		if (row[0] == null || row[0].isEmpty())
			throw new RuntimeException("Main identifier empty!");
		Organization organization = repository.findById(row[0]);
		if (organization != null)
			throw new RuntimeException("Duplicate identifer for organization " + organization);
		organization = new Organization();
		Identifier identifier = new Identifier();

		identifier.setId(row[0]);
		organization.setIdentifier(identifier);
		organization.setName(row[1]);

		if (row[44] != null && !row[44].isEmpty()) {
			Identifier additionalIdentifier = new Identifier();
			additionalIdentifier.setId(row[44]);
			organization.getAdditionalIdentifiers().add(additionalIdentifier);
		}

		Address address = new Address();
		address.setStreetAddress(row[14]);
		address.setPostalCode(row[13]);

		organization.setAddress(address);

		ContactPoint cp = new ContactPoint();
		cp.setName(row[5]);
		cp.setTelephone(row[7]);
		cp.setFaxNumber(row[8]);
		cp.setEmail(row[9]);
		cp.setUrl(row[18]);

		organization.setContactPoint(cp);

		documents.add(organization);

		return true;
	}

}