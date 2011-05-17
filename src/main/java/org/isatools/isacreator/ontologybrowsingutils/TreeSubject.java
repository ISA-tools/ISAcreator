package org.isatools.isacreator.ontologybrowsingutils;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 16/05/2011
 *         Time: 15:44
 */
public interface TreeSubject {
    public void registerObserver(TreeObserver observer);

    public void unregisterObserver(TreeObserver observer);

    public void notifyObservers();
}
