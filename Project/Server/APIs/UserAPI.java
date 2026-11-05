package Project.Server.APIs;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Project.Server.Classes.Restaurant;

public class UserAPI {
    private static final String DB_PATH = new File("reservation_com.db").getAbsolutePath();
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

   public static boolean validateUserLogin(String givenUsername, String givenPassword) {
        String query = "SELECT * FROM Users WHERE username = '" + givenUsername + "' AND password = '" + givenPassword + "';";

        try (Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            // Now run the main query
            try (ResultSet rs = stmt.executeQuery(query)) {
                // If a user is found with the given username and password, return true
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error validating user login: " + e.getMessage());
        }

        return false;
    }
}
