package org.isatools.isacreator.orcid;

import org.isatools.isacreator.orcid.model.OrcidAuthor;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 23/05/2013
 * Time: 13:20
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public interface OrcidClient {

   public OrcidAuthor getAuthorInfo(String orcidID);

   public OrcidAuthor[] getOrcidProfiles(String searchString);

}
