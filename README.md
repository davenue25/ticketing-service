# ticketing-service
Implementation of a simple ticket service that facilitates the discovery, temporary hold, and final reservation of seats for a performance venue 

### summary
Users may use the ticketing service to purchase tickets to a performance venue. First they must select the number of seats they want, next these seats are put on hold and not made available to anyone else. The hold last for a pre-determined set of seconds, configurable by settings. 

If the user chooses to reserve those seats within the "on hold" time period, those seats are reserved and a
reservation confirmation id is generated. If the "on hold" period is expired, the seats cannot be reserved and
those seats are available for someone else to hold.

The seats returned for an on hold request will be determined by the ticketing service. The service will try
to find the best seats available. The assumption is that the best seats are the closest to the stage, and 
starting out with the middle seat and spreading out to the edges of that row.

### compiling and creating the ticketing service
To compile the source code, clone this rep and install maven.

Run this maven command to compile and create the artifact (jar): mvn clean package

The above command will create a target directory. Inside the directory, there will be a "classes" directory
with the Java class files. It will also create the jar file, also in the target directory. This command
will also run the unit tests automatically.

An alternate way to run the tests is: mvn test
