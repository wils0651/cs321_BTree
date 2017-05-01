import java.util.LinkedList;
import java.util.ListIterator;



/**
 * 
 * Methods to create and maintain a Cache object
 * 
 * 
 * @param <BTree.BTreeNode>
 *
 */


public class Cache <BTreeNode>{

	private int size;
	private LinkedList<BTree.BTreeNode> data;


	/**
	 * Cache constructor method
	 * 
	 * @param size
	 */
	public Cache(int size) {
		this.setSize(size);
		data = new LinkedList<>();
	}

	/**
	 * returns object at index
	 * 
	 * @param index to return
	 * @return object at index
	 */
	public BTree.BTreeNode getObject(int index){
		return data.get(index);
	}


	/**
	 * Same as AddObject - with remove if already in cache
	 * 
	 * -Also removes objects outside of cache size from bottom 
	 * 
	 * @param target
	 */
	public void writeNode(BTree.BTreeNode target){
		data.remove(target); //removes target if already in list

		data.addFirst(target);

		if (data.size() > size()){
			data.removeLast();
		}
	}

	public BTree.BTreeNode removeFirst(){
		if(data.size() > 0){
			return data.removeFirst();
		}
		return null;
	}


	/**
	 * Adds object to top of cache
	 * -Also removes objects outside of cache size from bottom 
	 * 
	 * @param target
	 */
	public BTree.BTreeNode addObject(BTree.BTreeNode target){
		if(data.contains(target)){
			data.remove(target);
		}
		data.addFirst(target);
		
		if (data.size() > size()){
			return data.removeLast();
		}
		return null;
	}

	/**
	 * removes object from cache
	 * 
	 * @param target - object to remove
	 * @return true if object found
	 */
	public boolean removeObject(BTree.BTreeNode target){
		return data.remove(target);
	}


	/**
	 * removes object from current position in cache and moves it to top
	 * 
	 * @param target
	 * @return True of object found in cache
	 */
	public boolean moveToTop(BTree.BTreeNode target){
		boolean contains = data.remove(target);
		if(contains){
			addObject(target);
		}
		return contains;
	}


	/**
	 * clears data from cache
	 */
	public void clearCache(){
		data.clear();
	}


	/**
	 * Finds index of object in cache
	 * 
	 * @param target to find
	 * @return index of object if found, or -1 if not found
	 */
	public int indexOf(BTree.BTreeNode target){
		return data.indexOf(target);
	}


	/**
	 * @return the size of the cache object
	 */
	public int size() {
		return size;
	}

	/**
	 * @param size - the size of the cache
	 */
	public void setSize(int size) {
		this.size = size;
	}

	//	public String toString(){
	//		String s = "";
	//		BTree.BTreeNode node;
	//		for (int i = 0; i < data.size(); i++){
	//			node = data.get(i);
	//			s+= "node# " + node.getNodeNumber() + " : " + node + "\n ";
	//		}
	//		
	//		return s;
	//		
	//	}

}
