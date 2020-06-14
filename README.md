# ai -- The Machine

This is my artificial intelligence side project that I created to practice and expand my knowledge on machine learning and algorithms. 
It uses a MySQL database to store its data and then utilizes the functions and methods I have coded to sort that data out. 
This also gave me practice into working with more advanced data structures, such as Trees and Maps. However, you may feel free to use another database platform, but you will have to adjust certain SQL queries to match the format of your desired database platform within the autonomous and non-autonomous methods within AIFramework.java.

## Getting Started

You can clone my repository to a local remote folder within your computer through Git to load my files.

### Prerequisites

You will need:
* Java SE 11.0, minimum
* MySQL
* Git
* A database client

Currently, I am using IntelliJ IDEA to manage and track my Java packages and project. I am using Sequel Pro to manage the contents of my MySQL database as well. 

** It is heavily preferred that you use the IntelliJ IDEA for ease of dependency and external library management. **

### Libraries

This program utilizes several external libraries to run.
* [JDBC (Java Database Connectivity)] (https://dev.mysql.com/downloads/connector/j/)
* Selenium WebDriver 

### Executable Files

In order for the beta testing suite to work, along with Selenium, you will need:
* [ChromeDriver] (https://chromedriver.chromium.org/downloads)

## Configuring The Machine

Before you can get The Machine up and running, you must set up its database with the following structure.

### Setting Up Your Database

First and foremost, create an empty database.

```
CREATE DATABASE ai_db
```

In the Admin.java file, you should see the following variables:

```
private static String url = ...
private static String user = ...
private static String pwd = ...
```

Configure these settings with the account that you would like the program to be able to use to access the contents of the database. It is preferred that you use the Sequel Pro client to access the database. Refer to the JDBC documentation on writing the correct URL to go to when accessing your database.

### Sequel Pro Connection Details -- Standard/Socket
```
Name: localhost
Host: 127.0.0.1
Username: ...
Password: ...
Database: ...
Port: 3306
```
You can connect to the database through SSL also; it is not required.

### Setting Up Your Tables

Before running the program, deactivate the word learning algorithm within the AIFramework() method, else your code will fail and return a SQLException. Follow these steps and set up the following tables:

``` 
CREATE TABLE `greetings` (phrase VARCHAR (255), ID int AUTO_INCREMENT, PRIMARY KEY (ID))
CREATE TABLE `words` (word VARCHAR (255), type int, tense int, ID AUTO_INCREMENT, frequency int, protected int, command VARCHAR (255), PRIMARY KEY (ID))
```

You may run these through a query on your database client or through the console of the program. It is up to you.

### Setting Up Your Commands

Now that you are done setting up the tables for the database, you may turn on the word learning algorithm.
Take a look within the AIFramework.java main method. You will see the contingency prompts that I hard-coded.
Before you can use these commands, the word learning algorithm will ask you to identify these words. The words
that are used within the contigency prompts that refer to commands are protected words. Thus,
when you are asked if this word is a protected word, type "y" or "yes".

```
>> whoami
I do not recognize the word whoami. Is this a typo or a new word?
>> new word
...
>> You are admin
```

more to come...
