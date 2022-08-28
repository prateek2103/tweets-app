# Tweets-app

## Introduction:
- TweetsApp is a backend REST API which allows user to create new accounts and add or reply to a tweet.
- TweetsApp is integrated with spring security to provide authenticated endpoints and uses widely used no SQL database (Mongodb)
- It used Apache Kafka as the event streaming platform to post new tweets which provide great performance and ayncronous communication

### Tools and technology:
- Spring boot
- Spring security
- Apache Kafka
- JWT
- Mongodb

### Swagger UI
- Swagger UI provides an easy way to go through all the endpoints and have a gist of the application.
<img width="1438" alt="Screenshot 2022-08-28 at 1 25 40 PM" src="https://user-images.githubusercontent.com/30109806/187065817-d91c301f-527b-4c07-98b5-071921535943.png">

### JWT Authentication
- The api uses JWT token for security.

### Authentication flow diagram
<img width="991" alt="Screenshot 2022-08-28 at 2 22 24 PM" src="https://user-images.githubusercontent.com/30109806/187065912-50cb9557-78b2-4e15-8d90-f6a7a6da5cee.png">

### Code analysis and coverage
- For maintaining code quality and coverage , sonarqube is used by the application.
<img width="1442" alt="image" src="https://user-images.githubusercontent.com/30109806/187065974-bb92b69d-8e20-442b-9cd3-b2927e8e7130.png">

