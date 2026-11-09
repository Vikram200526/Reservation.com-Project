package Project.Server;

import java.util.List;
import java.util.Scanner;

import Project.Server.Classes.Restaurant;
import Project.Server.APIs.RestaurantAPI;
import Project.Server.APIs.UserAPI;
import Project.Server.Classes.User;


public class Project {
    // A temporary backend test, imagine this as the actual application running for the client
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

        int choice = -1;
        Scanner scanner = new Scanner(System.in);
        while (choice != 1 && choice != 2 && choice != 3) {
            System.out.println("1. Log in with existing account [1]");
            System.out.println("2. Create new account [2]");
            System.out.println("3. Exit [3]");
            choice = scanner.nextInt();
            if (choice != 1 && choice != 2 && choice != 3) {
                outputSpacing();
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
               
                User result = UserAPI.getUserAccount(username,password);

                if (result != null) {
                    successfulLogin = true;
                    System.out.println("Login successful! You can now access the system.");

                } else {
                    outputSpacing();
                    System.out.println("Invalid username or password.");
                    System.out.println("Type [1] to try again.");
                    System.out.println("Type [2] to cancel.");
                    int retryChoice = scanner.nextInt();
                    if (retryChoice == 2) {
                        canceledLogin = true;
                    }
                    outputSpacing();
                }
            }
            
            if (successfulLogin) {
                System.out.println("You have successfully logged in.");
                testRestaurantAPI();
            } else if (canceledLogin) {
                System.out.println("Login canceled.");
                loginMain();
            }
            
        } else if (choice == 2) {
            outputSpacing();
            System.out.println("You chose to create a new account.");
            
            boolean accountFlowActive = true;
            while (accountFlowActive) {

                // Step 1: Collect user details
                System.out.println("Enter username: ");
                String username = scanner.next();

                System.out.println("Enter password: ");
                String password = scanner.next();

                System.out.println("Enter full name: ");
                String name = scanner.next();

                System.out.println("Enter email: ");
                String email = scanner.next();

                System.out.println("Enter phone number: ");
                String phone = scanner.next();

                // Step 2: Show confirmation screen
                System.out.println("==============================");
                System.out.println("       CONFIRM DETAILS        ");
                System.out.println("==============================");
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                System.out.println("Name:     " + name);
                System.out.println("Email:    " + email);
                System.out.println("Phone:    " + phone);
                System.out.println("==============================");
                System.out.println("Are these details correct?");
                System.out.println("1. Yes [1]");
                System.out.println("2. No [2]");
                
                int confirm = scanner.nextInt();

                if (confirm == 1) {
                    if (!UserAPI.checkUsernameAvailable(username)) {
                        System.out.println("Error: Username is already taken.");
                        System.out.println("Do you want to [1] Restart account creation or [2] Cancel? Enter 1 or 2: ");
                        String decision = scanner.next();

                        if (!decision.equals("1")) {
                            System.out.println("Account creation cancelled returning to main menu.");
                            accountFlowActive = false; // Exit back to main menu
                        } else {
                            System.out.println("Restarting account creation...");
                            // Loop continues and prompts for details again
                        }
                    } else {
                        outputSpacing();
                        System.out.println("Creating your account...");
                        if (UserAPI.createUserAccount(username, password, name, email, phone)) {
                            System.out.println("Account created successfully!");
                            accountFlowActive = false; // Exit loop on success
                        } else {
                            System.out.println("Account creation failed");
                            System.out.println("Do you want to [1] Restart account creation or [2] Cancel? Enter 1 or 2: ");
                            String decision = scanner.next();

                            if (!decision.equals("1")) {
                                System.out.println("Account creation cancelled returning to main menu.");
                                accountFlowActive = false; // Exit back to main menu
                            } else {
                                System.out.println("Restarting account creation...");
                                // Loop continues and prompts for details again
                            }
                        }
                    }
                    
                } else {
                    // Step 4: NO branch - Ask to restart or cancel
                    System.out.println("Do you want to [1] Restart account creation or [2] Cancel? Enter 1 or 2: ");
                    String decision = scanner.next();

                    if (!decision.equals("1")) {
                        System.out.println("Account creation cancelled returning to main menu.");
                        accountFlowActive = false; // Exit back to main menu
                    } else {
                        System.out.println("Restarting account creation...");
                        // Loop continues and prompts for details again
                    }
                }
            }
            loginMain();
        } else if (choice == 3) {
            System.out.println("Exiting the application. Goodbye!");
            System.exit(0);
        }
    }
    public static void main(String[] args) {
        System.out.println("Initializing database...");
        DatabaseInitializer.initializeDatabase();

        loginMain();
        //testRestaurantAPI();
    }
}
