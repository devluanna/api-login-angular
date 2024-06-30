# Backend API for 'Login with Angular/Typescript' project
This API was developed for an Angular project (where I developed a front with a login screen with Authentication and Authorization)
<br />
💻 Technologies used:
- Java 17
- Spring Boot Framework
- Maven
- In-memory database using H2

📶 The idea behind the system is: an HR system, which centralizes people.<br />

✨ This is a project for my personal development as FullStack, using Java on the backend, and Angular with Typescript on the front end

``` mermaid

classDiagram
    class User {
        -firstName: String
        -lastName: String
        -email: String
        -username: String
        -password: String
        -identity: String
        -status: String
        -profileRole: String
        -Dashbboard[] dashboard
    }

    class Dashboard {
        -Informations[] informationsBasics
    }


   User "1" *-- "1" Dashboard

```

<br />
💻 Technologies used in the frontend:<br />
- Angular
<br />
- Typescript
<br />
✅ Front-End Repository: https://github.com/devluanna/project-angular-login
<br />
<br />

![image](https://github.com/devluanna/api-login-angular/assets/119416976/3df70018-30fe-48cf-a8ef-789ba510e4a0)

