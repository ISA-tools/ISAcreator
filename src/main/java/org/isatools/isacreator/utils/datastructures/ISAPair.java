package org.isatools.isacreator.utils.datastructures;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 02/05/2011
 *         Time: 11:27
 */
public class ISAPair<V, T> {

    public V fst;
    public T snd;

    public ISAPair(V fst, T snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public V getFst() {
        return fst;
    }

    public T getSnd() {
        return snd;
    }
}
