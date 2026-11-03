package Project.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.File;



public class DatabaseInitializer {
    private static final String DB_PATH = new File("reservation_com.db").getAbsolutePath();
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    public static void initializeDatabase() {
        boolean dbExists = new File(DB_PATH).exists();

        if (!dbExists) {
            System.out.println("Initializing fresh SQLite database with table counts...");
            
            // Connection 1: Create schema and seed data
            try (Connection conn = DriverManager.getConnection(URL)) {
                conn.setAutoCommit(false);

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                    executeSchemaAndSeed(stmt);
                }

                conn.commit(); 
                System.out.println("Database successfully created and seeded!");

            } catch (SQLException e) {
                System.out.println("Database initialization error: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            System.out.println("Existing `reservation_com.db` found. Connected successfully.");
        }
    }

    private static void executeSchemaAndSeed(Statement stmt) throws SQLException {
        // Drop tables in correct dependency order (child tables first)
        stmt.execute("DROP TABLE IF EXISTS Reservations;");
        stmt.execute("DROP TABLE IF EXISTS RestaurantCategories;");
        stmt.execute("DROP TABLE IF EXISTS TimeSlots;");
        stmt.execute("DROP TABLE IF EXISTS Restaurants;");
        stmt.execute("DROP TABLE IF EXISTS FoodCategories;");
        stmt.execute("DROP TABLE IF EXISTS Users;");

        // 1. Users Table
        stmt.execute("CREATE TABLE Users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "password TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "phone TEXT);");

        // 2. Restaurants Table
        stmt.execute("CREATE TABLE Restaurants (" +
                "restaurant_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "phone TEXT, " +
                "rating REAL, " +
                "address TEXT);");

        // 3. FoodCategories Table
        stmt.execute("CREATE TABLE FoodCategories (" +
                "food_category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT);");

        // 4. RestaurantCategories Junction Table
        stmt.execute("CREATE TABLE RestaurantCategories (" +
                "restaurant_id INTEGER, " +
                "food_category_id INTEGER, " +
                "PRIMARY KEY (restaurant_id, food_category_id), " +
                "FOREIGN KEY (restaurant_id) REFERENCES Restaurants(restaurant_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (food_category_id) REFERENCES FoodCategories(food_category_id) ON DELETE CASCADE);");

        // 5. TimeSlots Table
        stmt.execute("CREATE TABLE TimeSlots (" +
                "time_slot_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "restaurant_id INTEGER NOT NULL, " +
                "date TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "table_size INTEGER NOT NULL, " +
                "table_count INTEGER NOT NULL, " +
                "FOREIGN KEY (restaurant_id) REFERENCES Restaurants(restaurant_id) ON DELETE CASCADE);");

        // 6. Reservations Table
        stmt.execute("CREATE TABLE Reservations (" +
                "reservation_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "restaurant_id INTEGER NOT NULL, " +
                "time_slot_id INTEGER NOT NULL, " +
                "status TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (restaurant_id) REFERENCES Restaurants(restaurant_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (time_slot_id) REFERENCES TimeSlots(time_slot_id) ON DELETE CASCADE);");

        System.out.println("Tables created. Inserting seed data...");

        // Seed Restaurants individually
        stmt.executeUpdate("INSERT INTO Restaurants (name, description, phone, rating, address) VALUES ('The Rustic Spatula', 'Cozy farm-to-table dining featuring a seasonal local menu.', '555-0101', 4.70, '123 Main St, New Paltz NY');");
        stmt.executeUpdate("INSERT INTO Restaurants (name, description, phone, rating, address) VALUES ('Sakura Sushi & Ramen', 'Fresh hand-rolled sushi and rich, flavorful bowls of ramen.', '555-0202', 4.85, '456 Main St, New Paltz NY');");
        stmt.executeUpdate("INSERT INTO Restaurants (name, description, phone, rating, address) VALUES ('El Toro Taqueria', 'Authentic street tacos, sizzling fajitas, and house-made salsas.', '555-0303', 4.60, '789 Church St, New Paltz NY');");
        stmt.executeUpdate("INSERT INTO Restaurants (name, description, phone, rating, address) VALUES ('Slice of Italy Pizzeria', 'Wood-fired artisanal pizzas and classic comfort pasta dishes.', '555-0404', 4.75, '321 Huguenot St, New Paltz NY');");
        stmt.executeUpdate("INSERT INTO Restaurants (name, description, phone, rating, address) VALUES ('Green Leaf Bistro', 'Plant-based grain bowls, organic salads, and fresh juices.', '555-0505', 4.40, '654 North Front St, New Paltz NY');");

        System.out.println("Seed data insertion complete.");
    }


}