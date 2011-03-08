package org.isatools.isacreator.utils.datastructures;

import com.sun.tools.javac.util.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 08/03/2011
 *         Time: 10:18
 */
public class SetUtils<T> {

    public Pair<Boolean, Set<T>> compareSets(Set<T> setA, Set<T> setB, boolean enforceSizeMatch) {

        Pair<Boolean, Set<T>> result = new Pair<Boolean, Set<T>>(false, new HashSet<T>());

        for (T value : setB) {
            if (!setA.contains(value)) {
                result.snd.add(value);
            }
        }

        if (result.snd.size() > 0) {
            return result;
        } else {
            if (enforceSizeMatch) {
                return new Pair<Boolean, Set<T>>(setA.size() == setB.size(), null);
            }
            return new Pair<Boolean, Set<T>>(true, null);
        }
    }
}
