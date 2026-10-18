# LAPR3 DB Access example

This example works for JDK 8 with the following Oracle dependency:

```xml
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>19.8.0.0</version>
    <type>jar</type>
</dependency>
```

## DB Creation Tutorial

### Create Virtual Database

Use the provided tutorial [Oracle Virtual Server Tutorial](documentation/db-scripts/Oracle-Virtual-Server-Tutorial.docx).

### Create the New Database Schema

Login with SQLDeveloper, with the following credentials:

    host: vsgate-s1.dei.isep.ipp.pt
    port: 10522 (it may have a different number)
    service: xepdb1
    user: System
    password: oracle

    url:jdbc:oracle:thin:@//vsgate-s1.dei.isep.ipp.pt:10522/xepdb1

To create the new Database schema use the following script 
[Schema Create Script](documentation/db-scripts/schema-create-script.sql).

Replace "LAPR3_G000" by your team ID and "mypassword" by the password you want to use.

### Table Create Script 

Login with SQLDeveloper, with the following credentials:

    host: vsgate-s1.dei.isep.ipp.pt
    port: 10522 (it may have a different number)
    service: xepdb1
    user: LAPR3_G000
    password: mypassword

    url:jdbc:oracle:thin:@//vsgate-s1.dei.isep.ipp.pt:10522/xepdb1

Replace "LAPR3_G000" by your team ID and "mypassword" by the password you want to use.

To create the tables used by thus example, run the following script [Table Create Script](documentation/db-scripts/table-create-script.sql). 

## Configure the database connection string on your Java App

Change the values in the [application.properties](src/main/resources/application.properties) file.