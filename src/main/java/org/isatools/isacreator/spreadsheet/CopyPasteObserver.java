package org.isatools.isacreator.spreadsheet;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 22/11/2011
 *         Time: 08:43
 */
public interface CopyPasteObserver {

    public void notifyOfEvent(SpreadsheetEvent event);
}
