Data Structures Question Bank
Question 1)	Find the closest Org for target Employees
a)	Imagine you are the team that maintains the Atlassian employee directory. At Atlassian – there are multiple groups, and each can have one or more groups. Every employee is part of a group. You are tasked with designing a system that could find the closest common parent group giv a target set of employees in the organization.
b)	The Atlassian hierarchy sometimes can have shared group across an org or employees shared across different groups – How will the code evolve n this case if the requirement is to provide ONE closest common group
c)	The system now introduced 4 methods to update the structure of the hierarchy in the org. Suppose these dynamic updates are done in separate threads while getCommonGroupForEmployees is being called, How ill your system handled reads and writes into the system efficiently such that at any given time getCommonGroupForEmployees always reflects the latest updated state of the hierarchy?
d)	The company consists of a single level of  groups with no subgroups. Each group has a set of employees.

Question 2)	Expanding Tennis Club
a)	Implement a function that given a list of tennis court bookings with start and finish times, returns a plan assigning each booking to a specific court, ensuring each court is used by only one booking at a time and using the minimum amount of courts with unlimited number of courts available.
An example of the booking record might look like

Class BookingRecord:
Id: int//ID of the booking
Start_time: int
Finish_time: int

And our function is going to look like:

List<Court> assignCourts(List<BookingRecord> bookingRecords)

b)	After each booking, a fixed amount of time, X, is needed to maintain the court before it can be rented again
c)	Court only need maintainenece after X amount of usage
How would you modify the code if each court also had a Y maintainence time that occurred after X bookings?
The function should now become something like
Def assign_court_with_maintainence(booking_records: list{BookingRecord],

Maintainence_time: int,

Durability:  int) -> list[Court]:
d)	The original problem can be made simpler by removing the “assigning each booking to a specific court” part. The candidate needs to find the minimum number of courts needed to accommodate all the bookings
e)	Check if booking conflict  - Write a function that if given two bookings to check if they conflict with each other

Question 3)	Commodity Prices
Imagine you are given a stream of data points consisting of <timestamp, commodityPrice> you are supposed to return the maxCommodityPrice at any point in time.
The timestamps in the stream can be out of order, or there can be duplicate timestamps, we need to update the commodityPrice at that particular timestamp if an entry for the timestamp already exists
Create an in-memory solution tailored to prioritize frequent reads and writes for the given problem statement

Can we reduce the time complexity of the getMaxCommodityPrice to O(1) if the language does not support it? This can be done using a variable to keep the maxPrice value, but we need to update it when performing the upsert operations.

Question 4)	Popular content
Imagine you are given a stream of content ids along with an associated action to be performed on them
Example of contents are video, pages, posts etc. There cam be two actions associated with a content id:
•	increasePopularity -> increases the popularity of the content by 1. The popularity increases when someone comments on the content or likes the comtent
•	decreasePopularity-> decreases the popularity of the content by 1. The popularity decreases when a spam bot’s/users comments are deleted from the content or its likes are removed from the content
•	content ids are positive integers
Implement a class that can return the mostPopular content id at any time while consuming the stream of content ids and its associated action. If there are no contentIds with popularity greater than 0, return -1

Question 5) Weighted Graph
Imagine we have a network with N nodes. Each node has a label (a name). Between two nodes, there is a value of time in milliseconds 
that indicates how long a packet can be transmitted from one node to the other. Be noted that this is one-way direction which means 
packet can be transmitted the other way around with different time value, or, even prohibited.
Given 2 node labels (source and destination), write a function that answers the following questions:
Can a packet transmit from the source node to the destination node?
If yes, what is the shortest time that a packet can transmit from the source node to the destination node?
This graph illustrates the input/output for this question:The numbers between pairs of nodes illustrate the time a packet can be 
transmitted from one to another.
A solid line illustrates that two nodes are already established.
A dotted line illustrates that two nodes can be connected but they are not established yet.
The green path illustrates the shortest/fastest path that a packet can go from node A to node E (output) with a total weight of 1 + 6 + 2 = 9.
Scale Up - Build one bridge to allow packet transmission from the source to the destination.
Scale Down - Just use a naive solution by looping through all potential pairs of nodes (that can be connected but not yet established). For each of them:
modify the input data to establish that pair re-run the algorithm/function that candidate already built earlier compare to find the optimal result

Question 6) - Job Interval Reporting
CI pipeline count
Atlassian runs a lot of CI pipelines and our team is tasked to  build some reporting on the usage to find some cost optimisation patterns.
Each CI pipeline starts at a given time X and ends at Y. We are given a list of CI pipeline time windows {X,Y}.
[{X,Y}, {X`, Y`}, ….]

Objective:
Find the time windows where at least one CI pipeline is running.

Input:
[{2, 5}, {12, 15}, {4, 8}]
Output: [{2, 8}, {12, 15}]
Explanation: The windows in minimum where at least one job is running are {2, 8} and {12, 15}

Scale up - Find window with at least two pipelines running in a given time window.
Scale Up -  Find the busiest window/s where maximum number of pipelines are running?

