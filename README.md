# Parcel Delivery App

Example of REST API built on Spring Boot 3.

### User stories

1. User  
Can create an user account and log in`*1`;  
Can create a parcel delivery order`*2`;  
Can change the destination of a parcel delivery
order`*3`;  
Can cancel a parcel delivery order`*4`;  
Can see the details of a delivery;  
Can see all parcel delivery orders that he/she created;  


2. Admin  
Can change the status of a parcel delivery order;  
Can view all parcel delivery orders;  
Can assign parcel delivery order to courier;  
Can log in and create a courier account`*5`;  
Can track the delivery order by coordinates;  
Can see list of couriers with their statuses;  


3. Courier  
Can log in;  
Can view all parcel delivery orders that assigned to him;  
Can change the status of a parcel delivery
order;  
Can see the details of a delivery order;  

`*1` - with JWT;  
`*2` - define fields yourself;  
`*3` - define when user can change the destination yourself;  
`*4` - define when user can cancel parcel delivery order
yourself;  
`*5` - define courier types yourself;

### How to run
Application can be started locally (spring profile `local` + database in the docker container)  
or in the docker container (spring profile `test` and both database and app are docker containers)
#### Locally:
1. In the root folder start the container with database:
```
docker-compose up db -d
```
2. Start Spring Boot application locally with spring profile `local`.
#### Inside docker container:
1. In the root folder build the dockerfile:
```
docker build -t parceldeliveryapp .
```
2. Start docker compose with the database and application:
```
docker compose up -d
```
3. Call spring actuator to check that application is up and running:
```
GET http://localhost:8080/parceldeliveryapp/actuator/health
```
#### Stop the application:
```
docker compose down
```