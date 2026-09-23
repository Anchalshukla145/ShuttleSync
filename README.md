
# ShuttleSync — Office Shuttle Segment-Based Seat Booking

ShuttleSync is a Spring Boot backend system for managing office shuttle routes, trips, seats, passenger bookings, cancellations, and waitlists with segment-based seat allocation.

---

# Problem Statement

Office shuttle services operate on predefined routes with multiple ordered stops and a fixed number of seats. Passengers may travel between different stops instead of using the shuttle for the complete route.

The main problem is to efficiently allocate seats based on the **specific segment requested by a passenger**, rather than considering a seat occupied for the entire trip.

For example, on a route:

A → B → C → D


a passenger travelling from A to B should not prevent another passenger from using the same seat from B to D because the two journeys do not overlap.

At the same time, two bookings whose segments overlap must never be assigned to the same seat.

The system must therefore provide secure authentication, correct segment-based seat allocation, concurrency protection, cancellation and waitlist handling, proper error recovery, and a maintainable object-oriented architecture while considering the time, space, performance, scalability, and simplicity trade-offs of the implementation.

---

# Case Study Requirements

The project was designed according to the following major requirements.

## 1. Authentication

Implement secure user authentication so that only authenticated users can access protected application functionality.

### Implementation

ShuttleSync uses:

* Spring Security
* JWT-based authentication
* Password hashing
* Stateless authentication
* Protected REST endpoints

The authentication flow is:

Login Request
     |
     v
AuthenticationManager
     |
     v
UserDetailsService
     |
     v
UserRepository
     |
     v
MySQL
     |
     v
JWT
```

After login, the client sends the JWT with protected requests:

```http
Authorization: Bearer <JWT>
```

---

# 2. Time and Space Complexity

The seat allocation algorithm was designed with the expected shuttle capacity in mind.

For every requested segment, the system checks the seats of the trip and the confirmed bookings assigned to each seat.

If:

```text
S = number of seats
B = average number of active bookings checked per seat
```

the current allocation algorithm has:

```text
Time Complexity: O(S × B)

Additional Space: O(B)
```

The implementation uses lists and a linear scan.

A segment tree or interval tree could provide more efficient interval queries for a much larger booking volume, but it would introduce additional complexity.

For the expected shuttle capacity, the linear approach provides a simpler and easier-to-maintain solution.

---

# 3. Handling System Failure Cases

The system handles application and business-level failures through:

* Transactional database operations
* Validation
* Custom exceptions
* Global exception handling
* Database constraints
* Pessimistic locking for concurrent seat allocation
* Persistent booking records

Invalid operations do not silently create incorrect bookings.

Examples include:

* Invalid user
* Invalid trip
* Invalid stop
* Invalid route segment
* Overlapping seat booking
* Unavailable requested seat
* No suitable seat for the requested segment
* Repeated cancellation

Database transactions are used for critical operations so that related changes are committed together.

For a production deployment, database backup and recovery procedures should be configured at the database/infrastructure level to protect against database or infrastructure failure.

---

# 4. Object-Oriented Design

Java and Spring Boot were used as the OOP-based implementation platform.

The system is divided into entities, services, repositories, controllers, DTOs and security components.

Important OOP principles used include:

### Encapsulation

Business operations are kept inside appropriate service classes instead of placing the complete logic inside controllers.

For example:

```text
BookingController
       |
       v
BookingService
       |
       v
SeatAllocationService
```

The controller handles the API request while the service layer handles the booking rules.

### Abstraction

Interfaces and Spring abstractions such as repositories allow database operations to be separated from business logic.

For example:

```text
BookingRepository
SeatRepository
TripRepository
UserRepository
```

The service layer does not need to implement raw SQL for every operation.

### Composition

The system models relationships between objects such as:

```text
Route → Stops
Trip → Seats
Trip → Bookings
Trip → Waitlist Entries
Booking → User
Booking → Seat
```

### Single Responsibility

Different components have focused responsibilities.

For example:

```text
AuthService
        Authentication

RouteService
        Route management

TripService
        Trip creation and management

BookingService
        Booking and cancellation

SeatAllocationService
        Segment-based seat allocation

WaitlistService
        Waitlist and promotion
```

This makes the code easier to maintain and modify.

### Singleton / Managed Service Objects

Spring manages service and configuration components as singleton-scoped beans by default.

This allows shared application services such as authentication and booking services to be managed by the Spring container rather than manually creating multiple instances throughout the application.

---

# 5. Trade-offs

Several design decisions were made by balancing simplicity, performance and scalability.

## Linear Seat Search vs Interval Tree

The system currently checks bookings using a linear scan.

This is simple and appropriate for a shuttle with a relatively small fixed number of seats.

For significantly larger booking volumes, an interval tree or similar indexing structure could reduce search cost, but would make the implementation more complex.

## Database Locking vs Java Synchronization

Database pessimistic locking was selected instead of `synchronized`.

Java synchronization only protects threads within one application instance.

Database locking protects the seat allocation transaction at the database level and is therefore more appropriate if multiple application instances are running.

## Database State vs Cached Availability

The booking records are treated as the source of truth for segment availability.

Caching could be introduced later for frequently requested availability data to reduce database reads.

However, cached availability would need careful invalidation whenever a booking is created or cancelled so that stale availability does not cause incorrect bookings.

---

# System Architecture

ShuttleSync follows a layered architecture:

```text
                    Client
                      |
                      v
               Controller Layer
                      |
                      v
                 Service Layer
                      |
                      v
              Repository Layer
                      |
                      v
                  MySQL
