# ai -- The Machine

[![Generic badge](https://img.shields.io/badge/java-11.0+-blue.svg)](https://shields.io/)
[![Generic badge](https://img.shields.io/badge/mysql-8.0+-red.svg)](https://shields.io/)
[![Generic badge](https://img.shields.io/badge/selenium-3.141.59-green.svg)](https://shields.io/)


This is my machine learning side project that I created to practice and expand my knowledge on the mentioned topic and of course, including algorithms. 
It uses a MySQL database to store its data and then utilizes the functions and methods I have coded to sort that data out. 
This also gave me practice into working with more advanced data structures, such as Trees and Maps. However, you may feel free to use another database platform, but you will have to adjust certain SQL queries to match the format of your desired database platform within the autonomous and non-autonomous methods within AIFramework.java.

---

## Getting Started

You can clone my repository to a local remote folder within your computer through Git to load my files. I used Homebrew to manage my library and package downloads.

### Prerequisites

You will need:
* [Java](https://www.oracle.com/java/technologies/javase-downloads.html) SE 11.0, minimum
* [MySQL](https://www.mysql.com/downloads/)
* [Git](https://git-scm.com/downloads)
* [A database client](https://www.sequelpro.com/)
* [Google Chrome](https://www.google.com/chrome/)

Currently, I am using [IntelliJ IDEA](https://www.jetbrains.com/idea/) to manage and track my Java packages and project. I am using Sequel Pro to manage the contents of my MySQL database as well. Google Chrome is also required as it is the browser of choice for The Machine when accessing information from the Internet.

**It is heavily preferred that you use the IntelliJ IDEA for ease of dependency and external library management,** 
***because the following instructions I wrote on this README assume that you are using IntelliJ IDEA.***

This is because compared to IntelliJ, other IDEs such as Eclipse do not come with the JAR libraries required for The Machine to conduct its machine learning. Libraries that do not come with Eclipse are Guava and Apache Maven, where we will use the latter for our dependency management.

### Libraries

This program utilizes several external libraries to run.
* [JDBC (Java Database Connectivity)](https://dev.mysql.com/downloads/connector/j/)
* [Selenium WebDriver](https://www.selenium.dev/projects/) -- imported through Maven dependency

### Executable Files

In order for the beta testing suite to work, along with Selenium, you will need:
* [ChromeDriver](https://chromedriver.chromium.org/downloads)

---

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
CREATE TABLE `machineinfo` (name VARCHAR (255), ID int AUTO_INCREMENT, PRIMARY KEY (ID))
INSERT INTO `machineinfo` (name) VALUES ("The Machine")
```

Run these through your database client's query line.

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

---

## Running the Test Suite

The test suite was created so that The Machine could run each function autonomously to assess for any errors within
the framework's code. This can be done by running the program through TestSuite.java's main method.

## Word Learning Algorithm

As this README is being updated on June 15th, 2020, the program is capable of learning words autonomously by passing it a .txt file from an online source. The word learning algorithm is located in WordLearningAlgorithm.java. To pass the online text file for the algorithm to read and add to its neural network, go to the main method of the Java program and pass it in the dedicated parameter.

```
try {
  new WordLearningAlgorithm(YOUR TEXT FILE HERE)
} catch (IOException e) {
  ...
 ```
 
 more coming soon...

## License

[![GPLv3 license](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://github.com/n1413704/ai/blob/master/LICENSE)

Distributed under the GNU Lesser General Public License v3.0. See LICENSE for more information.
