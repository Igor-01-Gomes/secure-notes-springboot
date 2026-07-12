# Secure Notes - Spring Boot

## Overview

Secure Notes is a console-based application built with Spring Boot that
allows users to securely create, organize, and manage personal notes. 
The project demonstrates authentication, role-based authorization, password
encryption, and database persistence using a clean service-oriented
architecture.

## Features

-   User registration and login
-   BCrypt password hashing
-   Role-based authorization (USER and ADMIN)
-   Create, list, update and delete personal notes
-   Admin account automatically created on startup
-   MySQL persistence using Spring Data JPA
-   Environment variable configuration
-   Console-based interactive interface

## Tech Stack

-   Java 21
-   Spring Boot 3.3.5
-   Spring Security
-   Spring Data JPA
-   MySQL
-   Maven
-   BCrypt
-   dotenv-java

## Architecture

``` text
Console Application
        │
        ▼
Authentication Service
        │
        ▼
Note Service
        │
        ▼
Spring Data JPA
        │
        ▼
MySQL Database
```

## Project Structure

``` text
src
├── config
├── console
├── model
├── repository
└── service
```

## Security

-   Passwords are stored using BCrypt hashing.
-   Authentication is performed before any user operation.
-   Users can only manage their own notes.
-   Administrators can remove any stored note.

## Environment Variables

Create a `.env` file in the project root (or configure the variables
directly in your operating system).

``` env
DB_URL=jdbc:mysql://localhost:3306/secure_notes
DB_USERNAME=root
DB_PASSWORD=your_password

ADMIN_USERNAME=admin
ADMIN_PASSWORD=change_this_password
```

These variables are loaded by the application at startup and keep
sensitive information outside the source code. For production
environments, avoid storing credentials directly in
`application.properties`.

## Getting Started

### Requirements

-   Java 21
-   Maven
-   MySQL

### Configuration

Configure your database in `application.properties` and provide
administrator credentials using environment variables.

### Run

``` bash
mvn spring-boot:run
```

## Learning Objectives

This project was created to practice:

-   Spring Boot fundamentals
-   Spring Security
-   Password encryption
-   JPA repositories
-   Layered architecture
-   Clean service separation
-   Database persistence

## Contact

**Igor Gomes**  
DevOps Engineer | Java Developer 

**Email:** [igor.gomes.u@gmail.com](mailto:igor.gomes.u@gmail.com)  
**LinkedIn:** [Igor Gomes](https://www.linkedin.com/in/igor-gomes-5b6184290)
