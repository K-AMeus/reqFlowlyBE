# ReqFlowly - LLM-Powered Requirements Engineering Tool

[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.2-green)](https://spring.io/projects/spring-boot)

ReqFlowly is a web-based application that automates the analysis of software requirements using artificial intelligence together with rule-based heuristics. It assists users with the identification of key domain entities and with the generation of use cases and test cases, significantly reducing manual effort and improving the accuracy of requirements.

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
- [Usage](#-usage)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)

## ✨ Features

ReqFlowly provides the following key features:

- **Domain Entity Extraction**: Automatically identify domain entities and their attributes from requirements.
- **Use Case Generation**: Create detailed and structured use case specifications from domain objects.
- **Test Case Generation**: Derive comprehensive test cases from the generated use cases.
- **PDF Support**: Upload and process requirements from PDF documents.
- **Requirements Management**: Manage and organize requirements within projects.
- **Export Functionality**: Export generated artifacts as PDF documents.
- **User Authentication**: Secure user login and registration.
- **Project Management**: Create, manage, and organize projects.

## 🏛 Technology stack

ReqFlowly follows a three-tier architecture:

1. **Front-end**: React.js (https://github.com/K-AMeus/reqFlowlyFE)
2. **Back-end**: Java, Spring Boot
3. **Database**: PostgreSQL
4. **AI**: Google Cloud Vertex AI (Gemini 2.5 Flash)

## 🚀 Getting Started

### Prerequisites

- JDK 21
- Gradle 8.x
- PostgreSQL 14+
- Google Cloud Platform account (for Vertex AI)

### Installation

1. **Clone the repository**

```bash
git clone https://github.com/K-AMeus/reqFlowlyBE
cd reqFlowlyBE
```

2. **Build the application**

```bash
./gradlew clean build
```

3. **Run the application**

```bash
./gradlew bootRun
```

Or using Docker:

```bash
docker build -t reqFlowlyBE .
docker run -p 8080:8080 reqFlowlyBE
```

### Configuration

In order to run the application, the following configuration variables have to be defined in the `application.yml` file in the `src/main/resources` directory:

```yaml

spring:
  # Database Configuration
  datasource:
    url: jdbc:postgresql://localhost:5432/reqflowly
    username: your_postgres_username
    password: your_postgres_password


# Firebase Configuration
FIREBASE_SERVICE_ACCOUNT: # Path to your Firebase service account JSON file or service account JSON as a string

# Vertex AI Configuration
VERTEXAI_LOCATION: us-central1
VERTEXAI_PROJECT_ID: your-gcp-project-id
VERTEXAI_SERVICE_ACCOUNT: your-vertex-ai-service-account

# Use case generation prompt
USE_CASE_PROMPT: # Create use case specifications for the following domain entities using this grammar...

# ANTLR grammar for use case generation
ANTLR_GRAMMAR: # useCase : ucID? ucName...

# Test case generation prompt
TEST_CASE_PROMPT: # Create test cases for the following use cases...

# Domain object extraction prompt
DOMAIN_EXTRACTION_PROMPT: # Extract domain entities and their attributes from the following requirements...
```

Make sure to replace all placeholder values with your actual configuration information. All these variables are required for the application to function properly as they are not available in the repository.

## 📖 Usage

The application provides a RESTful API that can be used to:

1. **Manage Projects**: Create, retrieve, update, and delete projects.
2. **Process Requirements**: Extract domain objects from text or PDF requirements.
3. **Generate Use Cases**: Create structured use case specifications from domain objects.
4. **Generate Test Cases**: Derive test cases from use cases.
5. **Export Artifacts**: Export generated artifacts as PDF documents.

## 📚 API Documentation

### Domain Object Generation

```
POST /api/domain-object-service/v1/generation/text
Content-Type: application/json
Authorization: Bearer {token}

{
  "requirements": "...",
  "customPrompt": "..."
}
```

```
POST /api/domain-object-service/v1/generation/pdf
Content-Type: multipart/form-data
Authorization: Bearer {token}

file: [PDF file]
customPrompt: (optional) "..."
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── reqflowly/
│   │           └── application/
│   │               ├── common/           # Common utilities and configurations
│   │               ├── config/           # Configuration files
│   │               ├── domainObject/     # Domain object generation
│   │               ├── pdf/              # PDF processing
│   │               ├── project/          # Project management
│   │               ├── requirement/      # Requirement management
│   │               ├── security/         # Authentication and authorization
│   │               ├── testCase/         # Test case generation
│   │               ├── useCase/          # Use case generation
│   │               └── Application.java  # Main application entry point
│   └── resources/
│       ├── db/                           # Database migration scripts
│       └── application.yml               # Application configuration
├── test/                                 # Test sources
└── build.gradle                          # Gradle build file
```
