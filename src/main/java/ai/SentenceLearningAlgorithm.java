// DEPRECATED BUT ITS SOURCE CODE WILL BE PRESERVED UNTIL SOMEONE ELSE WANTS TO FIX THIS.

// Problems with this algorithm:
// 1) Grammarly.com misidentifies improper sentences as proper sentences, and thus the Machine learns incorrect grammar.
// 2) There seems to be a IndexOutOfBoundsException that frequently pops up, and I have yet to identify the exact reason why.
// 3) There also seems to be a heap error exception when looking for the whitespaces when trying to dissect the sentence towards the end.

// ----------------------------------------------------------------------------------------------------------------------------------------
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
    private final String link = "https://www.grammarly.com/grammar-check";

    /**
     * Checks if a LinkedList<Object> contains duplicates within the List
     *
     * @param LinkedList<Object> list
     * @return true if it does contain duplicates; false if it does not
     */
    private boolean checkForDuplicates(LinkedList<String> list) {
        // Dump the LinkedList into a Set
        Set<String> set = new HashSet<String>(list);
        // returns boolean
        return set.size() < list.size();
    }

    private Set<String> addNewWord(Set<String> set, String word) throws SQLException {

        int wordToPick = ThreadLocalRandom.current().nextInt(1, tableSize("words") + 1);
        ResultSet rs = selectQuery("SELECT word, type FROM `words` WHERE id = " + wordToPick);
        rs.next();

        // If the Set contains the word that is trying to be added
        if (set.contains(word)) {
            // Generate a new word
            set.add(rs.getString("word"));
            addNewWord(set, rs.getString ("word"));
        } else {
            return set;
        }
        return set;
    }

    /**
     * Replaces all the duplicates within the LinkedList<String> words.
     *
     * @param LinkedList<String> list containing the word pool
     * @param list
     * @return String object that contains the primary key's column name
     * @exception SQLException if a database or query error occurs
     *
     */
    private LinkedList<String> replaceDuplicates(LinkedList<String> list) {

        // Remember the size of the original LinkedList so that the method knows how many new words to add
        final int n = list.size();

        // if the List does contain duplicates
        if (checkForDuplicates(list)) {
            // Convert the list into a HashSet to remove duplicates
            Set<String> set = new HashSet<String>(list);

            // Find number of words required to make a new list
            int numWordsRemoved = n - set.size();

            while (n < set.size()) {
                int wordToPick = ThreadLocalRandom.current().nextInt(1, tableSize("words") + 1); // index of word to use
                ResultSet rs = selectQuery("SELECT word, type FROM `words` WHERE id = " + wordToPick);

                try {
                    rs.next();
                    String newWord = rs.getString("word");
                    int newWordType = rs.getInt("type");

                    // case: if this word actually already exists in the Set, find a new one
                    // note: this is a recursive method
                    addNewWord(set, newWord);
                } catch (SQLException e) {
                    throw new IllegalStateException("Something went wrong in adding a new unique word.", e);
                }
            }

            // Now, convert Set back into the List
            return new LinkedList<String>(set);
        } else {
            // the List does not contain duplicates
            return list;
        }
    }

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

        System.setProperty("webdriver.chrome.driver", "resources/chromedriver"); // connects the code to UNIX executable
        driver = new ChromeDriver();

        for (int j=0; j<sentenceSize; j++) {
            int wordToPick = ThreadLocalRandom.current().nextInt(1, size + 1); // index of word to use
            ResultSet rs = selectQuery("SELECT word, type FROM words WHERE id = " + wordToPick); // retrieves the word and type from the generated index
            boolean duplicate = false;

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
        LinkedList<String> modified = replaceDuplicates(words);

        // Spit out the old words that would be utilized
        System.out.println("Original set of words: " + words.toString());
        System.out.println("New set of words: " + modified.toString());

        // Permutation algorithm here
        Permute sentences = new Permute(modified.toArray());

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

            for (int j=1; j<modified.size(); j++) {
                if (j <= modified.size()-1) {
                    sentence.append(" ");
                }
                sentence.append(word_pool[j]);
            }
            sentence.append(".");

            // Open up Google Chrome and begin entering
            driver.get(link);

            synchronized (driver) {
                driver.wait(1000);
            }
            WebElement input = driver.findElement(By.className("_3CFUFrV"));
            WebElement submit = driver.findElement(By.className("_3moEmWh"));
            input.sendKeys(sentence);
            submit.click();

            // Allow web-page to catch up
            synchronized (driver) {
                driver.wait(2000);
            }

            List<WebElement> successMsg = driver.findElements(By.className("cNZCfE5"));
            List<String> keys = new ArrayList<String>();
            List<Integer> orderList = new ArrayList<Integer>();
            StringBuilder orderStructure = new StringBuilder();
            System.out.println(successMsg.size());
            System.out.println("Sentence inputted; " + sentence.toString());
            if (successMsg.size() > 0) {
                // Insert the keys from the HashedBidiMap into the ArrayList
                for (String key: wordTypeMap.keySet()) {
                    keys.add(key);
                }

                // Retrieve the indexes of each word and place them into an ArrayList
                for (int i=0; i<modified.size()-1; i++) {
                    orderList.add(sentence.indexOf(keys.get(i)));
                }

                // Then, reorder them in ascending order
                Collections.sort(orderList);

                // Find all instances of whitespace and store them in this array
                int indexWhiteSpace = sentence.indexOf(" ");
                List<Integer> indexList = new ArrayList<Integer>();

                while (indexWhiteSpace >= 0) {
                    indexList.add(indexWhiteSpace);
                    System.out.println("List size: " + indexList.size());
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
        System.out.println("There are " + permSize + " possible permutations with these " + modified.size() + " words.");

    }
    public static void main (String[] args) throws IOException, InterruptedException {
        SentenceLearningAlgorithm algorithm = new SentenceLearningAlgorithm();
    }
}
