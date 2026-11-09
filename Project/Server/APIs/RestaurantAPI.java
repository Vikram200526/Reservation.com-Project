package Project.Server.APIs;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

import Project.Server.Classes.Restaurant;

public class RestaurantAPI {
    // RestaurantAPI would provide methods that other java files could call
    // example "CoolMethod()"
    // CoolMethod would take some user input, and manipulate it into some information
    // then it would use that information to run some sql query
    // once the sql query returns some data, CoolMethod could do some final manipulation of the results
    // finally returns the finished result to the java file that requested it


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

    public static List<String> GetAllFilters() {
        return new ArrayList<>(List.of("Restaurant Name", "Above Rating", "Below Rating", "Food Category"));
    }

    public static List<Restaurant> GetRestaurantsbyFilter(String FilterType, String FilterValue) {
        List<Restaurant> restaurants = new ArrayList<>();
        if (FilterType == null || FilterValue == null) {
            return restaurants;
        }

        String sql = "";
        boolean isNumericRating = false;

        // Determine the query structure based on the filter type
        switch (FilterType) {
            case "Restaurant Name":
                sql = "SELECT * FROM Restaurants WHERE name LIKE ?;";
                break;
                
            case "Above Rating":
                sql = "SELECT * FROM Restaurants WHERE rating >= ?;";
                isNumericRating = true;
                break;
                
            case "Below Rating":
                sql = "SELECT * FROM Restaurants WHERE rating <= ?;";
                isNumericRating = true;
                break;
                
            case "Food Category":
                // Join Restaurants -> RestaurantCategories -> FoodCategories
                sql = "SELECT DISTINCT r.* FROM Restaurants r " +
                    "JOIN RestaurantCategories rc ON r.restaurant_id = rc.restaurant_id " +
                    "JOIN FoodCategories fc ON rc.food_category_id = fc.food_category_id " +
                    "WHERE fc.name LIKE ?;";
                break;
                
            default:
                System.out.println("Unknown filter type: " + FilterType);
                return restaurants;
        }

        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Bind the appropriate parameter type safely
            if (isNumericRating) {
                try {
                    double ratingVal = Double.parseDouble(FilterValue.trim());
                    pstmt.setDouble(1, ratingVal);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid numeric rating format: " + FilterValue);
                    return restaurants;
                }
            } else {
                // Use wildcards for partial matching on names and categories (e.g., "Pizza")
                pstmt.setString(1, "%" + FilterValue.trim() + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
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
            System.out.println("Error filtering restaurants: " + e.getMessage());
        }

        return restaurants;
    }
}
