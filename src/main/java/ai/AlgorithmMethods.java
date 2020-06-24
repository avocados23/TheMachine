package ai;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmMethods {

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

    /**
     * Retrieves the size of the table.
     *
     * @param String object containing the name of the table
     * @return int that represents the size of the table
     * @exception SQLException if a database or query error occurs.
     *
     */
    public int tableSize(String table) {
        ResultSet rs = selectQuery("SELECT COUNT(*) FROM " + table);
        int size = 0;

        if (rs != null) {
            try {
                while (rs.next()) {
                    size = rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return size;
    }

}
