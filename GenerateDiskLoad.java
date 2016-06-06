import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.text.*;

/**
 *GenerateDiskLoad is used to simulate heavy to low disk writes
 *It has throttling feature, but throttling applies only to writes which happens to page cache
 *write(..) ----speedInMBPerSec---->PageCache ----Kernel Speed---->Disk
 *Nevertheless, avg speed which you will demand from command line argument, will be provided by end of run.
 */
class GenerateDiskLoad {

	/**
 	* JVM will enter here
 	*/ 
	public static void main(String[] args) throws IOException{
		Map cmdLineArgs = validateAndGetArguments(args);//Command line arguments are stored as key value in a map
		System.out.println("Printing command line arguments or their default values: "+cmdLineArgs);
		for (int i = 0; i < Integer.parseInt((String)cmdLineArgs.get("iterations")); i++){
			testFileWrite(i,(Integer.parseInt((String)cmdLineArgs.get("fileSizeInMB")))*1024,Integer.parseInt((String)cmdLineArgs.get("speedInMBPerSec")),(String)cmdLineArgs.get("createTmpFileInDir"));
		}
	}

	/**
 	*Function 
	* a)To create a test.txt of size *fileSizeinBytes* and get deleted after program ends 
 	* b)To write with *speedInMBPerSec* speed
 	* 
 	*/
	private static void testFileWrite(int iteration, int fileSizeinBytes, int speedInMBPerSec, String filePath) throws IOException{
		File file = File.createTempFile("test", ".txt",new File(filePath)); file.deleteOnExit();
		char[] chars = new char[1024]; Arrays.fill(chars, 'A');String longLine = new String(chars); //String of length 1KB

		long start = System.nanoTime();
		long printNextTime = start;
		FileWriter pw = new FileWriter(file);
    		for (int i = 0; i < fileSizeinBytes; i++){
        		pw.write(longLine);
			printNextTime = printProgress(i,longLine.length(),printNextTime);
			if(i%(speedInMBPerSec*2) == 0) try { TimeUnit.NANOSECONDS.sleep(1); } catch (InterruptedException e) { } //Check after 1MB is written
		}
    		pw.close();
    		long end = System.nanoTime() - start;
    		System.out.printf("Iteration %d Took %.3f seconds to write to a %d MB, file rate: %.1f MB/s%n",iteration, end / 1e9, file.length() >> 20, file.length() * 1000.0 / end);

	}

	/**
 	*printProgress function prints how much of the file is currently being written.
 	*/ 
	private static long printProgress(int counter, int payloadSize, long printNextTime){
		long currTime = System.nanoTime();//Get current time
		if (currTime > printNextTime){
			long totalSizeWritten = (long) ((long)counter * (long)payloadSize);
			System.out.println("Total data written to file: "+readableFileSize(totalSizeWritten));
			printNextTime += (long)1000000000;//Add 1 sec, so that next message is printed after 1 sec
		}
		return printNextTime;
	}

	/**
 	* readableFileSize function converts number into human readable format of MB, GB
 	*/ 
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
		HashMap<String,Object> hm = new HashMap();

		if(args.length > 0){
			for(int i = 0; i < args.length;i++){
				if(args[i].equalsIgnoreCase("-h") || args[i].equalsIgnoreCase("--help")){
					allArgumentsExists = false;
					break;
				}
			}
			if(allArgumentsExists){
                        	for(int i = 0; i < args.length;i++){
					for (String retval: args[i].split(" ")){
						String[] keyval = retval.split("=");
						keyval[0] = keyval[0].replace("-","");
						hm.put(keyval[0],keyval[1]);
					}
                        	}
			}
		}
		if(!allArgumentsExists){
                        System.out.println("\u001B[33m"+"GenerateDiskLoad is used to simulate disk load.\n Ex, java GenerateDiskLoad; GenerateDiskLoad command also take custom parameters.\n  speedInMBPerSec is used to specify speed of disk write.\n  fileSizeInMB is used to specify size of the file being written to disk.\n  iterations is used to loop through file creation multiple times.\n Ex, java GenerateDiskLoad --speedInMBPerSec=100 --fileSizeInMB=1000 --iterations=2"+"\u001B[0m");
                        System.exit(1);
                }
		if(hm.get("fileSizeInMB") == null) hm.put("fileSizeInMB","5000");
		if(hm.get("speedInMBPerSec") == null) hm.put("speedInMBPerSec","100");
		if(hm.get("iterations") == null) hm.put("iterations","1");
		if(hm.get("createTmpFileInDir") == null) hm.put("createTmpFileInDir","/tmp/");
		return hm;	

	}

}
