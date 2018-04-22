Meeting Calendar
================
Simple application for processing meeting requests. It is a command line running application.
The application reads the input.txt file with meeting request content and prints the output to screen.
To choose a file of your choice pass "inputFileName" as a system property.


    Input
    Your processing system must process input as text.
    • The first line of the input text represents the company office hours, in 24 hour
    clock format
    • The remainder of the input represents individual booking requests. Each
    booking request is in the following format.
    [request submission time, in the format YYYY-MM-DD HH:MM:SS]
    [ARCH:employee id]
    [meeting start time, in the format YYYY-MM-DD HH:MM] [ARCH:meeting
    duration in hours]
    

    Sample Input
    0900 1730
    2015-08-17 10:17:06 EMP001
    2015-08-21 09:00 2
    2015-08-16 12:34:56 EMP002
    2015-08-21 09:00 2
    2015-08-16 09:28:23 EMP003
    2015-08-22 14:00 2
    2015-08-17 11:23:45 EMP004
    2015-08-22 16:00 1
    2015-08-15 17:29:12 EMP005
    2015-08-21 16:00 3
    
    Sample Output
    2015-08-21
    09:00 11:00 EMP002
    2015-08-22
    14:00 16:00 EMP003
    16:00 17:00 EMP004


Build
-----
The application is based on spring-boot maven.


Execution
---------
To run use mvn install and run the dat jar or run "mvn spring-boot:run" from the source code directory.


Future
------
 The application is currently a simple command line application but following things can be done
 
    Rest:
        we can create a rest controller to call the methods called by MeetingRequestFileInputRunner and get the output
    
    Jpa:
        we can use Jpa to replcae the MeetingCalendarRepository and have much easier access to data
        
    Queue:
        we can add a queue to store the incoming requests and make the MeetingCalendarService.save() independent
        
