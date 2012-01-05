/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 “The contents of this file are subject to the CPAL version 1.0 (the “License”);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an “AS IS” basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */

package org.isatools.isacreator.ontologymanager;

import java.util.HashMap;


/**
 * implementation of the HashMap class to provide a way of maintaining the objects held in the cache so that
 * the list does not become unwieldly. Maintains list size of 25 items which will contain mapping of recent searches
 * to results so that queries performed a lot can be cached effectively in a session and the load is reduced on the
 * server for frequent searches.
 *
 * @author Eamonn Maguire
 * @date Jun 8, 2008
 */
public class ResultCache<V, T> extends HashMap<V, T> {
    // store 25 most recently used elements
    private static final int SIZE = 25;

    private HashMap<V, Long> cacheStats = new HashMap<V, Long>();

    public ResultCache() {
        super(SIZE);
    }

    /**
     * Add mapping to cache
     *
     * @param key   - V containing key for access to cache e.g. all:mitosis
     * @param value - The result of the query as a T.
     */
    public void addToCache(V key, T value) {
        if ((size() + 1) > SIZE) {
            V indexToRemove = returnIndexForRemoval();
            remove(indexToRemove);
            cacheStats.remove(indexToRemove);
        }

        put(key, value);
        cacheStats.put(key, System.currentTimeMillis());
    }

    public T retrieveFromCache(V key) {
        // when we get the get the object, we also need to update the last time the object was accessed.
        cacheStats.put(key, System.currentTimeMillis());

        return get(key);
    }

    /**
     * Returns key of item in index which has not been accessed in the longest period of time.
     *
     * @return String containing key of item for removal.
     */
    private V returnIndexForRemoval() {
        // to get the item which has not been accessed in the longest period of time, we need to get the item with
        // the smallest millisLastAccessed variable, since this will be the item accessed further away from the current
        // time.
        Long longestTimeSinceQuery = Long.MAX_VALUE;
        V retVal = null;

        for (V cacheKeys : cacheStats.keySet()) {
            Long timeForCacheObject = cacheStats.get(cacheKeys);

            if (timeForCacheObject < longestTimeSinceQuery) {
                longestTimeSinceQuery = timeForCacheObject;
                retVal = cacheKeys;
            }
        }

        return retVal;
    }

    public void clearCache() {
        cacheStats.clear();
        clear();
    }
}
