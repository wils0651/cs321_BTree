import java.util.LinkedList;



/**
 * A simple cache object for storing strings that can be configured to have one or two caches.
 * @author Tim Wilson
 *
 */
public class Cache {
	private static long NR1;
	private static long NR2;
	private static long NH1;
	private static long NH2;
	private static LinkedList<Object> myCache1;
	private static LinkedList<Object> myCache2;
	private static boolean isDoubleLevel = false; //single or double level cache, default single
	private static int cacheSize1;
	private static int cacheSize2;
	
	/**
	 * Constructor for single level cache 
	 * @param cSize1 size of cache
	 */
	public Cache(int cSize1){
		cacheSize1 = cSize1;
		myCache1 = new LinkedList<Object>();
		NR1 =0;
		NH1 =0;
		System.out.print("First level cache with ");
		System.out.print(cSize1);
		System.out.println(" entries has been created");
		isDoubleLevel = false;
	}
	
	/**
	 * Constructor for 2 level cache 
	 * @param cSize1 size of first level cache
	 * @param cSize2 size of second level cache
	 */
	public Cache(int cSize1, int cSize2){
		cacheSize1 = cSize1;
		cacheSize2 = cSize2;
		myCache1 = new LinkedList<Object>();
		myCache2 = new LinkedList<Object>();
		NR1 =0;
		NR2 =0;
		NH1 =0;
		NH2 =0;
		System.out.print("First level cache with ");
		System.out.print(cSize1);
		System.out.println(" entries has been created");
		System.out.print("Second level cache with ");
		System.out.print(cSize2);
		System.out.println(" entries has been created");
		isDoubleLevel = true;
	}

	/**
	 * Checks if an object (String) is in the cache
	 * @param aWord the string to be searched for in the cache
	 * @return the string (aWord) or "not in cache" if not in the cache
	 */
	public Object getObject(Object aWord) {
		Object returnVal = null;
		if(myCache1.contains(aWord) || myCache2.contains(aWord)){
			returnVal = aWord;
		}
		return returnVal;
	}
	
	/**
	 * adds an object to the cache
	 * @param bTreeNode the String object to be added to the cache
	 */
	public static void addObject(Object bTreeNode){
		NR1++;
		if(myCache1.contains(bTreeNode)){	//word is in 1st cache
			NH1++;
			myCache1.remove(bTreeNode);
			myCache1.addFirst(bTreeNode);
			if(isDoubleLevel){
				myCache2.remove(bTreeNode);
				myCache2.addFirst(bTreeNode);
			}
		}else if(myCache1.size() >= cacheSize1){	//word is not in cache 1, cache1 is full
			myCache1.remove(cacheSize1-1);
			myCache1.addFirst(bTreeNode);
			if(isDoubleLevel){
				NR2++;
				if(myCache2.contains(bTreeNode)){		//word is in cache2
					NH2++;
					myCache2.remove(bTreeNode);
					myCache2.addFirst(bTreeNode);				
				}else if(myCache2.size() >= cacheSize2){	//word is not in cache2, cache2 is full
					myCache2.remove(cacheSize2-1);
					myCache2.addFirst(bTreeNode);
				} else {							//word is not in cache2, cache2 is not full
					myCache2.addFirst(bTreeNode);
				}
			}
		} else {									//word is not in cache1, cache1 is not full
			myCache1.addFirst(bTreeNode);
			if(isDoubleLevel){
				myCache2.addFirst(bTreeNode);
			}
		}
	}
	
	/**
	 * removes an object (String) from the cache
	 * @param aWord
	 */
	public void removeObject(Object aWord){
		if(myCache1.contains(aWord)){
			myCache1.remove(aWord);
		}
		if(isDoubleLevel){
			if(myCache2.contains(aWord)){
				myCache2.remove(aWord);
			}
		}
	}
	
	/**
	 * clears the cache
	 */
	public void clearCache(){
		myCache1.clear();
		if(isDoubleLevel){
			myCache2.clear();
		}
	}
	
	/**
	 *  
	 * @return the number of hits on cache1
	 */
	public long getNH1(){
		return NH1;
	}
	
	/**
	 *  
	 * @return the number of hits on cache2
	 */
	public long getNH2(){
		return NH2;
	}
	
	/**
	 *  
	 * @return the number of references to cache 1
	 */
	public long getNR1(){
		return NR1;
	}
	
	/**
	 * 
	 * @return the number of references to cache 2
	 */
	public long getNR2(){
		return NR2;
	}
}
