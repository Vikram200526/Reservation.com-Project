package Project.Server.APIs;

import java.util.List;

import Project.Server.Classes.Restaurant;

public class RestaurantAPI {
    // RestaurantAPI would provide methods that other java files could call
    // example "CoolMethod()"
    // CoolMethod would take some user input, and manipulate it into some information
    // then it would send that information to DatabaseAPI to run some sql query
    // once the sql query returns some data, CoolMethod could do some final manipulation of the results
    // finally returns the finished result to the java file that requested it
   public static List<Restaurant> listAllRestaurants() {
        return DatabaseAPI.listAllRestaurants();
   }
}
