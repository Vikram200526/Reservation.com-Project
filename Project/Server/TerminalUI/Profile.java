package Project.Server.TerminalUI;

import java.util.Scanner;

import Project.Server.Classes.User;

public class Profile {

     public static void outputSpacing() {
        System.out.println("---------------------------------------------------");
    }

    public static void println(String message) {
        System.out.println(message);
    }

    public static void runProfileOptions(User user) {
        outputSpacing();
        println("Welcome " + user.getName() + "! Please choose an option to continue:");
        println("0. Log Out [0]");
        println("1. View Account Details [1]");
        println("2. Search Restaurants [2]");
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        boolean validChoice = false;
        while (validChoice == false) {
            choice = scanner.nextInt();
            switch (choice) {
                case 0:
                    validChoice = true;
                    println("Logging out...");
                    Project.main();
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
                    outputSpacing();
                    println("Welcome " + user.getName() + "! Please choose an option to continue:");
                    println("0. Log Out [0]");
                    println("1. View Account Details [1]");
                    println("2. Search Restaurants [2]");
                    break;
                case 2:
                    validChoice = true;
                    Search.main(user);
                    break;
                default:
                    outputSpacing();
                    println("Invalid choice. Please choose an option:");
                    println("0. Log Out [0]");
                    println("1. View Account Details [1]");
                    println("2. Search Restaurants [2]");
                    break;
            }
        }
    }
    
    public static void main(User user) {
        runProfileOptions(user);
    }
}
