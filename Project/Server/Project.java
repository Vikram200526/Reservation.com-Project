package Project.Server;

import java.util.List;
import java.util.Scanner;

import Project.Server.Classes.Restaurant;
import Project.Server.APIs.RestaurantAPI;

public class Project {
    // A temporary backend test, imagine this as the actual application running for the client
    int clientUserId;
    public static void testRestaurantAPI() {
        System.out.println("Testing listAllRestaurants() API:");
        List<Restaurant> restaurants = RestaurantAPI.listAllRestaurants();
        
        for (Restaurant r : restaurants) {
            System.out.println("- " + r.getName() + " (" + r.getRating() + " stars): " + r.getAddress());
        }
    }
    public static void outputSpacing() {
        System.out.println("---------------------------------------------------");
    }
    public static void loginMain() {
        outputSpacing();
        System.out.println("Welcome to the Restaurant Management System!");
        System.out.println("Choose an option to log in:");
        System.out.println("1. Log in with existing account [1]");
        System.out.println("2. Create new account [2]");
        System.out.println("3. Exit [3]");

        int choice = -1;
        Scanner scanner = new Scanner(System.in);
        while (choice != 1 && choice != 2 && choice != 3) {
            System.out.println("1. Log in with existing account [1]");
            System.out.println("2. Create new account [2]");
            System.out.println("3. Exit [3]");
            choice = scanner.nextInt();
            if (choice != 1 && choice != 2 && choice != 3) {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
        if (choice == 1) {
            boolean successfulLogin = false;
            boolean canceledLogin = false;
            outputSpacing();
            System.out.println("You chose to log in with an existing account.");
            while (!successfulLogin && !canceledLogin) {
                System.out.println("Enter your username:");
                String username = scanner.next();
                System.out.println("Enter your password:");
                String password = scanner.next();
               
                

                if (username.equals("user") && password.equals("pass")) {
                    successfulLogin = true;
                    System.out.println("Login successful! You can now access the system.");
                } else {
                    System.out.println("Invalid username or password. Please try again.");
                }
            }
            System.out.println("Enter your username:");
            
        } else if (choice == 2) {
            System.out.println("You chose to create a new account.");
            // Here you would implement the logic to create a new account
        } else if (choice == 3) {
            System.out.println("Exiting the application. Goodbye!");
            System.exit(0);
        }
        // Here you would implement the login logic, for now, we will just simulate a successful login
        System.out.println("Login successful! You can now access the system.");
        testRestaurantAPI();
    }
    public static void main(String[] args) {
        System.out.println("Initializing database...");
        DatabaseInitializer.initializeDatabase();

        //loginMain();
        testRestaurantAPI();
    }
}
