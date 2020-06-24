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

public class WordLearningAlgorithm extends AlgorithmMethods {

    private WebDriver driver; // creates WebDriver object
    private Connection myConn; // creates connection to database

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
                String word = s2.next().replaceAll("[^\\w]+", "").toLowerCase();
//                System.out.println(word);

                if (wordList.size() == 0) {
                    wordList.addFirst(word); // declares first word as the head node
                } else {
                    wordList.add(word); // continuously adds the words
                }
            }
        }

        // Learn words from LinkedList
        for (String word : wordList) {

            // Local variables that will be used in the database I/O
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

                    synchronized (driver) {
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
                        switch (typeOfWord.toLowerCase()) {
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
//                         synchronized (driver) {
//                             driver.wait(500);
//                         }
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

                    int sizeOfTypes = sb.length();
                    for (int m = 0; m < sizeOfTypes; m++) {
                        String query = "INSERT INTO `words` (word, type, frequency, protected) VALUES ('" + word + "', " + sb.substring(m, m + 1) + ", 1, 0)";
                        System.out.println(query);
                        updateQuery(query);
                    }

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