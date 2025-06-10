# Railway Reservation System

## Project Description

The Railway Reservation System is a console-based Java application designed to manage train schedules, user registrations, and ticket bookings. It supports both user and administrator functionalities, including user authentication, train management (add, view, modify, delete), ticket booking, cancellation, and waitlist processing. The system uses a simple file-based approach for data persistence and incorporates an event-driven mechanism for logging and notifications.

## Features

### User Features
* **User Registration:** Create a new user account with validated details (name, age, gender, email, phone).
* **User Login:** Securely log in to an existing user account.
* **Book Ticket:** Book tickets for available trains, with support for different travel classes and waitlist functionality if trains are full.
* **View My Tickets:** See a list of all booked and waitlisted tickets.
* **Cancel Ticket:** Cancel an existing ticket using its PNR.
* **Search Trains:** Find trains based on source and destination.
* **View All Trains:** Display all trains currently in the system.

### Admin Features
* **Admin Login:** Secure login for administrators.
* **Add Train:** Add new train details (ID, name, route, date, time, seats, fare) with input validation.
* **View All Trains:** Display comprehensive details of all trains.
* **Modify Train:** Update details of an existing train.
* **Delete Train:** Remove a train from the system.
* **Sort Trains:** Sort trains by various criteria for easier management.
* **Process Waitlist:** Confirm waitlisted tickets when seats become available due to cancellations.
* **View All Tickets:** See all tickets booked by all users.
* **Generate Admin Report:** Create a report of all tickets for administrative purposes.
* **Activity Logging:** All administrative actions (train additions, modifications, deletions) are logged to a dedicated file (`logs/admin_activity.log`).

### Core System Features
* **File-Based Data Persistence:** Stores user, train, ticket, and waitlist data in plain text files (`.txt`).
* **Input Validation:** Robust validation for all user and admin inputs to ensure data integrity.
* **Event Management System:** Notifies relevant components (e.g., booking notifications, admin activity logging) about significant system events.
* **Console Colors:** Enhances user experience with colored console output for better readability.

## Project Structure

The project is organized into logical packages:
├── Final Review/
│   ├── auth/
│   │   ├── AdminAuth.java         # Handles admin login.
│   │   └── AuthManager.java       # Manages user registration and login.
│   ├── dao/
│   │   ├── TicketDAO.java         # Data Access Object for Ticket operations.
│   │   └── TrainDAO.java          # Data Access Object for Train operations.
│   ├── events/
│   │   ├── EventListener.java     # Interface for event listeners.
│   │   ├── EventManager.java      # Manages event registration and dispatch.
│   │   └── RailwayEvent.java      # Enum for defining various system events.
│   │   └── listeners/
│   │       ├── AdminActivityLogger.java # Logs admin actions.
│   │       └── BookingNotificationListener.java # Notifies about booking/cancellation.
│   ├── main/
│   │   └── Main.java              # Main application entry point.
│   ├── model/
│   │   ├── Ticket.java            # Represents a ticket entity.
│   │   ├── Train.java             # Represents a train entity.
│   │   └── User.java              # Represents a user entity.
│   └── util/
│       ├── ConsoleColors.java     # Utility for ANSI console colors.
│       └── FileHelper.java        # Utility for file read/write operations.
├── data/                          # Directory for storing application data (users.txt, trains.txt, etc.)
├── logs/                          # Directory for storing logs (admin_activity.log)

✅ Prerequisites
Make sure you have:

Java JDK 8 or above installed
A terminal or command prompt
A text/code editor (e.g., VS Code, IntelliJ)


🚀 How to Run the Project
Follow these steps:

Clone or download the project to your local machine.

Open a terminal and navigate to the final review directory:

cd /your-path/to/FinalReview



##Compile the Java source files:

```bash
javac */*.java main/*.java

##Run the application
```bash
java main.Main
