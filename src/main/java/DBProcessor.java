/**
 * Created by aprots on 31.05.17.
 */

import java.sql.*;

class DBProcessor {

    private static Connection con;
    private static boolean hasData = false;

    ResultSet displayRecords() throws SQLException, ClassNotFoundException {

        if (con == null) {
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM records");

        return res;
    }

    void addRecord(Date date, double price, String currency, String product) throws SQLException, ClassNotFoundException {

        if (con == null) {
            getConnection();
        }

        PreparedStatement prep = con.prepareStatement("INSERT INTO records VALUES (?,?,?,?,?);");
        prep.setDate(2, date);
        prep.setDouble(3, price);
        prep.setString(4, currency);
        prep.setString(5, product);
        prep.execute();
    }


    void removeRecords(Date date) throws SQLException, ClassNotFoundException {

        if (con == null) {
            getConnection();
        }

        PreparedStatement prep = con.prepareStatement("DELETE FROM records WHERE day = ?");
        prep.setDate(1, date);
        prep.execute();
    }

    private void getConnection() throws ClassNotFoundException, SQLException {

        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:SQLiteTest1.db");
        initialise();

    }

    private void initialise() throws SQLException {

        if (!hasData) {
            hasData = true;

            Statement state = con.createStatement();
            ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name = 'records'");

            if (!res.next()) {

                Statement state2 = con.createStatement();
                state2.execute("CREATE TABLE records (id INTEGER, day DATE, price DECIMAL(18,2),currency VARCHAR(60), product VARCHAR(60), PRIMARY KEY(id));");

            }
        }

    }
}
