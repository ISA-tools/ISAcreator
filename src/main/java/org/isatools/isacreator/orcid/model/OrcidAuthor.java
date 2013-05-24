package org.isatools.isacreator.orcid.model;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 23/05/2013
 * Time: 16:10
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class OrcidAuthor {//extends Contact {

    private String orcid;

    //first name
    private String givenNames;
    //last name
    private String familyName;
    private String email;

    private String pastInstitution;
    private String currentPrimaryInstitution;
    private String currentOtherInstitution;



    public OrcidAuthor(){

    }



    public String getOrcid(){
        return orcid;
    }

    public String getGivenNames(){
        return givenNames;
    }

    public String getFamilyName(){
        return familyName;
    }

    public String getPastInstitution(){
        return pastInstitution;
    }

    public String getCurrentPrimaryInstitution(){
        return currentPrimaryInstitution;
    }

    public String getCurrentOtherInstitution(){
        return currentOtherInstitution;
    }

    public String getEmail(){
        return email;
    }


    public void setOrcid(String orcid){
        this.orcid = orcid;
    }

    public void setGivenNames(String givenNames){
        this.givenNames = givenNames;
    }

    public void setFamilyName(String familyName){
        this.familyName = familyName;
    }

    public void setCurrentPrimaryInstitution(String currentPrimaryInstitution){
        this.currentPrimaryInstitution = currentPrimaryInstitution;
    }

    public void setEmail(String email){
        this.email = email;
    }



    public String getIdentifier() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String toString(){
        return getGivenNames()+" "+getFamilyName();
    }
}
