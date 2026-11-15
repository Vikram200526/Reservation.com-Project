package Project.Server.TerminalUI;

import Project.Server.Classes.User;
import Project.Server.APIs.RestaurantAPI;
import Project.Server.APIs.ReservationAPI;
import Project.Server.Classes.Restaurant;
import Project.Server.Classes.TimeSlot;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map;


public class Reserve {
     public static void outputSpacing() {
        System.out.println("---------------------------------------------------");
    }

    public static void println(String message) {
        System.out.println(message);
    }


    public static void runReserveOptions(User user, Restaurant restaurant) {
        boolean running = true;
        while (running) {
            outputSpacing();
            println(restaurant.getName()+" ("+restaurant.getRating()+")");
            println("Address: "+restaurant.getAddress());
            println("Phone: "+restaurant.getPhone());
            println("--");
            println(restaurant.getDescription());
            println("Food Categories: " + String.join(", ", restaurant.getFoodCategories()));
            println("--");

            println("0. Return to Restaurant Search [0]");
            println("1. Make a reservation [1]");
            println("Enter 0 or 1:");
            
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            if (choice == 0) {
                running = false;
                Search.main(user);
            } else if (choice == 1) {
                //
                Map<Integer, TimeSlot> timeSlots = RestaurantAPI.getTimeSlotsForRestaurant(restaurant.getRestaurantId());
                ArrayList<String> availableHours = new ArrayList<>(); // get available hours
                for (TimeSlot timeSlot : timeSlots.values()) {
                    if (!availableHours.contains(timeSlot.getTime())) {
                        // check if hour has avaiable tables
                        if (timeSlot.getTableCount() > 0) {
                            availableHours.add(timeSlot.getTime());
                        }
                    }
                }
                
                int index = 1;

                if (availableHours.isEmpty()) {
                    outputSpacing();
                    println("Sorry! No available hours since fully booked, check back later!");
                    running = false;
                    runReserveOptions(user, restaurant);
                } else {
                    println("Available Reservation Hours:");
                    for (String hour : availableHours) {
                        println("[" + index + "] " + ReservationAPI.convertTo12HourFormat(hour));
                        index++;
                    }
                    println("Enter 1 - " + (availableHours.size()) + " to choose your reservation hour, or [0] to return:");
                    int hourChoice = -1;
                    boolean validChoice = false;
                    while (!validChoice) {
                        hourChoice = scanner.nextInt();
                        if (hourChoice >= 1 && hourChoice <= availableHours.size()) {
                            validChoice = true;
                            //
                            index = 1;
                            println("----");
                            println("Available Tables for " + ReservationAPI.convertTo12HourFormat(availableHours.get(hourChoice - 1)) + ":");
                            ArrayList<TimeSlot> filteredTimeSlots = new ArrayList<>();
                            for (TimeSlot timeSlot : timeSlots.values()) {
                                if (timeSlot.getTime().equals(availableHours.get(hourChoice - 1)) && timeSlot.getTableCount() > 0) {
                                    filteredTimeSlots.add(timeSlot);
                                    println("[" + index + "] Table Size: " + timeSlot.getTableSize() + " | Tables Left: " + timeSlot.getTableCount());
                                    index++;
                                }
                            }
                            println("Enter 1 - " + (index - 1) + " to choose your table, or [0] to return:");
                            int tableChoice = -1;
                            boolean validTableChoice = false;
                            while (!validTableChoice) {
                                tableChoice = scanner.nextInt();
                                if (tableChoice >= 1 && tableChoice <= filteredTimeSlots.size()) {
                                    validTableChoice = true;
                                    //
                                    TimeSlot selectedTable = filteredTimeSlots.get(tableChoice - 1);
                                    outputSpacing();
                                    println("CONFIRM RESERVATION:");
                                    println(restaurant.getName());
                                    println(selectedTable.getDate() + " at " + ReservationAPI.convertTo12HourFormat(selectedTable.getTime()));
                                    println("Table for " + selectedTable.getTableSize() + " people");
                                    println("--");
                                    println("Are you sure you want to reserve this table? (y/n):");
                                    String confirmation = scanner.next();
                                    validTableChoice = true;
                                    if (confirmation.equalsIgnoreCase("y")) {
                                        println(ReservationAPI.reserveTimeSlot(selectedTable.getTimeSlotId(), user.getUserId()));
                                    } else {
                                        println("Reservation canceled.");
                                    }
                                    Profile.main(user);
                                } else {
                                    if (tableChoice == 0) {
                                        validTableChoice = true;
                                        runReserveOptions(user, restaurant);
                                    } else {
                                        println("Invalid choice. Please enter a number between 1 and " + filteredTimeSlots.size() + ", or 0 to return.");
                                    }
                                }
                            }
                            
                        } else {
                            if (hourChoice == 0) {
                                validChoice = true;
                                runReserveOptions(user, restaurant);
                            } else {
                                println("Invalid choice. Please enter a number between 1 and " + availableHours.size() + ", or 0 to return.");
                            }
                        }
                    }
                    //
                }
            } else {
                println("Invalid choice. Please enter 0 or 1.");
            }
        }
    }


    public static void main(User user,Restaurant restaurant) {
        runReserveOptions(user, restaurant);
    }
}
