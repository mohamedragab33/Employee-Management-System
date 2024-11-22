# Employee Management System

This is a Spring Boot-based application for managing employees within an organization. It allows CRUD operations on employee records, including validation of email addresses and department details. The application also sends email notifications upon employee creation.

---

## Features

- **Employee CRUD Operations**: Create, read, update, and delete employee records.
- **Email Validation**: Validates email addresses using an external API.
- **Department Validation**: Ensures employees are assigned to valid departments.
- **Email Notifications**: Sends an email to the new employee upon creation.
- **Retry Mechanisms**: Retries email sending in case of failure.
- **Logging**: Detailed logs for every operation.
- **Asynchronous Processing**: Email notifications are sent asynchronously.

---

## Technologies Used

- **Spring Boot**: Framework for building the application.
- **Spring Data JPA**: For database operations.
- **Hibernate**: ORM tool for database interactions.
- **Spring Retry**: To handle retries for email sending.
- **Thymeleaf**: Template engine for email templates.
- **Lombok**: To reduce boilerplate code.
- **JavaMailSender**: For sending emails.
- **Validation APIs**: Integration with [ZeroBounce](https://www.zerobounce.net) for email validation.

---

## Prerequisites

- **Java 17** or higher
- **Maven 3.2** or higher
- **MySQL Database**
- An API key from ZeroBounce for email validation

---

## Setup and Installation

### 1. Clone the Repository
```bash
git clone https://github.com/your-repo/employee-management-system.git
cd employee-management-system
