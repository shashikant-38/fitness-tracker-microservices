# Fitness Tracker Microservices

A scalable Fitness Tracking Application built using Spring Boot Microservices Architecture.

## Features

* User Management Service
* Activity Tracking Service
* AI Recommendation Service
* Eureka Service Discovery
* RESTful APIs
* MongoDB Integration
* Kafka-based Event Communication
* Microservices Architecture

## Tech Stack

* Java 21
* Spring Boot
* Spring Cloud Eureka
* Spring Data MongoDB
* Apache Kafka
* Maven
* Git & GitHub

## Microservices

### User Service

Manages user registration, profiles, and authentication-related operations.

### Activity Service

Tracks user fitness activities such as workouts, calories burned, and exercise history.

### AI Service

Provides personalized fitness recommendations based on user activity data.

### Eureka Server

Handles service registration and discovery between microservices.

## Project Structure

fitness-tracker-microservices

├── userservice

├── activityservice

├── aiservice

└── eureka

## Architecture

Client → Microservices → MongoDB

↓

Eureka Service Discovery

↓

Kafka Event Communication

## Running the Project

### Clone Repository

```bash
git clone https://github.com/shashikant-38/fitness-tracker-microservices.git
```

### Start Services

1. Start Eureka Server
2. Start User Service
3. Start Activity Service
4. Start AI Service
5. Verify services are registered in Eureka Dashboard

## Future Enhancements

* API Gateway
* Docker Containerization
* JWT Authentication
* Monitoring with Prometheus and Grafana
* Kubernetes Deployment
* CI/CD Pipeline
