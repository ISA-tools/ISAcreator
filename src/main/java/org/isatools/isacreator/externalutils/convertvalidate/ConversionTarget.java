package org.isatools.isacreator.externalutils.convertvalidate;

import org.isatools.isatab.gui_invokers.AllowedConversions;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 08/09/2011
 *         Time: 13:54
 */
public class ConversionTarget {

    private AllowedConversions target;
    private int numValidAssays;
    private boolean selected;

    public ConversionTarget(AllowedConversions target, int numValidAssays, boolean selected) {
        this.target = target;
        this.numValidAssays = numValidAssays;
        this.selected = selected;
    }

    public AllowedConversions getTarget() {
        return target;
    }

    public int getNumValidAssays() {
        return numValidAssays;
    }

    public int incrementNumValidAssays() {
        numValidAssays++;
        return numValidAssays;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
