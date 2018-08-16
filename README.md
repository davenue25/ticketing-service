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

A seat is available for hold if the seat is not already on hold and is not reserved.

A "seat hold" is concept to hold seats for a particular user. If the seat hold is active, then the seat hold
can be reserved. If the "on hold" time expired, the seat hold is not active and cannot be used to reserve the
seats.

### compiling and creating the ticketing service
To compile the source code, clone this rep and install maven.

To clone the repo, make you have git installed on your system and then run:

git clone https://github.com/davenue25/ticketing-service.git

After installing maven, to the ticketing-service directory that was created with the clone command,
and run this maven command to compile, run unit tests and create the ticketing service 
artifact (jar):

mvn clean package

The above command will create a target directory. Inside the directory, there will be a "classes" directory
with the Java class files. It will also create the jar file, also in the target directory. This command
will also run the unit tests automatically.

An alternate way to run the tests is: mvn test

### sample code to hold and reserve seats

Look at the tests in TicketServiceImplTest.java.
