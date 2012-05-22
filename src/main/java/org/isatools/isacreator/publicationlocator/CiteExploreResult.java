package org.isatools.isacreator.publicationlocator;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 22/05/2012
 *         Time: 12:24
 */
public class CiteExploreResult {

    public String id, doi, authors, title, abstractText, affiliation;

    public CiteExploreResult(String id, String doi, String authors, String title, String abstractText, String affiliation) {
        this.id = id;
        this.doi = doi;
        this.authors = authors;
        this.title = title;
        this.abstractText = abstractText;
        this.affiliation = affiliation;
    }

    public String getId() {
        return id;
    }

    public String getDoi() {
        return doi;
    }

    public String getAuthors() {
        return authors;
    }

    public String getTitle() {
        return title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public String getAffiliation() {
        return affiliation;
    }
}
