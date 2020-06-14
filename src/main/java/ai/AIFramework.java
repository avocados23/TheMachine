// Functions that the AI framework will and can use will be coded here. Throughout the main driver,
// these functions can be referenced.

// Built by Nam Tran.

package ai;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.text.ParseException;

public class AIFramework {
	
	private String name = "The Machine"; // default name
	private String admin = ai.Admin.returnAdminIdentity();
	private Connection myConn;
	
	// CONNECTION FUNCTIONALITY METHODS -- DO NOT TOUCH THESE!
	// -----------------------------------------------------------------------------
	
	public void load() {
		int percentageLoaded = 0;
		
		while (percentageLoaded < 100) {
			percentageLoaded++;
			
			if (percentageLoaded < 100) {
				System.out.print("█");
			} else if (percentageLoaded == 100) {
				System.out.println();
				System.out.println("Success!");
				System.out.println("Connected to: " + Admin.returnAdminUrl() + " as " + Admin.returnAdminIdentity() + ".");
			}
		}
	}
	

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
	 * Prints out the information within a List.
	 *
	 * @param List object.
	 * @return none
	 * @exception none
	 */
	public void printListInfo(List<String> list) {
		System.out.println("Size of the list: " + list.size());
		for (int p=0; p<list.size(); p++) {
			System.out.println(list.get(p));
		}
	}

