package org.isatools.isacreator.effects;

/**
 * Check if os is windows or mac. Linux has problems with transparency
 */
public class GraphicsUtils {

    public static boolean isWindowTransparencySupported() {
        String os = System.getProperty("os.name").toLowerCase();

        return !os.contains("linux");
    }
}
