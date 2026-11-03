package Project.Server.APIs;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Project.Server.Classes.Restaurant;

public class DatabaseAPI {
    // Ensure DatabaseAPI uses the exact same absolute path as the initializer
    // This automatically generates the correct, native Windows path with proper backslashes
private static final String DB_PATH = new File("reservation_com.db").getAbsolutePath();
private static final String URL = "jdbc:sqlite:" + DB_PATH;

   public static List<Restaurant> listAllRestaurants() {
    List<Restaurant> restaurants = new ArrayList<>();
    String query = "SELECT * FROM Restaurants;";

    try (Connection conn = DriverManager.getConnection(URL);
         Statement stmt = conn.createStatement()) {

        stmt.execute("PRAGMA foreign_keys = ON;");

        // Now run the main query
        try (ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("restaurant_id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String phone = rs.getString("phone");
                double rating = rs.getDouble("rating");
                String address = rs.getString("address");

                Restaurant restaurant = new Restaurant(id, name, description, phone, rating, address);
                restaurants.add(restaurant);
            }
        }

    } catch (SQLException e) {
        System.out.println("Error listing restaurants: " + e.getMessage());
    }

    return restaurants;
}
}