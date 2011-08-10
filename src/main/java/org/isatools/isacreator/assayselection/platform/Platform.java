package org.isatools.isacreator.assayselection.platform;

import org.isatools.isacreator.assayselection.AssayType;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 10/08/2011
 *         Time: 18:57
 */
public class Platform {

    private String vendor;
    private String machine;

    public Platform(String vendor, String machine) {
        this.vendor = vendor;
        this.machine = machine;
    }

    public String getVendor() {
        return vendor;
    }

    public String getMachine() {
        return machine;
    }

    @Override
    public String toString() {

        return machine.equals("") ? vendor : machine + "(" + vendor + ")";
    }
}
