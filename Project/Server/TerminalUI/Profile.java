package Project.Server.TerminalUI;

import java.util.List;
import java.util.Scanner;

import Project.Server.APIs.ReservationAPI;
import Project.Server.Classes.DetailedReservation;
import Project.Server.Classes.User;

public class Profile {

     public static void outputSpacing() {
        System.out.println("---------------------------------------------------");
    }

    public static void println(String message) {
        System.out.println(message);
    }

    public static void runProfileOptions(User user) {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        boolean validChoice = false;
        while (validChoice == false) {
            outputSpacing();
            println("Welcome " + user.getName() + "! Please choose an option to continue:");
            println("0. Log Out [0]");
            println("1. View Account Details [1]");
            println("2. Search Restaurants [2]");
            println("3. View Active Reservations [3]");

            choice = scanner.nextInt();
            switch (choice) {
                case 0:
                    validChoice = true;
                    println("Logging out...");
                    Project.loginMain();
                    break;
                case 1:
                    outputSpacing();
                    println("Account Details:");
                    System.out.println("Username: " + user.getUsername());
                    System.out.println("Password: " + user.getPassword());
                    System.out.println("Name:     " + user.getName());
                    System.out.println("Email:    " + user.getEmail());
                    System.out.println("Phone:    " + user.getPhone());
                    println("Type anything to continue...");
                    scanner.next();
                    break;
                case 2:
                    validChoice = true;
                    Search.main(user);
                    break;
                case 3:
                    outputSpacing();
                    println("Active Reservations:");
                    List<DetailedReservation> reservations = ReservationAPI.getDetailedConfirmedReservationsForUser(user.getUserId());
                    if (reservations.isEmpty()) {
                        println("No active reservations found.");
                    } else {
                        int index = 1;
                        for (DetailedReservation reservation : reservations) {
                            println("--");
                            println(index + ". Reservation at " + reservation.getRestaurantName() + " as Table for " + reservation.getTableSize() + " [" + index + "]:");
                            println(reservation.getDate() + " at " + ReservationAPI.convertTo12HourFormat(reservation.getTime()));
                            index++;
                        }
                        println("--");
                        println("Choose [0] to return, or 1 - " + (index - 1) + " to cancel a reservation:");
                        boolean validCancelChoice = false;
                        while (!validCancelChoice) {
                            int cancelChoice = scanner.nextInt();
                            if (cancelChoice == 0) {
                                validCancelChoice = true;
                            } else if (cancelChoice >= 1 && cancelChoice <= index - 1) {
                                DetailedReservation selectedReservation = reservations.get(cancelChoice - 1);
                                outputSpacing();
                                println("CONFIRM CANCELLATION [y/n]?");
                                println("Reservation at " + selectedReservation.getRestaurantName() + " as Table for " + selectedReservation.getTableSize());
                                println(selectedReservation.getDate() + " at " + ReservationAPI.convertTo12HourFormat(selectedReservation.getTime()));
                                String confirmationChoice = scanner.next();
                                if (confirmationChoice.equalsIgnoreCase("y")) {
                                    String cancelResult = ReservationAPI.cancelReservation(selectedReservation.getReservationId());
                                    println(cancelResult);
                                }
                                validCancelChoice = true;
                            } else {
                                println("Invalid choice. Please try again.");
                            }
                        }
                    }
                    break;
                default:
                    println("Invalid Choice");
                    break;
            }
        }
    }
    
    public static void main(User user) {
        runProfileOptions(user);
    }
}
