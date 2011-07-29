package org.isatools.isacreator.autofilteringlist;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 15/07/2011
 *         Time: 13:44
 */
public interface FilterContentProvider<T> {

    public T[] getFilterContent();

}
