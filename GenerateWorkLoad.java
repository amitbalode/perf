import java.util.*;
import java.util.concurrent.*;
import java.io.*;

class GenerateWorkLoad {

	private static final double MEG = (Math.pow(1024, 2));
	private static final String RECORD = "Tumse na ho payega\n";
	private static final int RECSIZE = RECORD.getBytes().length;



	public static void main(String[] args) throws IOException{
		Map mp = validateAndGetArguments(args);
		System.out.println(mp);
		int fileSizeinBytes = ((int)mp.get("fileSizeInMB"))*1024;
		int loopCounter = fileSizeinBytes/RECSIZE;
		testFileWrite(fileSizeinBytes,(int)mp.get("speedInMBPerSec"));
	}

	private static void testFileWrite(int fileSizeinBytes, int speedInMBPerSec) throws IOException{
		File file = File.createTempFile("test", ".txt");
		file.deleteOnExit();
		char[] chars = new char[1024];
		Arrays.fill(chars, 'A');
		String longLine = new String(chars);

		long start1 = System.nanoTime();
		//PrintWriter pw = new PrintWriter(new FileWriter(file));
		FileWriter pw = new FileWriter(file);
		System.out.println(fileSizeinBytes);
		long lastSeenTime = start1;
		long currTime = start1;
		long prevTime = start1;
		long fileSizeCheckPoint = 0;
    		for (int i = 0; i < fileSizeinBytes; i++){
        		pw.write(longLine);
			fileSizeCheckPoint += longLine.length();
			currTime = System.nanoTime();
			if(currTime > lastSeenTime){ 
				System.out.printf("Took %.3f seconds to write to %d MB, file rate: %.1f MB/s%n",(currTime-prevTime) / 1e9, (fileSizeCheckPoint) >> 20, fileSizeCheckPoint * 1000.0 / (currTime-prevTime)); 
				lastSeenTime += (long)1000000000;prevTime = currTime; fileSizeCheckPoint = 0;
			}
			if(i%(speedInMBPerSec) == 0) try { TimeUnit.NANOSECONDS.sleep(50000); } catch (InterruptedException e) { }
		}
    		pw.close();
    		long time1 = System.nanoTime() - start1;
    		System.out.printf("Took %.3f seconds to write to a %d MB, file rate: %.1f MB/s%n",time1 / 1e9, file.length() >> 20, file.length() * 1000.0 / time1);

	}
/**
 * Function to validate input parameters to this class file
 * At the end it will generate a hashmap of key value store of command line input arguments
 */
	public static Map validateAndGetArguments(String[] args){

		boolean allArgumentsExists = true;
		HashMap hm = new HashMap();

		if(args.length == 0){
			allArgumentsExists = false;
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
