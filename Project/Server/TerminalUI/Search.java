package Project.Server.TerminalUI;

import Project.Server.Classes.User;
import Project.Server.APIs.RestaurantAPI;
import Project.Server.Classes.Restaurant;
import java.util.List;
import java.util.ArrayList;


public class Search {
     public static void outputSpacing() {
        System.out.println("---------------------------------------------------");
    }

    public static void println(String message) {
        System.out.println(message);
    }

    public static void displaySearchResults(User user, String searchTerm, String filterType, String filterValue) {
        List<Restaurant> restaurants = RestaurantAPI.GetRestaurantsbyFilter(searchTerm, filterType, filterValue);
        int totalResults = restaurants.size();
        println("Searching: Name = "+searchTerm+" | Filter = "+filterType+" | Filter Value = "+filterValue);
        println("Total results found: " + totalResults);
        int i = 1;
        for (Restaurant restaurant : restaurants) {
            println("--");
            println("["+i+"] " + restaurant.getName() + " (" + restaurant.getRating() + " Stars)");
            i++;
            println(restaurant.getDescription());
            println("Food Categories: " + String.join(", ", restaurant.getFoodCategories()));
            println("--");
        }
        boolean running = true;
        while (running) {
            println("Choose an Option:");
            println("0. Back to Search Menu [0]");
            if (totalResults > 0) {
                println("[1 - " + totalResults + "] Select a Restaurant");
            }
            int choice = Integer.parseInt(System.console().readLine());
            if (choice == 0) {
                running = false;
                runSearchOptions(user);
            } else if (choice >= 1 && choice <= totalResults) {
                Restaurant selectedRestaurant = restaurants.get(choice - 1);
                println("Selected Restaurant: " + selectedRestaurant.getName());
                running = false;
            } else {
                println("Invalid choice. Please try again.");
            }
        }
    }
    
    public static void runSearchOptions(User user) {
        // Implement search options here
        boolean running = true;
        while (running) {
            outputSpacing();
            println("Reservation.com Search Menu");
            println("0. Back to Profile [0]");
            println("1. List all Restaurants [1]");
            println("2. Search by Restaurant Name & Filter [2]");
            int choice = Integer.parseInt(System.console().readLine());
            switch (choice) {
                case 0:
                    running = false;
                    Profile.main(user);
                    break;
                case 1:
                    // List all restaurants
                    running = false;
                    displaySearchResults(user, "", "None", "");
                    break;
                case 2:
                    // Search by restaurant name
                    running = false;
                    println("Enter restaurant name to search:");
                    String searchTerm = System.console().readLine();

                    int i = 1;
                    ArrayList<String> filters = new ArrayList<>(RestaurantAPI.GetAllFilters());
                    for (String filter : filters) {
                        println(i + ". " + filter);
                        i++;
                    }
                    println("Choose a Filter [1 - " + filters.size() + "]");
                    String filterType = filters.get(Integer.parseInt(System.console().readLine()) - 1);
                    String filterValue = "";
                    switch (filterType) {
                        case "Above Rating":
                            println("Enter number for Above Rating:");
                            filterValue = "-1";
                            while (filterValue.equals("-1")) {
                                println("Enter number between 0 - 5 for Above Rating:");
                                int rating = -1;
                                try {
                                    rating = Integer.parseInt(System.console().readLine());
                                } catch (NumberFormatException e) {
                                    rating = -1;
                                }
                                if (rating >= 0 && rating <= 5) {
                                    filterValue = String.valueOf(rating);
                                }
                            }
                            displaySearchResults(user, searchTerm, filterType, filterValue);
                            break;
                        case "Below Rating":
                            println("Enter number for Below Rating:");
                            filterValue = "-1";
                            while (filterValue.equals("-1")) {
                                println("Enter number between 0 - 5 for Below Rating:");
                                int rating = -1;
                                try {
                                    rating = Integer.parseInt(System.console().readLine());
                                } catch (NumberFormatException e) {
                                    rating = -1;
                                }
                                if (rating >= 0 && rating <= 5) {
                                    filterValue = String.valueOf(rating);
                                }
                            }
                            displaySearchResults(user, searchTerm, filterType, filterValue);
                            break;
                        case "Food Category":
                            println("Enter name for Food Category:");
                            filterValue = "-1";
                            while (filterValue.equals("-1")) {
                                println("Enter a valid food category:");
                                String category = System.console().readLine();
                                if (category != null && !category.trim().isEmpty()) {
                                    filterValue = category.trim();
                                }
                            }
                            displaySearchResults(user, searchTerm, filterType, filterValue);
                            break;
                        default:
                            displaySearchResults(user, searchTerm, filterType, filterValue);
                            break;
                    }
                    break;
                default:
                    outputSpacing();
                    println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public static void main(User user) {
        runSearchOptions(user);
    }
}