```

### Controller Layer

Responsible for:

* Receiving HTTP requests
* Request validation
* Calling services
* Returning API responses

### Service Layer

Contains the main business logic.

Important services:

```text
AuthService
RouteService
TripService
BookingService
SeatAllocationService
WaitlistService
AvailabilityService
```

### Repository Layer

Handles database persistence through Spring Data JPA.

### Entity Layer

Contains the main domain objects:

```text
User
Route
Stop
Trip
Seat
Booking
WaitlistEntry
```

---

# Core Booking Logic

## Route and Stops

A route contains ordered stops.

Example:

```text
A → B → C → D
```

Each stop has a sequence number:

```text
A = 1
B = 2
C = 3
D = 4
```

This sequence is used to determine whether two booking segments overlap.

---

# Segment Overlap

Bookings are represented using:

```text
[pickupSequence, dropoffSequence)
```

For example:

```text
A → B = [1, 2)

B → D = [2, 4)

A → C = [1, 3)
```

Two bookings overlap when:

```text
newStart < existingEnd
AND
newEnd > existingStart
```

Therefore:

```text
A → B
B → D
```

can use the same seat because the bookings only meet at B.

But:

```text
A → C
B → D
```

cannot use the same seat because they overlap between B and C.

---

# Automatic Seat Allocation

When a passenger does not request a specific seat, the system:

1. Retrieves the seats belonging to the trip.
2. Processes them in deterministic order.
3. Checks confirmed bookings for each seat.
4. Compares their segments with the requested segment.
5. Selects the first suitable seat.
6. Rejects the booking if no suitable seat exists.

The system therefore does not rely only on a route-wide available-seat count.

A seat can be reused during different non-overlapping portions of the route.

---

# Specific Seat Allocation

A passenger can request a particular seat.

The system verifies that:

* The seat exists.
* The seat belongs to the requested trip.
* The pickup and dropoff stops belong to the correct route.
* Pickup occurs before dropoff.
* The requested segment does not overlap an existing confirmed booking.

If the requested seat cannot serve the segment, the request is rejected.

---

# Concurrent Last-Seat Booking

A major concurrency problem occurs when two passengers request the same last usable seat at the same time.

ShuttleSync uses database pessimistic locking for this operation.

The simplified flow is:

```text
Request 1
    |
    v
Lock Seat
    |
Check Booking
    |
Create Booking
    |
Commit
    |
Unlock
    |
    v
Request 2
    |
Wait for Lock
    |
Re-check Booking
    |
Reject if overlap exists
```

This prevents two overlapping bookings from successfully claiming the same seat.

---

# Booking Cancellation

Bookings are not physically deleted.

Instead:

```text
CONFIRMED → CANCELLED
```

This preserves booking history.

After cancellation, the system checks whether a waiting passenger can use the released seat.

---

# Waitlist

When a passenger cannot obtain a suitable seat, they can join the waitlist.

Waitlist entries maintain a priority order representing first-come-first-served ordering.

Example:

```text
Priority 1
Priority 2
Priority 3
```

The repository retrieves pending entries in priority order.

---

# Smart Waitlist Promotion

Cancellation triggers waitlist processing.

The system:

1. Finds pending waitlist entries for the trip.
2. Processes them in priority order.
3. Checks whether the passenger's requested segment fits the released seat.
4. Skips passengers whose segment is not compatible.
5. Promotes the first eligible passenger.
6. Creates a normal confirmed booking for that passenger.

The important rule is:

```text
FIFO among eligible passengers
```

This prevents a passenger with an incompatible segment from blocking another passenger who can actually use the freed seat.

---

# Error Handling

The application uses custom exceptions and a global exception handler.

Examples:

```text
ResourceNotFoundException
BookingException
```

The system validates business rules before performing operations.

Examples:

* User does not exist
* Trip does not exist
* Stop does not exist
* Invalid pickup/dropoff order
* Stops belong to another route
* Seat belongs to another trip
* Segment overlaps an existing booking
* No seat available
* Booking already cancelled

Errors are returned as structured API responses instead of exposing internal application errors directly.

---

# Database

MySQL is used as the persistent data store.

Main tables/entities include:

```text
users
routes
stops
trips
seats
bookings
waitlist_entries
```

JPA/Hibernate manages the object-relational mapping.

---

# API Overview

## Authentication

```http
POST /api/auth/register
POST /api/auth/login
```

## Routes

```http
POST /api/routes
GET  /api/routes
GET  /api/routes/{id}
```

## Trips

```http
POST /api/trips
GET  /api/trips
GET  /api/trips/{id}
GET  /api/trips/route/{routeId}
GET  /api/trips/search
```

## Bookings

```http
POST /api/bookings
POST /api/bookings/{id}/cancel
```

## Waitlist

```http
POST /api/waitlist/join
GET  /api/waitlist/trip/{tripId}
GET  /api/waitlist/user/{userId}
```

Protected APIs require:

```http
Authorization: Bearer <JWT>
```

---

# Project Structure

```text
src/main/java/com/movein/shuttlesync/
│
├── common/
│   ├── constants/
│   ├── enums/
│   └── util/
│
├── config/
│
├── controller/
│
├── dto/
│
├── entity/
│
├── exception/
│
├── repository/
│
├── security/
│
└── service/
```

---

# Installation and Setup

## Prerequisites

Install:

* Java 25
* Maven
* MySQL
* Git
* Postman

Verify Java:

```bash
java -version
```

---

# Clone the Repository

```bash
git clone https://github.com/Anchalshukla145/ShuttleSync.git
cd ShuttleSync
```

---

# Database Setup

Create the MySQL database:

```sql
CREATE DATABASE shuttlesync;
```

---

# Environment Configuration

Create a `.env` file in the project root:

```env
DB_URL=jdbc:mysql://localhost:3306/shuttlesync?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

