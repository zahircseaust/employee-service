# Employee Service

A Spring Boot-based employee management API built with Java and JPA.

## Overview
This project provides a simple and flexible REST API for managing employees. It supports employee listing, search, filtering, pagination, sorting, and creation with validation and consistent error handling.

## Features
- Get all employees
- Get employee by ID
- Search employees by name, email, or employee code
- Filter by department and status
- Apply date range filters
- Pagination and sorting
- Create new employee records
- Validation and custom exception handling

## Tech Stack
- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database
- Maven

## Project Structure
- `src/main/java` — application source code
- `src/main/resources` — configuration and data files
- `README.md` — project documentation

## Run the Application
Requirements:
- Java 17+
- Maven

```bash
mvn spring-boot:run