	/**
	 * Gets the current hour in 24-hour clock/military time.
	 *
	 * @param none
	 * @return int containing the current hour in military time
	 * @exception ParseException if there is a failure to parse the Date object.
	 */
	public int currentHour() {
		Calendar rightNow = Calendar.getInstance();
		Integer currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
		SimpleDateFormat df = new SimpleDateFormat("HH");
		
		Date date = null;
		String output = null;
		
		try {
			date = df.parse(currentHour.toString());
			output = df.format(date);
//			System.out.println(output);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(output);
	}
	// -----------------------------------------------------------------------------
	
	// Contingency methods -- these are hard-coded in as preliminary methods that I could test out so that the machine could learn from me.
	// Please do not touch these either, as these are the basic functions that allow the machine to communicate in its baby stages.
	// -----------------------------------------------------------------------------

	/**
	 * Returns the name of the administrator.
	 *
	 * @param none
	 * @return name of the administrator
	 * @exception none
	 */
	public String whoisAdmin() {
		return admin;
	}

	/**
	 * Returns the name of the AI framework. Its default name is "The Machine".
	 *
	 * @param none
	 * @return name of the AI framework
	 * @exception none
	 */
	public String whatismyName() {
		return name;
	}

	/**
	 * Changes the name of the AI framework.
	 *
	 * @param Scanner that is utilized as an I/O
	 * @return none
	 * @exception none
	 */
	public void changeAIName(Scanner input) {
		System.out.print("What would you like my new name to be? ");
		String inputtedName = input.nextLine();
		name = inputtedName;
		//input.close();
	}

	/**
	 * Terminates the program.
	 *
	 * @param none
	 * @return none
	 * @exception none
	 */
	public void terminate() {
		System.exit(1);
	}
	// -----------------------------------------------------------------------------
	
	
	// Database methods -- this will allow the machine to sift through its neural network and build upon its knowledge.
	// DO NOT TOUCH THESE!
	// -----------------------------------------------------------------------------
	
	// Table functions

	/**
	 * Creates a table from the console.
	 *
	 * @param the Scanner object that the program will listen with
	 * @return none
	 * @exception SQLException if a database or query error occurs
	 */
	public void createTable(Scanner input) throws SQLException {
		connect();
		System.out.print("What would you like this table name to be? "); // ask for table name
		String inputtedName = input.nextLine();
		
		// instantiated variable that will store the primary key column here
		String primaryKey = "";
		System.out.println("What are the columns you would like to add, " + whoisAdmin() + "? Please separate them into commas."); // ask for columns
		String inputtedColumns = input.nextLine();
		
		List<String> listColumns = new ArrayList<String>(Arrays.asList(inputtedColumns.split(","))); // split into individual String objects into the List
		
		int listSize = listColumns.size();
		
		// get rid of all white spaces and hidden keys (tab)
		for (int s=0; s<listSize; s++) {
			listColumns.set(s, listColumns.get(s).replaceAll("\\s", ""));
		}
		printListInfo(listColumns);
		
		if (listSize == 1) {
			System.out.println("There is only one column that is being created. By doing this, this will establish this column as the primary key.");
			
			String response = input.nextLine();
			
			if (response.contains("y")) {
				primaryKey = listColumns.get(0);
			}
		} else if (listSize > 1) {
			System.out.println("Which of these would you like to be the primary key? Pick by the given number.");
				
			for (int k=0; k<listSize; k++) {
				System.out.println(listColumns.get(k) + "(" + k + ")");
			}
				
			String primaryKeyInput = input.nextLine();
			primaryKey = listColumns.get(Integer.parseInt(primaryKeyInput));
		}
		List<String> listDataTypes = new ArrayList<String>();
		
		for (int i=0; i < listSize; i++) {
			String currentColumn = listColumns.get(i);
			
			System.out.println("What would you like the data type of column " + currentColumn +  " to be?");
			String inputtedDataType = input.nextLine();
			
			// varchar decision handling portion
			if(inputtedDataType.equalsIgnoreCase("varchar")) {
				System.out.println("Reminder that the default varchar limit is 255. Shall this be the case here?");
				String choice = input.nextLine();
				
				//varchar decisions: yes AND no to re-specifying the limit
				if (choice.contains("y")) {
					listDataTypes.add("VARCHAR (255)"); // add varchar as default 255 character limit column
				} else {
					System.out.println("What will the limit be on column " + currentColumn + "?");
					String specifyCharLimit = input.nextLine();
					
					while (Integer.parseInt(specifyCharLimit) > 255) {
						System.out.println("This is bigger than the specified limit. Please re-enter your value.");
						String specifyCharLimit2 = input.nextLine();
					}
					
					listDataTypes.add("VARCHAR (" + specifyCharLimit + ")");
					
				}
			} else {
				listDataTypes.add(inputtedDataType);
			}
		}
		
		String query_section1 = "CREATE TABLE " + inputtedName + " ";
		String query_section2 = "(";
		
		// Forming the create query...
		for (int j=0; j<listColumns.size(); j++) {
			if ((listColumns.size() == 1) || (j == listColumns.size() - 1)) {
				query_section2 += listColumns.get(j) + " " + listDataTypes.get(j); // if the for-loop is at the beginning or end of the statement
				
				if (listColumns.get(j).equals(primaryKey)) {
					query_section2 += " AUTO_INCREMENT";
				}
			} else if (j == 0 && listColumns.size() > 1) {
				query_section2 += listColumns.get(j) + " " + listDataTypes.get(j);
				
				if (listColumns.get(j).equals(primaryKey)) {
					query_section2 += " AUTO_INCREMENT, ";
				} else {
					query_section2 += ", ";
				}
			} else if (j > 0 && (j < listColumns.size() - 1)) {
				query_section2 += listColumns.get(j) + " " + listDataTypes.get(j);
				
				if (listColumns.get(j).equals(primaryKey)) {
					query_section2 += " AUTO_INCREMENT, ";
				} else {
					query_section2 += ", ";
				}
			}
		}
		
		// Debugging purposes
		Statement stmt = null;
		String query = query_section1 + query_section2 + ", PRIMARY KEY (" + primaryKey + "))";
//		System.out.println(query);
		
		try {
	        stmt = myConn.createStatement();
	        stmt.executeUpdate(query);
	    } catch (SQLException e ) {
	        throw new IllegalStateException("An error occurred during the creation of this SQL query: ", e);
//	    	e.printStackTrace();
	    } finally {
	        if (stmt != null) { 
	        	stmt.close(); 
	        }
	    }
	}

	/**
	 * Verifies that this table does exist. This method will be extremely useful for the following methods in executing SQL queries.
	 *
	 * @param the Scanner object that the program will listen with, and String table_name
	 * @return boolean tableExists indicating if the table indeed does exist
	 * @exception SQLException if a database or query error occurs
	 */
	public boolean verifyTable(Scanner input, String table_name) {
		boolean tableExists = false;
		try {
			DatabaseMetaData dbm = myConn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, table_name, null);
			if (tables.next()) {
			  // Table exists
				tableExists = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tableExists; // true if the table exists, false if the table does not exist
	}
	
	/**
	 * Selects the table and extracts all its records. This method also utilizes verifyTable().
	 *
	 * @param the Scanner object that the program will listen with, and String table_name 
	 * @return ResultSet containing all the records from that table.
	 * @exception SQLException if a database or query error occurs
	 * 
	 * NOTE: READ SOMEWHERE THAT PASSING A RESULTSET WITHIN A PUBLIC METHOD IS A BAD IDEA. In the future, we might need to revise this method utilizing Lists.
	 */
	public ResultSet selectTableRecords(Scanner input, String table_name) {
		
		ResultSet rs = null;
		
		if (verifyTable(input, table_name) == true) { // verifies that the table exists before running the code at all
			// select all records from the given table
			
			try {
				Statement stmt = myConn.createStatement();
				String query = "SELECT * FROM " + table_name; // select query to run utilizing the passed table name parameter
				rs = stmt.executeQuery(query); // executes the query
				
			} catch (SQLException e) {
				throw new IllegalStateException("Something went wrong in gathering data from the neural network.", e);
			}
			
		} else {
			System.out.println("The table you just mentioned does not exist in my databases.");
		}
		return rs;
	}
	
	// Autonomous database functions
	
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
	
	// Mutator methods for the database
	
	/**
	 * Inserts data into the table.
	 *
	 * @param Scanner input object and String table_name to determine which table it is inserting into.
	 * @return none
	 * @exception SQLException if a database or query error occurs.
	 * 
	 */
	public void insertIntoTable(Scanner input, String table_name) {
		
		if (verifyTable(input, table_name) == true) {
			ResultSet table = selectTableRecords(input, table_name); // records of selected table
			List<String> columnNames = getTableColumnNames(table); // column names within the selected table
			
			// Retrieve primary key
			String primaryKey = getPrimaryKey(table);
			
			// Now, time to autonomously build our SQL query...
			
			try {
				Statement stmt = myConn.createStatement();
				
				// Let's now build the SQL query here
				
				String query = "INSERT INTO " + table_name + "(";
				String separatorValue = "";
				StringBuilder strcat = new StringBuilder();
				strcat.append(query);
				
				// Iterate through and append the String query above
				for (int i=0; i<columnNames.size(); i++) {
					
					if (!columnNames.get(i).equals(primaryKey)) {
						strcat.append(separatorValue);
						strcat.append(columnNames.get(i));
						separatorValue = ",";
					}
				}
				
				separatorValue = ""; // resets the separator value back to a space.
				strcat.append(") VALUES ("); // connects SQL query column labels to its values
				
				// Ask the user what values they would like to put for the columns
				
				// Load up the data types of the columns here
				List<String> columnDataTypes = getTableColumnTypes(table);
				
				for (int j=0; j<columnNames.size(); j++) {
					
					if (!columnNames.get(j).equals(primaryKey)) {
						System.out.println("What value would you like to insert for column " + columnNames.get(j) + "? (requires a " + columnDataTypes.get(j) + ")");
						String columnValue = input.nextLine();
						strcat.append(separatorValue);
						
						String insertedData = columnValue;
						if (columnDataTypes.get(j).equalsIgnoreCase("varchar")) {
							insertedData = '"' + columnValue + '"';
						}
						strcat.append(insertedData);
						separatorValue = ",";
					}
				}
				strcat.append(")");
				
				// For debugging purposes
				System.out.print("Query written: ");
				System.out.println(strcat);
				stmt.executeUpdate(strcat.toString());
				
				
			} catch (SQLException e) {
				throw new IllegalStateException("Something went wrong with inserting data into the table " + table_name + ".", e);
			}
			
		}
		
	}
	
	/**
	 * Deletes information from a table, or the table itself.
	 *
	 * @param Scanner input object and String table_name to determine which table it is inserting into.
	 * @return none
	 * @exception SQLException if a database or query error occurs.
	 * 
	 */
	public void deleteTable(Scanner input, String table_name) {
		if (verifyTable(input, table_name) == true) {
			 
			// precaution on wanting to delete the table
			
			System.out.println("Are you sure you want to delete from this table? ");
			String verify = input.nextLine();
			
			if (verify.contains("y")) {
				try {
					Statement stmt = myConn.createStatement(); // creates a blank SQL statement for now
					
					// Now, see if they want to drop the entire table, or a specific piece of data
					
					System.out.println("Do you want to drop the entire table? ");
					String option = input.nextLine();
					
					if (option.contains("y")) { // if the user chooses to drop an entire table
						String query = "DROP TABLE " + table_name;
						stmt.executeUpdate(query);
						
						// For debugging purposes
						System.out.println("Successfully dropped table " + table_name);
					} else { // if the user chooses to drop only a select amount of data
						
						ResultSet tableRecords = selectTableRecords(input, table_name); // returns a result set
						String primaryKey = getPrimaryKey(tableRecords); // primary key
						
						System.out.println("What is the primary key value of the record that you would like to delete? ");
						String idInput = input.nextLine();
						
						String query = "DELETE FROM " + table_name + " WHERE " + primaryKey + " = " + idInput;
						
						// For debugging purposes
//						System.out.println(query);
						stmt.executeUpdate(query);
						
					}
				} catch (SQLException e) {
					throw new IllegalStateException("An error occurred when attempting to delete data from the database: ", e);
				}
			}
		}
	}
	
	/**
	 * Alters information from a table's record.
	 *
	 * @param Scanner input object and String table_name to determine which table it is altering data from.
	 * @return none
	 * @exception SQLException if a database or query error occurs.
	 * 
	 */
	public void alterTable(Scanner input, String table_name) {
	
		if (verifyTable(input, table_name) == true) {
			ResultSet tableRecords = selectTableRecords(input, table_name);
			String primaryKey = getPrimaryKey(tableRecords);
			List<String> columnNames = getTableColumnNames(tableRecords);
			
			try {
				ResultSetMetaData rsmd_table = tableRecords.getMetaData();
				for (int i=0; i<rsmd_table.getColumnCount(); i++) {
					System.out.println(columnNames.get(i) + "(" + i + ")");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Integer indexOfColumn = 0; // default
			Integer passedInput = 0;
			
			System.out.println("Which column would you like to edit from this table? Pick by the number. ");
			String inputtedColumn = input.nextLine();
			indexOfColumn = Integer.parseInt(inputtedColumn);
			
			System.out.println("What is the primary key value of the record you would like to alter? ");
			String idInput = input.nextLine();
			
			try {
				Statement stmt = myConn.createStatement();
				String selectQuery = "SELECT " + inputtedColumn + " FROM " + table_name + " WHERE " + primaryKey + " = " + idInput;
				ResultSet rs = stmt.executeQuery(selectQuery);
				
				ResultSetMetaData rsmd = rs.getMetaData();
				rs.next();
				
				// get column type
				passedInput += 1;
				System.out.println(passedInput);
				System.out.println(inputtedColumn);
				int columnType = rsmd.getColumnType(passedInput);
				
				// condition handling 
				if (columnType == 4 || columnType == -5) { // if columnType is an integer
					
					System.out.println("New value: ");
					String newValue = input.nextLine();
					
					String alterQuery = "UPDATE " + table_name + " SET " + columnNames.get(indexOfColumn) + " = '" + newValue + "' WHERE " + primaryKey + " = " + idInput;
					
					// Debugging purposes
					System.out.println(alterQuery);
					stmt.executeUpdate(alterQuery);
					
				} else if (columnType == 12) { // if columnType is a varchar
//					String columnInfo = rs.getString(inputtedColumn);
//					
//					System.out.println("Value that is in the current column: " + columnInfo);
					System.out.println("New value: ");
					String newValue = input.nextLine();
					
					String alterQuery = "UPDATE " + table_name + " SET " + columnNames.get(indexOfColumn) + " = " + newValue + " WHERE " + primaryKey + " = " + idInput;
					
					// Debugging purposes
//					System.out.println(alterQuery);
					stmt.executeUpdate(alterQuery);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	// -----------------------------------------------------------------------------
	// ALGORITHMS THAT ALLOW THE PROGRAM TO BE CAPABLE OF MACHINE LEARNING.
	// -----------------------------------------------------------------------------
	
	public void simpleSentenceAlgorithm(Scanner input) {
		
		// Format: build a simple sentence.
		
	}
	
	/**
	 * Word processing algorithm that breaks up the message into separate words stored in a String[] array.
	 *
	 * @param String message inputed by the computer or through the console
	 * @return String[] array with the message broken up into its word components
	 * @exception none
	 * 
	 */
	public String[] processMessageAlgorithm(String message) {
		
		String[] words = message.split("\\W+"); // removes non-alphabetic characters occurring one or more times
		
		for (int i=0; i< words.length; i++) {
			words[i] = words[i].replaceAll("[^\\w]", "").toLowerCase(); // makes word lowercase
		}
		return words;

	}
	
	/**
	 * Word learning algorithm (NOT AUTONOMOUS) that learns English through the console.
	 *
	 * @param Scanner input object and the message passed through as a String object
	 * @return none
	 * @exception SQLException if a database or query error occurs.
	 * 
	 */
	public void wordLearningAlgorithm(Scanner input, String message) {
		
		// words table structure, for reference
		// word VARCHAR (255), type int (11), tense int (11), ID int (11) PRIMARY KEY, frequency int (11), command VARCHAR (255)
		
		String[] words = message.split("\\W+"); // removes non-alphabetic characters occurring one or more times
		for (int i = 0; i < words.length; i++) {
		    words[i] = words[i].replaceAll("[^\\w]", "");
		    String word = words[i].toLowerCase();
		    ResultSet rs = selectQuery("SELECT frequency FROM words WHERE word = '" + word + "'"); // find word in database
		    
		    try {
		    	int protectedWord = 0; // false
		    	String functionInput = null;
		    	
		    	if (rs.next()) { // if the word exists within the database
		    		int frequency = rs.getInt("frequency") + 1;
		    		updateQuery("UPDATE words SET frequency = " + frequency + " WHERE word = '" + word + "'");
		    	} else {
		    		System.out.println("I do not recognize the word " + word + ". Is this a typo or a new word?");
		    		String console = input.nextLine();
		    		
		    		if (console.contains("typo") || console.contains("yes")) {
		    			System.out.println("Please print the correct spelling of this word.");
		    			String newWord = input.nextLine();
		    			word = newWord.toLowerCase();
		    		}
		    		System.out.println("What type of word is this?");
		    		System.out.println("0: noun -- e.g; dog");
		    		System.out.println("1: verb -- e.g; run");
		    		System.out.println("2: adjective -- e.g; happy");
		    		System.out.println("3: adverb -- e.g; sadly");
		    		System.out.println("4: pronoun -- e.g; He/she");
		    		System.out.println("5: preposition -- e.g; of, through, over, before, between");
		    		System.out.println("6: determiner -- e.g; the, those, that");
		    		System.out.println("7: interjection -- e.g; alas, amen, eureka");
		    			
		    		System.out.println();
		    		String typeWord = input.nextLine(); // convert this to an int
		    		int type = Integer.parseInt(typeWord);
		    		
		    		// Handle protected word case
		    		System.out.println("Is this word also a protected word?");
		    		String protectedStatus = input.nextLine();
		    		
		    		if (protectedStatus.contains("y") || protectedStatus.equalsIgnoreCase("yes")) {
		    			protectedWord = 1; // change to true
		    			
		    			System.out.println("What function would you like to invoke for this keyword?");
		    			functionInput = input.nextLine();
		    			
		    			
		    		}
		    			
		    		System.out.println("What tense of word is this?");
		    		System.out.println("0: present simple -- e.g; I study English");
		    		System.out.println("1: past -- e.g; I studied English");
		    		System.out.println("2: not applicable -- not a verb");
		    			
		    		System.out.println();
		    		String tenseWord = input.nextLine();
		    		int tense = Integer.parseInt(tenseWord);
		    		String query = "INSERT INTO words (word, type, tense, frequency, protected, command) VALUES ('" + word + "', " + type + ", " + tense + ", 1, " + protectedWord + ", " + "' " + functionInput + "')";
		    		
		    		// For debugging purposes
//		    		System.out.println(query);
		    		
		    		updateQuery(query);
		    	}
		    } catch (SQLException e) {
		    	e.printStackTrace();
		    }
		    
		}
	}
	
	/**
	 * Uses the `words` table to form a Map object that connects the protected keywords to the commands wanted to invoke.
	 *
	 * @param none
	 * @return Map<String, LinkedList<String>> connecting the keywords (key) to its commands (values)
	 * @exception SQLException if a database or query error occurs.
	 * 
	 */
	public Map<String, LinkedList<String>> formProtectedMapAlgorithm() {
		
		Map<String, LinkedList<String>> wordsMap = new HashMap<String, LinkedList<String>>(); // HashMap that will map out the commands (key) to its protected words (values)
		
		// Select query to find all data records of `command` in the words table
		String query_commands = "SELECT word, command FROM `words` WHERE protected = 1";
		ResultSet rs_commands = selectQuery(query_commands);
		
		try {
			while (rs_commands.next()) { // Iterate through the SQL table
				
				LinkedList<String> wordsList = new LinkedList<String>(); // creates LinkedList that will store the words
				String command = rs_commands.getString("command"); // get the command 
				String key_word = rs_commands.getString("word"); // get the word
				
				// condition: if command does not exist as a key, add it as a node...
				if (!wordsMap.containsKey(command)) {
					wordsList.addFirst(key_word); // add to the beginning of the LinkedList
					wordsMap.put(command, wordsList);				
				} else {
					// condition: if command does exist as a key BUT does not contain the value linked to the key
					if (wordsList.indexOf(key_word) == -1) {
						wordsList.add(key_word); // adds keyword that will activate the command (value to key)
					}
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return wordsMap;
	}
	
	/**
	 * Autonomous word learning algorithm -- machine learning algorithm
	 *
	 * @param none
	 * @return none
	 * @exception SQLException if a database or query error occurs.
	 * 
	 */
	public void autonomousLearningAlgorithm(String message) {
		// ...
	}
	/**
	 * Greeting algorithm determining what phrase to say to the administrator.
	 *
	 * @param Scanner input object.
	 * @return the appropriate greeting in a String object
	 * @exception SQLException if a database or query error occurs.
	 * 
	 * note: this algorithm will be built upon with a randomizer on which phrase to say.
	 * 
	 */
	public String greetingAlgorithm(Scanner input) {
		String greeting = "";
		int currentHour = currentHour();
//		System.out.println(currentHour());
		
		try {
			ResultSet tableRecords = selectTableRecords(input, "greetings");
			
			while (tableRecords.next()) {	
				if ((currentHour >= 0 && currentHour < 6) || (currentHour >= 18 && currentHour <= 23)) { // between the hours of 12-6am, 6pm-11pm
					greeting = tableRecords.getString(1);
					break;
				} else if ((currentHour >= 6 && currentHour < 12)) { // between the hours of 6am to 12pm
					greeting = tableRecords.getString(1);
					break;
				} else if ((currentHour >= 12 && currentHour < 18)) { // between the hours of 12pm to 6pm
					greeting = tableRecords.getString(1);
					break;
				}
				
				// Debugging purposes
				if (greeting.isEmpty()) {
					System.out.println("greeting is empty");
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return greeting + ", " + admin + ".";
	}
	
	// Debugging methods
	// DO NOT TOUCH THESE!
	// -----------------------------------------------------------------------------

	public void showMapContents(Map<String, LinkedList<String>> example) {
		 example.entrySet().forEach(entry->{
			    System.out.println(entry.getKey() + " " + entry.getValue());  
			 });
	}
	
	// -----------------------------------------------------------------------------
	
	// Driver method
	public AIFramework() {
		
		// loadup functions
		
		connect();
		
		Scanner sc = new Scanner(System.in);
		System.out.println(greetingAlgorithm(sc));
		
		while (true) {
			
			// listen...
			
			// Map will be reformed and recreated on every new loadup.
			Map<String, LinkedList<String>> protectedWords = formProtectedMapAlgorithm();
//			showMapContents(protectedWords);
			
			String inputtedText = sc.nextLine();
			
			wordLearningAlgorithm(sc, inputtedText);
			
			// These are the contingency prompts
			// We can revise the contingency prompts by forming it into a binary decision tree
			
			if (inputtedText.contains("change") && inputtedText.contains("name")) { // Changing the program's default name
				changeAIName(sc);
			}
			if (inputtedText.contains("whoami")) { // Testing the "whoami" method
				System.out.println("You are " + whoisAdmin());
			}
			if (inputtedText.contains("whoareyou")) { // Testing the "whoareyou" method
				System.out.println(whatismyName());
			}
			if (inputtedText.contains("bye") || inputtedText.contains("Good night") || inputtedText.contains("exit")) { // Exit method
				System.out.println("Bye, " + whoisAdmin());
				terminate();
			}
			if (inputtedText.contains("create") && inputtedText.contains("table")) { // Creates a table
				try {
					createTable(sc);
				} catch (SQLException e) {
					System.err.println(e);
				}
				
			}
			if (inputtedText.contains("insert") && (inputtedText.contains("table") || inputtedText.contains("data"))) { // Inserts data into a table
				System.out.println("What table would you like to insert into?");
				String input = sc.nextLine();
				insertIntoTable(sc, input);
			}
			if (inputtedText.contains("alter") && inputtedText.contains("data")) { // Inserts data into a table
				System.out.println("What table would you like to change data in? ");
				String input = sc.nextLine();
				alterTable(sc, input);
			}
			if (inputtedText.contains("delete") && inputtedText.contains("table")) { // Inserts data into a table
				System.out.println("What table would you like to delete?");
				String input = sc.nextLine();
				deleteTable(sc, input);
			}
			if (inputtedText.contains("table") && inputtedText.contains("exist")) { // Checks if a table exists
				System.out.println("What table do you want to see exists?");
				String input = sc.nextLine();
				if(verifyTable(sc, input) == true) {
					System.out.println("Yes, it does.");
				} else {
					System.out.println("No, it does not exist.");
				}
			}
		}
	}
}
