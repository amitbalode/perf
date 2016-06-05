# GenerateDiskLoad #
* Java command to simulate disk load. It creates a test.txt file of custom size and dump random data with custom speed. Custom size and speed can be specified via command line arguments.


## Basic Command ##
```
javac GenerateDiskLoad.java
java GenerateDiskLoad --help
java GenerateDiskLoad
```

## Custom Command with heap size and JVM parameters
```
javac GenerateDiskLoad.java && java -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -Xloggc:/tmp/gc.log -XX:+UseConcMarkSweepGC -XX:NewSize=100m -XX:MaxNewSize=100m -XX:+HeapDumpOnOutOfMemoryError -XX:CMSInitiatingOccupancyFraction=85 -XX:+ParallelRefProcEnabled -XX:+AlwaysPreTouch -XX:MaxTenuringThreshold=15 -XX:SurvivorRatio=8 -server -Xms6000m -Xmx6000m GenerateDiskLoad --speedInMBPerSec=50 --fileSizeInMB=2000
```
