// Genetic programming algorithm created by Nam Tran
// This program will use the library of words it knows to form a sentence, and will not stop until it successfully registers a proper sentence.
// Note: if there are missing indexes (with primary key IDs, an SQLException will be thrown.

package ai;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
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
    private final String link = "https://www.gingersoftware.com/grammarcheck";

    public SentenceLearningAlgorithm() throws IOException, InterruptedException { // wordSize represents how many words will be used to form a sentence.
        connect(); // Establishes a connection to the database

        // Get 'words' table metadata here to set up gene pool for the algorithm
        boolean foundSentenceStructure = false; // boolean that dictates when the algorithm will end running
        int size = tableSize("words"); // size of the `words` table

        int sentenceSize = ThreadLocalRandom.current().nextInt(3, 8 + 1); // select a random number that will dictate the maximum amount of words allowed to be used in forming a sentence
        LinkedList<String> words = new LinkedList<String>(); // creates a LinkedList that will store the words
        BidiMap<String, Integer> wordTypeMap = new DualHashBidiMap<String, Integer>();

        // Debugging purposes
//        System.out.println("Sentence size: " + sentenceSize);
//        System.out.println("Size of table `words`: " + size);

        for (int j=0; j<sentenceSize; j++) {
            int wordToPick = ThreadLocalRandom.current().nextInt(1, size + 1); // index of word to use
            ResultSet rs = selectQuery("SELECT word, type FROM words WHERE id = " + wordToPick); // retrieves the word and type from the generated index

            // Debugging purposes
//            System.out.println("Chosen word at index " + wordToPick);
//            System.out.println();
            try {
                rs.next();
                String word = rs.getString("word");
                int type = rs.getInt("type");
                wordTypeMap.put(word, type);

                // case: if the LinkedList is empty, establish the first word as the head node
                if (words.size() == 0) {
                    words.addFirst(word);
                } else {
                    // case: head node is already established, hence just continually add to the tail of the LinkedList
                    rs.next();
                    words.add(word);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Spit out the words that will be utilized
        System.out.println(words.toString());

        // Permutation algorithm here
        Permute sentences = new Permute(words.toArray());

        int permSize = 0;
        while(sentences.hasNext()) {
            permSize++;

            // Form possible sentence here
            String[] word_pool = sentences.toArray();
            StringBuilder sentence = new StringBuilder();

            // Capitalize the first word
            String word = word_pool[0];
            String capitalizedWord = word.substring(0,1).toUpperCase() + word.substring(1);
            sentence.append(capitalizedWord);

            for (int j=1; j<sentenceSize; j++) {
                if (j <= sentenceSize-1) {
                    sentence.append(" ");
                }
                sentence.append(word_pool[j]);
            }
            sentence.append(".");
            System.out.println(sentence.toString());

            // Open up Google Chrome and begin entering
            System.setProperty("webdriver.chrome.driver", "resources/chromedriver"); // connects the code to UNIX executable
            driver = new ChromeDriver();
            driver.get(link);

            WebElement input = driver.findElement(By.className("ginger-grammarchecker-panel-area ginger-grammarchecker-panel-area-input"));
            WebElement submit = driver.findElement(By.className("btn btn-primary btn-lg ginger-grammarchecker-try"));
            input.sendKeys(sentence);
            submit.click();

            // Allow web-page to catch up
            synchronized (driver) {
                driver.wait(2000);
            }

            List<WebElement> successMsg = driver.findElements(By.className("ginger-grammarchecker-panel-area-suggestion-empty ginger-grammarchecker-panel-area-suggestion-nomistake"));
            List<String> keys = new ArrayList<String>();
            List<Integer> orderList = new ArrayList<Integer>();
            StringBuilder orderStructure = new StringBuilder();

            if (successMsg.size() > 0) {
                // Insert the keys from the HashedBidiMap into the ArrayList
                for (String key: wordTypeMap.keySet()) {
                    keys.add(key);
                }

                // Retrieve the indexes of each word and place them into an ArrayList
                for (int i=0; i<sentenceSize; i++) {
                    orderList.add(sentence.indexOf(keys.get(i)));
                }

                // Then, reorder them in ascending order
                Collections.sort(orderList);

                // Find all instances of whitespace and store them in this array
                int indexWhiteSpace = sentence.indexOf(" ");
                List<Integer> indexList = new ArrayList<Integer>();

                while (indexWhiteSpace >= 0) {
                    indexList.add(indexWhiteSpace);
                }

                // Now, reorder the original ArrayList of words in the proper word
                for (int m=0; m<keys.size(); m++) {
                    if (m < keys.size() - 1) {
                        int indexOfWord = orderList.get(m);
                        int whitespace = indexList.get(m);
                        keys.set(m, sentence.substring(indexOfWord, whitespace));
                    }
                }
                int lastIndexOfWord = orderList.get(orderList.size()-1);
                keys.set(keys.size()-1, sentence.substring(lastIndexOfWord, sentence.length()-1));

                // Now, utilize the ArrayList elements as keys for the Map and line up the order of the types
                for (int k=0; k<wordTypeMap.size(); k++) {
                    String key = keys.get(k);
                    int index = wordTypeMap.get(key);
                    orderStructure.append(index);
                }

                // Insert order into database
                Integer finalOrder = Integer.valueOf(orderStructure.toString());

                // For debugging purposes
                String query = "INSERT INTO `sentencestructure_memory` (structure) VALUES (" + finalOrder + ")";
                System.out.println(query);
                updateQuery(query);
                break;
            }
            sentences.next();
        }
        System.out.println("There are " + permSize + " possible permutations with these " + sentenceSize + " words.");

    }
    public static void main (String[] args) throws IOException, InterruptedException {
        SentenceLearningAlgorithm algorithm = new SentenceLearningAlgorithm();
    }
}
