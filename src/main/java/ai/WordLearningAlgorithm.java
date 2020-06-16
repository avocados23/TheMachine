// Word learning algorithm created by Nam Tran
// Selenium command references
// http://the-internet.herokuapp.com/

package ai;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.*;

public class WordLearningAlgorithm {

    private WebDriver driver; // creates WebDriver object
    private Connection myConn; // creates connection to databas e

    /**
     * Connects to the database.
     *
     * @param none
     * @return none
     * @exception SQLException if a database access error occurs
     */
    public void connect() {

        // System.out.println("Establishing a connection to " + Admin.returnAdminUrl() + "...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // loads up driver to fully establish a potential connection can happen.

            // The following variables have been hidden-- to access these variables, go to the referenced class.
            String url = Admin.returnAdminUrl();
            String admin = Admin.returnAdminIdentity();
            String pwd = Admin.returnAdminPwd();

            myConn = DriverManager.getConnection(url, admin, pwd);
//			load();

        } catch (SQLException e) {
            throw new IllegalStateException("Error connecting to neural database.", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Closes the connection to the neural network database.
     *
     * @param none
     * @return none
     * @exception SQLException if a database access error occurs
     */
    public void close() {
        try {
            myConn.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Something went wrong trying to exit out.", e);
        }
    }

    /**
     * Updates a table autonomously. This can also be used for insert queries.
     *
     * @param String object of the specific query.
     * @return none
     * @exception SQLException if a database or query error occurs
     *
     */
    public void updateQuery(String query) {
        try {
            Statement stmt = myConn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Selects an individual record from a table autonomously.
     *
     * @param String object of the specific query.
     * @return ResultSet containing the individual data from that specific record.
     * @exception SQLException if a database or query error occurs
     *
     * NOTE: READ SOMEWHERE THAT PASSING A RESULTSET WITHIN A PUBLIC METHOD IS A BAD IDEA. In the future, we might need to revise this method utilizing Lists.
     */
    public ResultSet selectQuery(String query) {
        ResultSet rs = null;
        try {
            Statement stmt = myConn.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            throw new IllegalStateException("Something went wrong in gathering data from the neural network.", e);
        }
        return rs;
    }

    /**
     * Retrieves the primary key within a table's column structure.
     *
     * @param ResultSet retrieved from the getTableRecords() method and the table_name
     * @return String object that contains the primary key's column name
     * @exception SQLException if a database or query error occurs
     *
     */
    public String getPrimaryKey(ResultSet tableRecords) {
        String primaryKey = "";
        try {
            ResultSetMetaData rsmd = tableRecords.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Search for a primary key by finding it via auto increment
            for (int i=1; i<=columnCount; i++) {
                if (rsmd.isAutoIncrement(i)) {
                    primaryKey = rsmd.getColumnName(i);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return primaryKey;
    }

    /**
     * Retrieves the names of the columns.
     *
     * @param ResultSet tableRecords passed by the selectTableRecords() method.
     * @return List of Strings that contain each name of every column of the selected table.
     * @exception SQLException if a database or query error occurs.
     *
     */
    public List<String> getTableColumnNames(ResultSet tableRecords) {

        List<String> columnNameList = new ArrayList<String>();
        try {
            ResultSetMetaData rsmd = tableRecords.getMetaData(); // retrieves metadata from ResultSet from JDBC driver
            int columnCount = rsmd.getColumnCount(); // size of the amount of columns within the selected table

            for (int i=1; i <= columnCount; i++) { // iterate and find each label (column) within the table
                columnNameList.add(rsmd.getColumnName(i)); // insert it into the List that will be returned by this method
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Something went wrong in gathering the column names from the database.", e);
        }
        return columnNameList;
    }

    /**
     * Retrieves the data types of the columns.
     *
     * @param ResultSet tableRecords passed by the selectTableRecords() method.
     * @return List of Strings that contain the data type of every column of the selected table.
     * @exception SQLException if a database or query error occurs.
     *
     */
    public List<String> getTableColumnTypes(ResultSet tableRecords) {
        List<String> columnTypeList = new ArrayList<String>(); // empty List that will store the names of the types of columns

        try {
            ResultSetMetaData rs = tableRecords.getMetaData(); // retrieves the metadata of the table records

            // Now, we must iterate through the metadata to find out each column's specific data type

            List<String> columnNames = getTableColumnNames(tableRecords);

            for (int i=1; i<=columnNames.size(); i++) {
                columnTypeList.add(rs.getColumnTypeName(i));

                // Debugging purposes
                System.out.println("Data type of column " + rs.getColumnName(i) + ": " + rs.getColumnTypeName(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columnTypeList; // note that column types are returned as integers, not STRINGS
    }

    public WordLearningAlgorithm(String link) throws IOException {

        connect();
        LinkedList<String> wordList = new LinkedList<String>();
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver"); // connects the code to UNIX executable
        driver = new ChromeDriver();
        driver.get(link);

        try {
            synchronized (driver) {
                driver.wait(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        URL url = new URL(link); // casts String link into a URL object
        Scanner s = new Scanner(url.openStream());

        // Iterate through text file
        while (s.hasNextLine()) {
            Scanner s2 = new Scanner(s.nextLine());

            // Iterate through each word
            while (s2.hasNext()) {
                String word = s2.next().replaceAll("[^\\w]", "").toLowerCase();
//                System.out.println(word);

                if (wordList.size() == 0) {
                    wordList.addFirst(word); // declares first word as the head node
                } else {
                    wordList.add(word); // continuously adds the words
                }
            }
        }

        // Learn words from LinkedList
        for (int i=0; i<wordList.size(); i++) {

            // Local variables that will be used in the database I/O
            String word = wordList.get(i);
            String typeOfWord = "";
            StringBuilder typesOfWord = new StringBuilder();

            // Find word in the database
            ResultSet rs = selectQuery("SELECT frequency FROM words WHERE word = '" + word + "'"); // find word in database

             try {
                 if (rs.next()) { // if word exists within the database
                     int frequency = rs.getInt("frequency") + 1; // add counter to how many times the word has seen it
                     updateQuery("UPDATE words SET frequency = " + frequency + " WHERE word = '" + word + "'");
                 } else { // if word does not exist in the database
                     driver.get("https://dictionary.com/"); // open dictionary.com

                     WebElement searchTool = driver.findElement(By.id("searchbar_input")); // Find search button
                     searchTool.sendKeys(word); // search for word
                     driver.findElement(By.id("search-submit")).click(); // Click enter button

                     // Handle word types

                     synchronized(driver) {
                         driver.wait(2000);
                     }

                     List<WebElement> list = driver.findElements(By.className("luna-pos")); // Find all instances of the types of words
                     // Iterate through all the word types
                     for (WebElement el : list) {
                         // Search for first index of whitespace
                         String type = el.getText();
                         int firstWhiteSpace = type.indexOf(" ");
                         String nullVar = null;

                         // Find first word within the string
                         if (firstWhiteSpace == -1) { // if whitespace does not exist
                             typeOfWord = type.substring(0);
                         } else { // if whitespace does exist, get the first word within the String
                             typeOfWord = type.substring(0, firstWhiteSpace);
                         }

                         // typeOfWord switch statement handling
                         // convert to lowercase during comparison test

                         switch(typeOfWord.toLowerCase()) {
                             case "noun":
                                 typesOfWord.append("0");
                                 break;

                             case "verb":
                                 typesOfWord.append("1");
                                 break;

                             case "adjective":
                                 typesOfWord.append("2");
                                 break;

                             case "adverb":
                                 typesOfWord.append("3");
                                 break;

                             case "pronoun":
                                 typesOfWord.append("4");
                                 break;

                             case "preposition":
                                 typesOfWord.append("5");
                                 break;

                             case "determiner":
                                 typesOfWord.append("6");
                                 break;

                             case "interjection":
                                 typesOfWord.append("7");
                                 break;
                         }

                         synchronized (driver) {
                             driver.wait(500);
                         }
                     }

                     // Remove duplicates

                     char[] chars = typesOfWord.toString().toCharArray();
                     Set<Character> charSet = new LinkedHashSet<Character>();
                     for (char c : chars) {
                         charSet.add(c);
                     }

                     StringBuilder sb = new StringBuilder();
                     for (Character character : charSet) {
                         sb.append(character);
                     }
//                     System.out.println(sb.toString());

                     String query = "INSERT INTO `words` (word, type, frequency, protected) VALUES ('" + word + "', " + sb + ", 1, 0)";
                     System.out.println(query);
                     updateQuery(query);
                 }
             } catch (SQLException | InterruptedException e) {
                 e.printStackTrace();
             }
        }
        System.out.println("Finished my reading.");
        System.exit(1);
    }

    public static void main(String[] args) {

        try {
            new WordLearningAlgorithm("https://raw.githubusercontent.com/Patater/qso-generator/master/corpora/navy-seal-copypasta.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
