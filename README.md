Appoinment Sheduler Application Build By Weranga Rathnayaka

---

# Appointment Scheduler

Appointment Scheduler is a web application designed to manage appointments between providers and customers. It allows for easy scheduling, tracking, and management of appointments, along with features such as appointment lifecycle management, notifications, and account types differentiation.

## Table of Contents

1. [Setup](#setup)
2. [Account Types](#account-types)
3. [Booking Process](#booking-process)
4. [Appointments Lifecycle](#appointments-lifecycle)
5. [Notifications](#notifications)
6. [Built With](#built-with)

## Setup

### 1. Clone the application

```bash
git clone https://github.com/Weranga-Rathnayaka/AppointmentScheduler.git
```

### 2. Create MySQL database

```sql
CREATE DATABASE appointmentscheduler;
```
After that, run the MySQL script to create tables `src/main/resources/appointmentscheduler.sql`.

### 3. Configure environment variables

Open `src/main/resources/application.properties`:

- Set environment variables for JDBC `dbURL`, `dbUsername`, `dbPassword`.
- Set environment variables for mail server `mailUsername`, `mailPassword`.
- Set `jwtSecret` encoded with Base64.

### 4. Run the app using Maven

```bash
mvn spring-boot:run
```
The app will start running at [http://localhost:8080](http://localhost:8080).

### 5. Login to admin account

- **Username**: admin
- **Password**: qwerty123

## Account Types

- **Admin**: Can add new providers, services, manage appointments, providers, customers, and issue invoices.
- **Provider**: Can set working plans, manage services, and view own appointments.
- **Customer Retail**: Can book and manage appointments.

## Booking Process

To book a new appointment, customers need to:

1. Choose desired work from available works list.
2. Choose provider for selected work.
3. Choose an available date.
4. Confirm the booking.

## Appointments Lifecycle

Every appointment has its own status:

- **Scheduled**: New appointment is created.
- **Finished**: Current date is after appointment end time.
- **Confirmed**: Current date is 24h after appointment end time.
- **Invoiced**: Invoice for appointment is created.
- **Canceled**: Customer cancels the appointment.
- **Rejection Requested**: Customer requests rejection.
- **Rejection Accepted**: Provider accepts rejection.

## Notifications

Email and SMS notifications are sent for various events including appointment status changes, new appointments, cancellations, and invoice issuance.

Email templates can be found in `src/main/resources/templates/email`.

## Built With

- Fullcalendar: A JavaScript event calendar.
- FlyingSaucer: Used to generate invoice PDF.
- jjwt: Used to generate/validate JWT tokens.

---

This README.md provides comprehensive information about setting up, using, and understanding the Appointment Scheduler application. You can customize it further to include badges, additional sections, or any specific instructions relevant to your project.
