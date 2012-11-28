package org.isatools.isacreator.api;

import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.io.UserProfile;
import org.isatools.isacreator.spreadsheet.Spreadsheet;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 08/10/2012
 * Time: 17:02
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public interface Authentication {

    public boolean login(String username, char[] password);

    public boolean logout(String username);

    //to support single sign on
    public boolean login(String username);


}
