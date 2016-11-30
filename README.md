# Axwave SoundServer and SoundClient

SoundClient depends on SoundServer to share code, so to build you need to first do mvn install on server then mvn package on client.

Then 

> java -jar server-1.0-SNAPSHOT-jar-with-dependencies.jar

and

> java -jar client-1.0-SNAPSHOT-jar-with-dependencies.jar

You can list availaible command line options (port, host, k,n, etc) by adding -help.

# What could be the next steps to make the app more "production" ready: 
1. It could be tested on Linux
2. Shared code should be extracted to separate module (now for simplicity it's just a package inside the server)
2. Recovery from exceptions (reconnection on server down etc.)
3. Better audioFormat coding (to get all the availaible lines from the system and choosing the best one instead of relying on predefined ones)




