package Project.Server;

import java.util.List;

import Project.Server.Classes.Restaurant;
import Project.Server.APIs.RestaurantAPI;

public class Project {
    // A temporary backend test, imagine this as the actual application running for the client
    public static void main(String[] args) {
        System.out.println("Initializing database...");
        DatabaseInitializer.initializeDatabase();

        System.out.println("\nTesting listAllRestaurants() API:");
        List<Restaurant> restaurants = RestaurantAPI.listAllRestaurants();
        
        for (Restaurant r : restaurants) {
            System.out.println("- " + r.getName() + " (" + r.getRating() + " stars): " + r.getAddress());
        }
    }
}
