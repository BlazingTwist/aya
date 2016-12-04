package element.entities.operations;

import java.util.ArrayList;

import element.ElemTypes;
import element.entities.number.Numeric;

public class ListOperations {

	/**
	 * Given a list of indices, remove all indices from the object list
	 * @param list
	 * @param indices
	 */
	public static void removeIndices(ArrayList<Object> list, ArrayList<Numeric> indices) {
		int size = list.size();
		
		for (int i = 0; i < indices.size(); i++) {
			list.set(indices.get(i).toIndex(size), null);
		}
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == null) {
				list.remove(i);
				i--;
			}
		}
		
	}
	
	/**
	 * Remove all items in objects from list. Set minus.
	 * @param list
	 * @param objects
	 */
	public static void removeObjects(ArrayList<Object> list, ArrayList<Object> objects) {		
		for (Object key : objects) {
			for (int i = 0; i < list.size(); i++) {
				if (ElemTypes.areEqual(list.get(i), key)) {
					list.remove(i);
					i--;
				}
			}
		}
	}
	
	/**
	 * Remove duplicates from a list (rough algorithm)
	 * TODO: This algorithm can be simplified when all types implement .equals 
	 * @param list
	 */
	public static ArrayList<Object> removeDuplicates(ArrayList<Object> list) {
		ArrayList<Object> unique = new ArrayList<Object>();
		for (Object l : list) {
			boolean alreadyContains = false;
			for (Object u : unique) {
				if (ElemTypes.areEqual(l, u)) {
					alreadyContains = true;
					break;
				}
			}
			if (!alreadyContains) {
				unique.add(l);
			}
		}
		return unique;
	}

}