SERVER_PORT=8080

JWT_SECRET=your_long_random_secret_key
JWT_EXPIRATION=86400000
```

Replace the MySQL password with the credentials of your local MySQL installation.

The `.env` file should not be committed to Git.

---

# Build the Project

The project is configured for Java 25.

Compile using:

```bash
mvn-java25 clean compile
```

Expected result:

```text
BUILD SUCCESS
```

---

# Run the Application

Start the Spring Boot application from the IDE.

The default server port is:

```text
8080
```

The API will be available at:

```text
http://localhost:8080
```

---

# Testing and Validation

The backend can be tested using Postman.

Recommended testing order:

1. Register a user
2. Login and obtain JWT
3. Create a route
4. Create ordered stops
5. Create a trip
6. Verify seats are created
7. Create a valid booking
8. Test invalid booking requests
9. Test non-overlapping seat reuse
10. Test overlapping booking rejection
11. Test automatic seat allocation
12. Test concurrent last-seat booking
13. Add passengers to the waitlist
14. Cancel a booking
15. Verify waitlist promotion
16. Verify the final database state

Example booking:

```http
POST http://localhost:8080/api/bookings?userId=2
Authorization: Bearer <JWT>
Content-Type: application/json
```

```json
{
  "tripId": 1,
  "pickupStopId": 1,
  "dropoffStopId": 2
}
```

---

# Database Verification

Bookings can be verified directly in MySQL:

```sql
SELECT
    id,
    trip_id,
    seat_id,
    pickup_stop_id,
    dropoff_stop_id,
    status
FROM bookings
WHERE trip_id = 1
ORDER BY id;
```

Waitlist entries can be verified using:

```sql
SELECT
    id,
    trip_id,
    pickup_stop_id,
    dropoff_stop_id,
    priority_order,
    status
FROM waitlist_entries
WHERE trip_id = 1
ORDER BY priority_order;
```

This allows both the API response and the underlying database state to be validated.

---

# Future Scope

The current implementation uses simple lists and linear segment checking because the expected shuttle capacity is relatively small.

For larger-scale deployment, the system can be extended with:

* Interval trees or other indexed structures for faster segment queries when the number of seats and bookings becomes large.
* Caching for frequently requested availability data to reduce repeated database reads, with proper cache invalidation after booking and cancellation.
* Automated database backup and recovery for stronger disaster recovery.
* Automated integration and concurrency test suites.
* Better availability-specific DTOs instead of exposing entity relationships directly.
* Deriving the authenticated user directly from the JWT instead of passing `userId` in booking requests.
* More detailed monitoring and operational metrics.

---

# Current Implementation Status

The core ShuttleSync backend has been implemented and validated through compilation and API/database testing.

The implemented functionality includes:

* JWT authentication
* Route and stop management
* Trip creation
* Automatic seat creation
* Segment-based seat allocation
* Overlap detection
* Specific seat booking
* Database-level concurrency protection
* Booking cancellation
* FIFO waitlist
* Segment-aware waitlist promotion
* Validation
* Exception handling
* MySQL persistence

Build verification:

```bash
mvn-java25 clean compile
```

Result:

```text
BUILD SUCCESS
```

Repository:

```text
https://github.com/Anchalshukla145/ShuttleSync
```

```
```
