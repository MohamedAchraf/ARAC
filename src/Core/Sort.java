
package Core;

import java.util.*;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class Sort {
    
    /**
     * 
     * @param unsortMap
     * @return sorted Map
     */  
    public Map sortMapByValues(Map unsortMap) {
 
        List list = new LinkedList(unsortMap.entrySet());
 
        //sort list based on comparator
        Collections.sort(list, new Comparator() {            
             @Override
             public int compare(Object o1, Object o2) {
	           return ((Comparable) ((Map.Entry) (o2)).getValue())
	           .compareTo(((Map.Entry) (o1)).getValue());
             }
	});
 
        //put sorted list into map again
	Map sortedMap = new LinkedHashMap();
	for (Iterator it = list.iterator(); it.hasNext();) {
	     Map.Entry entry = (Map.Entry)it.next();
	     sortedMap.put(entry.getKey(), entry.getValue());
	}
        
       
	return sortedMap;
        
   }
    
    
}
