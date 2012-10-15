package org.isatools.isacreator.model;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 09/03/2011
 *         Time: 10:10
 */
public class StudyContact extends Contact {

    public static final String CONTACT_LAST_NAME = "Study Person Last Name";
    public static final String CONTACT_FIRST_NAME = "Study Person First Name";
    public static final String CONTACT_MID_INITIAL = "Study Person Mid Initials";
    public static final String CONTACT_EMAIL = "Study Person Email";
    public static final String CONTACT_PHONE = "Study Person Phone";
    public static final String CONTACT_FAX = "Study Person Fax";
    public static final String CONTACT_ADDRESS = "Study Person Address";
    public static final String CONTACT_AFFILIATION = "Study Person Affiliation";
    public static final String CONTACT_ROLE = "Study Person Roles";
    public static final String CONTACT_ROLE_TERM_ACCESSION = "Study Person Roles Term Accession Number";
    public static final String CONTACT_ROLE_TERM_SOURCE_REF = "Study Person Roles Term Source REF";

    public StudyContact() {
        this("", "", "", "", "", "", "", "", "");
    }

    /**
     * Contact Constructor
     *
     * @param lastName    - last name of contact
     * @param firstName   - first name of contact
     * @param midInitial  - initial(s) for contact
     * @param email       - email address
     * @param phone       - phone no
     * @param fax         - fax no
     * @param address     - address
     * @param affiliation - where they are from e.g.EBI
     * @param role        - persons role e.g. curator.
     */
    public StudyContact(String lastName, String firstName, String midInitial,
                        String email, String phone, String fax, String address,
                        String affiliation, String role) {
        this(lastName, firstName, midInitial, email,
                phone, fax, address, affiliation, role, "", "");
    }

    /**
     * Contact Constructor
     *
     * @param lastName          - last name of contact
     * @param firstName         - first name of contact
     * @param midInitial        - initial(s) for contact
     * @param email             - email address
     * @param phone             - phone no
     * @param fax               - fax no
     * @param address           - address
     * @param affiliation       - where they are from e.g.EBI
     * @param role              - persons role e.g. curator.
     * @param roleTermAccession - accession for the role term.
     * @param roleTermSourceRef - source ref for the role term.
     */
    public StudyContact(String lastName, String firstName, String midInitial,
                        String email, String phone, String fax, String address,
                        String affiliation, String role, String roleTermAccession,
                        String roleTermSourceRef) {
        super();

        fieldValues.put(CONTACT_LAST_NAME, lastName);
        fieldValues.put(CONTACT_FIRST_NAME, firstName);
        fieldValues.put(CONTACT_MID_INITIAL, midInitial);
        fieldValues.put(CONTACT_EMAIL, email);
        fieldValues.put(CONTACT_PHONE, phone);
        fieldValues.put(CONTACT_FAX, fax);
        fieldValues.put(CONTACT_ADDRESS, address);
        fieldValues.put(CONTACT_AFFILIATION, affiliation);
        fieldValues.put(CONTACT_ROLE, role);
        fieldValues.put(CONTACT_ROLE_TERM_ACCESSION, roleTermAccession);
        fieldValues.put(CONTACT_ROLE_TERM_SOURCE_REF, roleTermSourceRef);
    }

    /**
     * Returns the Contact's address.
     *
     * @return String representing the Contacts address
     */
    public String getAddress() {
        return getValue(CONTACT_ADDRESS);
    }

    /**
     * Returns the Contact's affiliation.
     *
     * @return String representing the Contacts affiliation
     */
    public String getAffiliation() {
        return getValue(CONTACT_AFFILIATION);
    }


    /**
     * Returns the Contact's email.
     *
     * @return String representing the Contacts email
     */
    public String getEmail() {
        return getValue(CONTACT_EMAIL);
    }


    /**
     * Returns the Contact's fax number.
     *
     * @return String representing the Contacts number
     */
    public String getFax() {
        return getValue(CONTACT_FAX);
    }


    /**
     * Returns the Contact's First name (forename).
     *
     * @return String representing the Contacts First name (forename)
     */
    public String getFirstName() {
        return getValue(CONTACT_FIRST_NAME);
    }

    /**
     * Returns the Contact's identifier. This method is required by the Implemented Class.
     *
     * @return String representing the Contacts First name (forename) , Last name (surname) & email address
     * @see org.isatools.isacreator.gui.StudySubData
     */
    public String getIdentifier() {
        return getFirstName() + " " + getLastName() + " " + getEmail();
    }

    /**
     * Returns the Contact's Last name (surname).
     *
     * @return String representing the Contacts Last name (surname)
     */
    public String getLastName() {
        return getValue(CONTACT_LAST_NAME);
    }

    /**
     * Returns the Contact's Mid Initial.
     *
     * @return String representing the Contacts Mid Initial
     */
    public String getMidInitial() {
        return getValue(CONTACT_MID_INITIAL);
    }

    /**
     * Returns the Contact's Phone.
     *
     * @return String representing the Contacts Phone
     */
    public String getPhone() {
        return getValue(CONTACT_PHONE);
    }

    /**
     * Returns the Contact's Role.
     *
     * @return String representing the Contacts Role
     */
    public String getRole() {
        return getValue(CONTACT_ROLE);
    }

    /**
     * Returns the Contact's Role Term Accession.
     *
     * @return String representing the Contacts Role Term Accession
     */
    public String getRoleTermAccession() {
        return getValue(CONTACT_ROLE_TERM_ACCESSION);
    }

    /**
     * Returns the Contact's Role Term Source REF.
     *
     * @return String representing the Contacts Role Term Source REF
     */
    public String getRoleTermSourceRef() {
        return getValue(CONTACT_ROLE_TERM_SOURCE_REF);
    }

    /**
     * Set the role of the contact to some String.
     *
     * @param role - the Contact's role.
     */
    public void setRole(String role) {
        fieldValues.put(CONTACT_ROLE, role);
    }
}
