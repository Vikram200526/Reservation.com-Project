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
                "username TEXT NOT NULL UNIQUE, " +
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
        stmt.executeUpdate("INSERT INTO Restaurants (name, description, phone, rating, address) VALUES ('Hudson Valley Smokehouse', 'Slow-smoked barbecue ribs, pulled pork, and hearty southern sides.', '555-0606', 4.65, '987 Main St, New Paltz NY');");
        stmt.executeUpdate("INSERT INTO Restaurants (name, description, phone, rating, address) VALUES ('The Artisan Baker & Café', 'Artisan sourdough breads, gourmet pastries, and specialty espresso drinks.', '555-0707', 4.80, '234 Huguenot St, New Paltz NY');");
        stmt.executeUpdate("INSERT INTO Restaurants (name, description, phone, rating, address) VALUES ('Thai Basil Garden', 'Traditional curries, fragrant noodle dishes, and fresh herb-infused salads.', '555-0808', 4.55, '567 Church St, New Paltz NY');");
        stmt.executeUpdate("INSERT INTO Restaurants (name, description, phone, rating, address) VALUES ('Ridgeview Steakhouse & Grill', 'Prime-cut steaks, grilled seafood, and an extensive local wine selection.', '555-0909', 4.90, '890 North Front St, New Paltz NY');");
        stmt.executeUpdate("INSERT INTO Restaurants (name, description, phone, rating, address) VALUES ('Mediterranean Olive', 'Gyros, falafel wraps, crisp Greek salads, and house-made hummus.', '555-1010', 4.50, '112 Main St, New Paltz NY');");
        //
        //
        // Seed Generic Food Categories
        stmt.executeUpdate("INSERT INTO FoodCategories (name, description) VALUES ('Pizza', 'Wood-fired, artisanal, and classic cheese/pepperoni pizzas.');");
        stmt.executeUpdate("INSERT INTO FoodCategories (name, description) VALUES ('Burgers', 'Juicy beef, chicken, and plant-based burger patties.');");
        stmt.executeUpdate("INSERT INTO FoodCategories (name, description) VALUES ('Sushi', 'Fresh rolls, sashimi, and hand-rolled Japanese fish dishes.');");
        stmt.executeUpdate("INSERT INTO FoodCategories (name, description) VALUES ('Tacos', 'Street tacos, burritos, and Mexican appetizers.');");
        stmt.executeUpdate("INSERT INTO FoodCategories (name, description) VALUES ('Salad', 'Fresh organic vegetable bowls and healthy green salads.');");
        stmt.executeUpdate("INSERT INTO FoodCategories (name, description) VALUES ('Pasta', 'Classic Italian noodle dishes, baked ziti, and comfort bowls.');");
        stmt.executeUpdate("INSERT INTO FoodCategories (name, description) VALUES ('BBQ', 'Slow-smoked meats, pulled pork, and ribs.');");
        stmt.executeUpdate("INSERT INTO FoodCategories (name, description) VALUES ('Bakery', 'Freshly baked breads, pastries, and croissants.');");
        stmt.executeUpdate("INSERT INTO FoodCategories (name, description) VALUES ('Steak', 'Prime cuts of beef, ribeyes, and grilled chops.');");
        stmt.executeUpdate("INSERT INTO FoodCategories (name, description) VALUES ('Soup & Ramen', 'Brothy noodle bowls, pho, and warm soups.');");
        // Link Restaurants to Multiple Generic Food Categories (restaurant_id, food_category_id)
        // 1. The Rustic Spatula (American / Farm-to-Table) -> Burgers, Salad, Steak
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (1, 2);"); 
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (1, 5);"); 
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (1, 9);"); 

        // 2. Sakura Sushi & Ramen (Japanese) -> Sushi, Soup & Ramen
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (2, 3);"); 
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (2, 10);"); 

        // 3. El Toro Taqueria (Mexican) -> Tacos, Salad
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (3, 4);"); 
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (3, 5);"); 

        // 4. Slice of Italy Pizzeria (Italian) -> Pizza, Pasta, Salad
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (4, 1);"); 
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (4, 6);"); 
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (4, 5);"); 

        // 5. Green Leaf Bistro (Vegan / Health) -> Salad, Burgers (Veggie burgers)
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (5, 5);"); 
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (5, 2);"); 

        // 6. Hudson Valley Smokehouse (BBQ) -> BBQ, Burgers, Salad
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (6, 7);"); 
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (6, 2);"); 
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (6, 5);"); 

        // 7. The Artisan Baker & Café (Bakery) -> Bakery
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (7, 8);"); 

        // 8. Thai Basil Garden (Thai) -> Soup & Ramen, Salad
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (8, 10);"); 
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (8, 5);"); 

        // 9. Ridgeview Steakhouse & Grill (Steakhouse) -> Steak, Burgers, Salad
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (9, 9);"); 
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (9, 2);"); 
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (9, 5);"); 

        // 10. Mediterranean Olive (Mediterranean) -> Salad, Burgers (Gyros/Wraps grouped loosely)
        stmt.executeUpdate("INSERT INTO RestaurantCategories (restaurant_id, food_category_id) VALUES (10, 5);");
        //
        //
        // create guest account
        stmt.executeUpdate("INSERT INTO Users (username, password, name, email, phone) VALUES ('Guest', '12345', 'Guest', 'guest@example.com', '123-456-7890');");
        System.out.println("Seed data insertion complete.");
    }


}