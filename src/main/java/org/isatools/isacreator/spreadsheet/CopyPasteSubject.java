package org.isatools.isacreator.spreadsheet;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 22/11/2011
 *         Time: 08:45
 */
public interface CopyPasteSubject {

    public void registerCopyPasteObserver(CopyPasteObserver observer);

    public void removeCopyPasteObserver(CopyPasteObserver observer);

    public void notifyObservers(SpreadsheetEvent event);
}
