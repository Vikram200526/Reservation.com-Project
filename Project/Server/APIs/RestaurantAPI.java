package Project.Server.APIs;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Statement;

import Project.Server.Classes.Restaurant;
import Project.Server.Classes.TimeSlot;

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

    public static ArrayList<String> getFoodCategoriesForRestaurant(int restaurantId) {
        ArrayList<String> foodCategories = new ArrayList<>();
        String query = "SELECT fc.name FROM FoodCategories fc JOIN RestaurantCategories rc ON fc.food_category_id = rc.food_category_id WHERE rc.restaurant_id = ?;";

        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, restaurantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    foodCategories.add(rs.getString("name"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching food categories for restaurant: " + e.getMessage());
        }

        return foodCategories;
    }

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


                    ArrayList<String> foodCategories = getFoodCategoriesForRestaurant(id);
                    Restaurant restaurant = new Restaurant(id, name, description, phone, rating, address, foodCategories);
                    restaurants.add(restaurant);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error listing restaurants: " + e.getMessage());
        }

        return restaurants;
    }

    public static List<String> GetAllFilters() {
        return new ArrayList<>(List.of("None", "Above Rating", "Below Rating", "Food Category"));
    }

    public static List<Restaurant> GetRestaurantsbyFilter(String SearchName, String FilterType, String FilterValue) {
        List<Restaurant> restaurants = new ArrayList<>();
        
        // Base SQL builder with WHERE 1=1 to safely chain optional conditions
        StringBuilder sql = new StringBuilder("SELECT DISTINCT r.* FROM Restaurants r");
        
        boolean isCategoryFilter = "Food Category".equals(FilterType);
        if (isCategoryFilter) {
            sql.append(" JOIN RestaurantCategories rc ON r.restaurant_id = rc.restaurant_id");
            sql.append(" JOIN FoodCategories fc ON rc.food_category_id = fc.food_category_id");
        }
        
        sql.append(" WHERE 1=1");
        
        // Check if a search name was provided
        boolean hasSearch = (SearchName != null && !SearchName.trim().isEmpty());
        if (hasSearch) {
            sql.append(" AND r.name LIKE ?");
        }
        
        // Check if an active secondary filter is applied
        boolean hasActiveFilter = FilterType != null && !FilterType.equals("None") && FilterValue != null && !FilterValue.trim().isEmpty();
        if (hasActiveFilter) {
            if (FilterType.equals("Above Rating")) {
                sql.append(" AND r.rating >= ?");
            } else if (FilterType.equals("Below Rating")) {
                sql.append(" AND r.rating <= ?");
            } else if (FilterType.equals("Food Category")) {
                sql.append(" AND fc.name LIKE ?");
            }
        }
        
        sql.append(";");

        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Bind SearchName parameter if present
            if (hasSearch) {
                pstmt.setString(paramIndex++, "%" + SearchName.trim() + "%");
            }

            // Bind FilterValue parameter if present
            if (hasActiveFilter) {
                if (FilterType.equals("Above Rating") || FilterType.equals("Below Rating")) {
                    try {
                        double ratingVal = Double.parseDouble(FilterValue.trim());
                        pstmt.setDouble(paramIndex++, ratingVal);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid numeric rating format: " + FilterValue);
                        return restaurants;
                    }
                } else if (FilterType.equals("Food Category")) {
                    pstmt.setString(paramIndex++, "%" + FilterValue.trim() + "%");
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("restaurant_id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String phone = rs.getString("phone");
                    double rating = rs.getDouble("rating");
                    String address = rs.getString("address");

                    ArrayList<String> foodCategories = getFoodCategoriesForRestaurant(id);
                    Restaurant restaurant = new Restaurant(id, name, description, phone, rating, address, foodCategories);
                    restaurants.add(restaurant);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error filtering restaurants: " + e.getMessage());
        }

        return restaurants;
    }

    public static Map<Integer, TimeSlot> getTimeSlotsForRestaurant(int restaurantId) {
        Map<Integer, TimeSlot> timeSlotsMap = new HashMap<>();
        String query = "SELECT * FROM TimeSlots WHERE restaurant_id = ?;";

        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, restaurantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int timeSlotId = rs.getInt("time_slot_id");
                    String date = rs.getString("date");
                    String time = rs.getString("time");
                    int tableSize = rs.getInt("table_size");
                    int tableCount = rs.getInt("table_count");

                    TimeSlot slot = new TimeSlot(timeSlotId, restaurantId, date, time, tableSize, tableCount);
                    timeSlotsMap.put(timeSlotId, slot); // Key: time_slot_id, Value: TimeSlot object
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching time slots for restaurant: " + e.getMessage());
        }

        return timeSlotsMap;
    }
}
