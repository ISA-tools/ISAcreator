package org.isatools.isacreator.orcid.model;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 23/05/2013
 * Time: 16:10
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class OrcidAuthor {

    private String orcid;
    private String givenNames;
    private String familyName;
    private String pastInstitution;
    private String currentPrimaryInstitution;
    private String currentOtherInstitution;
    private String email;

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

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(getGivenNames()+"\t");
        buffer.append(getFamilyName()+"\t");
        buffer.append(getEmail());
        return buffer.toString();
    }

}