Code Design Question Bank
Question 1)	Customer Satisfaction
a)	Imagine we have a customer support ticketing system. The system allows customers to rate the support agent out of 5. 
To start with, write a function which accepts a rating, 
and another which will show me all of the agents and the average rating each one has received, ordered highest to lowest.
b)	Currently your solution does not account for what happens if two agents have the same average rating. What options are there for handling ties 
and how can we implement that in code?
c)	Now I want to be able to see who the best agents are each month. 
Change the implementation so I can get that information.
d)	Write a new function that will allow me to export of each agent’s average ratings per month. 
You can export in any format you like- for example csv,json or xml.
e)	Make it return the average ratings unsorted./ Make it return the total rating for each agent without the average

Question 2)	Middleware Router
a)	We want to implement a middleware router for our web service, which based on the path returns different strings
Our interface for the router looks something like:

Interface Router {
Fun addRoute(path: String, result: String) : Unit;
Fun callRoute(path:String) :String;
}

Usage:
Router.addRoute(“/bar” , “result)
Router.callRoute(“/bar”) -> “result”

Scale Up 1 – Wildcards using ordered checking
Scale Up 2 – PathParams

Question 3)	Game of Snakes
Remember the old phone game of snake? The snake can move up, down, left or right in a 2Dimensional board of arbitrary size.
Lets try to implement the base logic of this game
Rules:
•	Every time moveSnake() is called, the snake moves up, down, left or right
•	The snake’s initial size is 3 and grows by 1 every 5 moves
•	The game ends when the snake hits itself

We can use the following as a starting point (pseudo-code):
Interface SnakeGame {
moveSnake(snakeDirection);
isGameOver();
}

Proposed Changes
Change#1
Make scale-up 2 optional
Remove scale-up 2 completely
Change#2
Add new optional scale up: snake grows when it eats food rather than every 5moves.  Food is dropped at a random position on the board.

Question 4)	Cost Explorer
Imagine you are working on payments team at Atlassian.
A customer subscribes to Jira and is interested in exploring how much it ‘ill cost them to keep using the product for the rest of the year
Your task is to develop a CostExplorer that calculates the total cost a customer has to pay in a unit year . 
This means that at any day of the year they should be able to get a provisional report giving monthly/yearly cost estimated.

CostExplorer should be able to provide a report of –

1. Monthly cost (Generate a bill for each month, including bill amount for future months for the unit year)
2. Yearly cost estimated(for the unit year)

Level 0 : Multiple Plans for a Product

Question 5)  F1 Last Lap Hero

Design and implement a program that accepts lap times for Formula 1 race drivers. Return the "Last Lap Hero" - 
the driver who had the biggest improvement on their last lap compared to their average lap time.

Scale Ups

1. Implement a system to handle Pit Stop Laps where a pit stop causes a longer lap time. Pit Stop Laps should be excluded from the average lap time calculation, but are a valid last lap.
   Calculate the “Last Lap Hero” for both scenarios: including and excluding pit stops.
2. Introduce telemetry to track and report every time the “Last Lao hero” changes during the race

Scale Down
3. Only return which driver had the fastest lap.
4. accept lap times for only one driver. Stats to return:
   The fastest lap time
   The average lap time

Question 6) Cinema Screenings
Let’s pretend we are in charge of a cinema. We want to figure out whether a new movie can be
added to the existing schedule without removing any of the current movies
   Note that:
   The cinema opens at 10.00
   The last possible end time for a movie is 23.00
   Movie durations include setting up the room before the movie begins and cleaning it up afterward.
In other words, if for example a movie ends at 14.00, the next movie can start at 14.00
   Movie start times are expressed in minutes starting from midnight, so for example,10am would be 10*60 = 600
   Scale Up 1)
   Let’s add expected revenue per screening to each movie.
   Scale Up 2)
   Insert a new screening into a full schedule
   You have a full schedule and a new movie. You want to add one screening of the new movie in the schedule. Which existing screening are we removing?
   Scale Up 3) Multiple Rooms
   The cinema now has more than one room. How does this affect the design of the solution? Keep in mind that when adding a new movie to the schedule, we want to maximise earnings.
   Scale Downs
   Given a schedule, simply print out movie start and end times.

Question 7) Deployment Notification

You are working on a deployment notification service for keeping developers informed about when their changes have been deployed.
The service should provide two interfaces, one to receive an event and another to send out a batch of notifications for any received events.
An event contains a version number, a list of authors, and a status indicating if the deployment has started, completed, or failed.
Deployment notifications should include the author and the version in which their changes were deployed.
The goal is to notify code change authors on the first time their changes are deployed successfully for each unique set of code changes

Scale Ups - Some teams have noticed that when changes get reverted by a different author, the original author will still receive notifications when they no longer have any changes in the deployed version.
To prevent these notifications, we want to add support for a revert deployment event type.
In the case where a revert occurs before the changes are deployed, we should prevent the notification going out to that author.
Scale Down - Only started and completed events can occur, deployments cannot fail.