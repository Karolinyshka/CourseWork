package coursework.model;


import java.sql.*;
import org.sqlite.SQLiteConfig;

public class DatabaseConnection {

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig configuration = new SQLiteConfig();
            configuration.enforceForeignKeys(true);
            configuration.setBusyTimeout(String.valueOf(900000));
            Connection conn = DriverManager.getConnection("jdbc:sqlite:Library.db", configuration.toProperties());
            return conn;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e);
            return null;
        }
    }


}
