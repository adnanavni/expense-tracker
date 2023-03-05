# Expense Tracker
This is a simple desktop app to track your expenses. It is a group project made for OTP1 course in Metropolia UAS. The group consisted of 4 engineering students. 
The purpose of the course was learning agile frameworks. We took turns for scrum master position. The scrum master period was 2 weeks.

The subject chosen for the project was a expense tracker app, which can be used for tracking your personal expenses. The user can make budjets and extract the expenses
from those budjets. It is also possible to check your income and add your tax rate, it doesn't matter if you have an hourly wage or a monthly wage, because both features
are included. The user can keep track of his expenses with graphs.

The software is made with Java. GUI is made with JavaFX. The tools used for these are IntelliJ IDEA and SceneBuilder. The apps dependencies and building is made with Maven.
The software connects to a MYSQL database made with MariaDB. The database is located in Metropolia UAS educloud server, but you can use your own computer to host the database, 
because Metropolia VPN connection is required for the cloud database. The test are made with JUnit 5.

## Instructions
You can get started by cloning this repository and building it in IntelliJ with Java version 16 or higher. You have to make an .evn file 
```src/main/java/fi/metropolia/expensetracker/datasource``` in this folder. The .env file must contain the following variables 

```.env
DB_name=expensetracker
DB_username=someUserName
DB_password=somePassword
```

You can setup your local database with the databaseSetup.sql file.

After this is all done you can just run MainApplication.java

## Team
* [Adnan Avni](https://github.com/adnanavni)
* [Roope Kylli](https://github.com/roopeky)
* [Ilona Juvonen](https://github.com/IlonaJuv)
* [Perttu Harvala](https://github.com/800010179)
