package Project.Server.APIs;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Project.Server.Classes.Restaurant;
import Project.Server.Classes.User;

public class UserAPI {
    private static final String DB_PATH = new File("reservation_com.db").getAbsolutePath();
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

   public static User getUserAccount(String givenUsername, String givenPassword) {
        String query = "SELECT * FROM Users WHERE username = '" + givenUsername + "' AND password = '" + givenPassword + "';";

        try (Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            // Now run the main query
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");

                    User user = new User(userId, username, password, name, email, phone);
                    return user;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error validating user login: " + e.getMessage());
        }

        return null;
    }

    public static boolean checkUsernameAvailable(String givenUsername) {
        String query = "SELECT COUNT(*) FROM Users WHERE username = '" + givenUsername + "';";

        try (Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            // Now run the main query
            try (ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error checking username availability: " + e.getMessage());
        }

        return false;
    }

    public static boolean createUserAccount(String username, String password, String name, String email, String phone) {
    String sql = "INSERT INTO Users (username, password, name, email, phone) VALUES (?, ?, ?, ?, ?);";

    try (Connection conn = DriverManager.getConnection(URL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(true);

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }
}
