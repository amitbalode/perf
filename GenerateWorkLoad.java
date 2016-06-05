import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.text.*;

class GenerateWorkLoad {

	public static void main(String[] args) throws IOException{
		Map<String,Integer> cmdLineArgs = validateAndGetArguments(args);//Command line arguments are stored as key value in a map
		testFileWrite(((int)cmdLineArgs.get("fileSizeInMB"))*1024,(int)cmdLineArgs.get("speedInMBPerSec"));
	}


	/**
 	*Function 
	* a)To create a test.txt of size *fileSizeinBytes* and get deleted after program ends 
 	* b)To write with *speedInMBPerSec* speed
 	* 
 	*/
	private static void testFileWrite(int fileSizeinBytes, int speedInMBPerSec) throws IOException{
		File file = File.createTempFile("test", ".txt"); file.deleteOnExit();
		char[] chars = new char[1024]; Arrays.fill(chars, 'A');String longLine = new String(chars); //String of length 1KB

		long start = printNextTime = System.nanoTime();
		FileWriter pw = new FileWriter(file);
		System.out.println(fileSizeinBytes);
    		for (int i = 0; i < fileSizeinBytes; i++){
        		pw.write(longLine);
			printNextTime = printProgress(i,longLine.length(),printNextTime);
			if(i%(speedInMBPerSec*2) == 0) try { TimeUnit.NANOSECONDS.sleep(1); } catch (InterruptedException e) { } //Check after 1MB is written
		}
    		pw.close();
    		long end = System.nanoTime() - start;
    		System.out.printf("Took %.3f seconds to write to a %d MB, file rate: %.1f MB/s%n",end / 1e9, file.length() >> 20, file.length() * 1000.0 / end);

	}

	private static long printProgress(int counter, int payloadSize, long printNextTime){
		long currTime = System.nanoTime();//Get current time
		if (currTime > printNextTime){
			long totalSizeWritten = (long) ((long)counter * (long)payloadSize);
			System.out.println("Total data written to file:"+readableFileSize(totalSizeWritten));
			printNextTime += (long)1000000000;//Add 1 sec, so that next message is printed after 1 sec
		}
		return printNextTime;
	}

	public static String readableFileSize(long size) {
    		if(size <= 0) return "0";
    		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
    		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
    		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
}

/**
 * Function to validate input parameters to this class file
 * At the end it will generate a hashmap of key value store of command line input arguments
 */
	public static Map validateAndGetArguments(String[] args){

		boolean allArgumentsExists = true;
		HashMap<String,Integer> hm = new HashMap();

		if(args.length == 0){
			hm.put("fileSizeInMB",5000);
			hm.put("speedInMBPerSec",100);
		}else{
			for(int i = 0; i < args.length;i++){
				if(args[i].equalsIgnoreCase("-h") || args[i].equalsIgnoreCase("--help")){
					allArgumentsExists = false;
					break;
				}
			}
                        for(int i = 0; i < args.length;i++){
				for (String retval: args[i].split(" ")){
					String[] keyval = retval.split("=");
					keyval[0] = keyval[0].replace("-","");
					hm.put(keyval[0],new Integer(keyval[1]));
				}
                        }
		}
		if(!allArgumentsExists){
                        System.out.println("Please specify command line arguments like: java GenerateWorkLoad --speedInMBPerSec=100 --fileSizeInMB=1000");
                        System.exit(1);
                }
		return hm;	

	}

}
