## Aircall Technical Test - Aircall Pager

The main goal of this project is to implement a solution for incident alerts and on-call shifts for Aircall.

## Development

This project was build using [Java 11](https://www.oracle.com/es/java/technologies/javase-jdk11-downloads.html) and it uses [Maven 3](https://maven.apache.org/download.cgi).

The structure of this project is very simple. In the package main/java/io/aircall you will find all the classes that will contain the services, entities and adapters needed to implement the solution.

The tests are located in test/java/io/aircall/pagerservice. In order to implement the test JUnit and Mockito was used.

## How to test it

In order to test this solution, you will have to execute the next command in the root folder.

`mvn clean test`

## Assumptions

It would like to mention some assumptions before starting with the tests:

* In order to be able to run the tests you will need maven installed on your machine.

* I will assume that in PagerDB the Monitored Services are already stored.
  
* All the notifications are sent properly, otherwise we should manage the case when the notification sent is failed.

* When the all the target have been notified but still no ack received nor healthy event, if a timeout ack is received, no actions will be done for the service.

## Concurrency

My proposal to avoid the issues in persistence layer is always to manage the operations with DB ensuring ACID principles, managing transactions properly. Also, it could be implemented an optimistic / pesimistic locking strategy.
