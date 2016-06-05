
## Command ##
```
javac GenerateWorkLoad.java && java -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -Xloggc:/tmp/gc.log -XX:+UseConcMarkSweepGC -XX:NewSize=100m -XX:MaxNewSize=100m -XX:+HeapDumpOnOutOfMemoryError -XX:CMSInitiatingOccupancyFraction=85 -XX:+ParallelRefProcEnabled -XX:+AlwaysPreTouch -XX:MaxTenuringThreshold=15 -XX:SurvivorRatio=8 -server -Xms6000m -Xmx6000m GenerateWorkLoad --speedInMBPerSec=50 --fileSizeInMB=2000
```
