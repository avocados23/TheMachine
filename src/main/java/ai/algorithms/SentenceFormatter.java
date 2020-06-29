package ai.algorithms;

import ai.Admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SentenceFormatter {

    private Connection myConn;

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

    public SentenceFormatter(String message) {
        // Load up inquiry words from database
        LinkedList<String> inquiryWords = new LinkedList<String>();
        try {
            connect();
            ResultSet rs = selectQuery("SELECT word from inquiry_words");
            while (rs.next()) {
                inquiryWords.add(rs.getString("word"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LinkedList<String> words = new LinkedList<String>(Arrays.asList(message.split("\\s+")));
        List<String> word_pool = Arrays.asList(message.split("\\s+"));
        StringBuilder sentence = new StringBuilder();

        // Capitalize the first word
        String word = word_pool.get(0);
        String capitalizedWord = word.substring(0,1).toUpperCase() + word.substring(1);
        sentence.append(capitalizedWord);

        for (int j=1; j<word_pool.size(); j++) {
            if (j <= word_pool.size()-1) {
                sentence.append(" ");
            }
            sentence.append(word_pool.get(j));
        }
        // adds last punctuation at the end

        // last character in the message
        String punctuation = message.substring(message.length()-1);

        // case: if there is proper punctuation in the sentence
        if (punctuation.equals("?") || punctuation.equals(".")) {
            sentence.append(message.substring(message.length()-1));
        } else {
            // case: missing proper punctuation
            // get first word
            String firstWord = message.substring(0, message.indexOf(" ")).toLowerCase();

            if (inquiryWords.contains(firstWord)) {
                // sub-case: sentence is a question but lacks a question mark
                sentence.append("?");
            } else {
                // sub-case: sentence is not a question mark but lacks a period
                sentence.append(".");
            }
        }
        System.out.println(sentence);
        close();
    }

    public static void main(String[] args) {
        new SentenceFormatter("did you take the dog out");
        new SentenceFormatter("i went to Miami");
    }
}
