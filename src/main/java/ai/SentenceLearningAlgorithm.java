// Genetic programming algorithm created by Nam Tran

// This program will use the library of words it knows to form a sentence, and will not stop until it successfully registers a proper sentence.

package ai;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SentenceLearningAlgorithm extends AlgorithmMethods {

    private WebDriver driver;
    private Connection myConn;
    private String link = "https://www.gingersoftware.com/grammarcheck";

    public SentenceLearningAlgorithm() throws IOException { // wordSize represents how many words will be used to form a sentence.
        connect(); // Establishes a connection to the database

        // Get 'words' table metadata here to set up gene pool for the algorithm
        boolean foundSentenceStructure = false; // boolean that dictates when the algorithm will end running
        int size = tableSize("words"); // size of the `words` table

//        while (!foundSentenceStructure) {
//            int sentenceSize = ThreadLocalRandom.current().nextInt(2, 8 + 1); // select a random number that will dictate the maximum amount of words allowed to be used in forming a sentence
//            LinkedList<String> words = new LinkedList<String>(); // creates a LinkedList that will store the words
//            int numbersUsed[] = new int[sentenceSize]; // instantiates an array that will store the indexes of the words being used to prevent duplicates
//            int counter = 0;
//
//            while (words.size() < sentenceSize) {
//                int wordToPick = ThreadLocalRandom.current().nextInt(1, size + 1); // index of word to use
//                ResultSet rs = selectQuery("SELECT word, type FROM words WHERE id = " + wordToPick); // retrieves the word and type from the generated index
//                if (words.size() == 0) {
//                    try {
//                        String word = rs.getString("word");
//                        words.add(word);
//                        numbersUsed[counter] = rs.getInt("id");
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }

        int sentenceSize = ThreadLocalRandom.current().nextInt(2, 8 + 1); // select a random number that will dictate the maximum amount of words allowed to be used in forming a sentence
        LinkedList<String> words = new LinkedList<String>(); // creates a LinkedList that will store the words
        int numbersUsed[] = new int[sentenceSize]; // instantiates an array that will store the indexes of the words being used to prevent duplicates
        int counter = 0;

        while (words.size() < sentenceSize) {
            int wordToPick = ThreadLocalRandom.current().nextInt(1, size + 1); // index of word to use
            ResultSet rs = selectQuery("SELECT word, type FROM words WHERE id = " + wordToPick); // retrieves the word and type from the generated index
            try {
                if (words.size() == 0) {
                    rs.next();
                    String word = rs.getString("word");
                    words.addFirst(word);
                    numbersUsed[counter] = wordToPick;
                    counter++;
                } else {
                    rs.next();
                    String word = rs.getString("word");
                    words.add(word);
                    numbersUsed[counter] = wordToPick;
                    counter++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Debugging purposes

        // Spits out the contents of the LinkedList<String> to see what words will be utilized
        for (int i=0; i<words.size(); i++) {
            System.out.println(words.get(i));
            System.out.println("index in table at: " + numbersUsed[i]);
        }

        System.setProperty("webdriver.chrome.driver", "resources/chromedriver"); // connects the code to the UNIX executable file
        driver = new ChromeDriver();
        driver.get(link);

    }
    public static void main (String[] args) throws IOException {
        SentenceLearningAlgorithm algorithm = new SentenceLearningAlgorithm();
    }
}
