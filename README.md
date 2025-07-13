# Task Management System 🗂️

A java full-stack **Task Management System** built with **Spring Boot** that allows users to manage tasks efficiently with features like user roles, authentication, and task assignment.

## 🚀 Features

- ✅ User registration & login (JWT based)
- 👥 Role-based access: `TEAM_MEMBER`, `TEAM_MANAGER`
- 📋 Create, update, delete tasks
- ⏰ Timestamp tracking for task creation
- 🔐 Secure REST APIs using Spring Security


## 🛠️ Tech Stack

- **Backend**: Java, Spring Boot, Spring Security, Spring Data JPA
- **Database**: MySQL / H2 (for testing)
- **Build Tool**: Maven
- **Authentication**: JWT (JSON Web Token)


## 📁 Project Structure
 ```text
TaskManagementSystem_UsingSpringBoot
├── src
│ ├── main
│ │ ├── java
│ │ │ └── com.task.TaskManager
│ │ │ ├── controller
│ │ │ ├── model
│ │ │ ├── repository
│ │ │ ├── service
│ │ │ └── config
│ └── resources
│ └── application.properties
├── pom.xml
└── README.md

```

## 🔧 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/arshiyachauhan/TaskManagementSystem_UsingSpringBoot.git
   cd TaskManagementSystem_UsingSpringBoot

2. Set up MySQL or use H2 in application.properties

3. Run the project:

```bash
Copy
Edit
./mvnw spring-boot:run
