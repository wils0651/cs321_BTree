import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * A test and driver class for Cache.java. Parses and inputs a text file into the
 * cache and computes statistics on the cache.
 * @author Tim Wilson
 *
 */
public class Test {
	
	/**
	 * the main method for the driver class.
	 * @param args
	 */
	public static void main(String[] args){
		int cacheSize1=0;
		int cacheSize2=0;
		String filename = "";
		Cache myCache = null;
		boolean isDoubleLevel = false; //single or double level cache, default single
		
		// parse args
		if (args.length != 3 && args.length != 4) {
			printUsage();
			System.exit(1);
		} else if(args[0].equals("1")) {	//
//			System.out.println("Single Level Cache");
			cacheSize1 = Integer.parseInt(args[1]);
			filename = args[2];
			isDoubleLevel = false;
			myCache = new Cache(cacheSize1);
			
		} else if(args[0].equals("2")) {
			//System.out.println("Double Level Cache");
			cacheSize1 = Integer.parseInt(args[1]);
			cacheSize2 = Integer.parseInt(args[2]);
			filename = args[3];
			isDoubleLevel = true;
			myCache = new Cache(cacheSize1, cacheSize2);
			
		} else {
			printUsage();
			System.exit(1);
		}
		
		try {

			File theFile = new File(filename);

			Scanner fileScan = new Scanner(theFile);

			while (fileScan.hasNextLine()) {
				String inputLine = fileScan.nextLine();

				StringTokenizer tokenizer = new StringTokenizer(inputLine);

				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					myCache.addObject(token);
				}
			}

			fileScan.close();
			System.out.println(".");

		} catch (FileNotFoundException e) {
			System.err.println("File not found.");
			System.exit(1);
		}
		
		if(!isDoubleLevel){
			long NH= myCache.getNH1();
			long NR = myCache.getNR1();
			System.out.println("Total number of references: "+NR);
			System.out.println("Total number of cache hits: "+NH);
			float HR = (float)NH/NR;
			System.out.println("The global hit ratio                  : "+HR);
		}else{
			long NH1= myCache.getNH1();
			long NH2= myCache.getNH2();
			long NR1 = myCache.getNR1();
			long NR2 = myCache.getNR2()+cacheSize1;
			long TNH = NH1 + NH2;
			System.out.println("Total number of references: "+NR1);
			System.out.println("Total number of cache hits: "+TNH);
			float HR = (float)TNH/NR1;
			System.out.println("The global hit ratio                  : "+HR);
			System.out.println("Number of 1st-level cache hits: "+ NH1);
			float HR1 = (float)NH1/NR1;
			System.out.println("1st-level cache hit ratio             : "+ HR1);
			System.out.println("Number of 2nd-level cache hits: "+ NH2);
			float HR2 = (float)NH2/NR2;
			System.out.println("2nd-level cache hit ratio             : "+ HR2);
			//System.out.println("NR1: "+NR1);
			//System.out.println("NR2: "+NR2);
		}
	}
	
	/** Print instructions for running the Test class from the command line. */
	private static void printUsage() {
		System.out.println("Usage: java Test 1 <cache size> <input textfile name> or");
		System.out.println("       java Test 2 <1st-level cache size> <2nd-level cache size> <input textfile name>");
	}

}
