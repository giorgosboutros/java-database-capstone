## Architecture Summary

This Spring Boot application uses both MVC and REST controllers.  
Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules.  
The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions).  
All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories.  
MySQL uses JPA entities while MongoDB uses document models.

## Data and Control Flow

1. User accesses AdminDashboard or Appointment pages.
2. The request is routed to the appropriate Thymeleaf or REST controller.
3. The controller delegates the action to the service layer.
4. The service layer contains the business logic and interacts with repositories.
5. Repositories fetch or update data in MySQL or MongoDB.
6. The results are passed back from repository → service layer → controller.
7. Controller returns data either to Thymeleaf templates (for dashboards) or as JSON response (for REST APIs).
