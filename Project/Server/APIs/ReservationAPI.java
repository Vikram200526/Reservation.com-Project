package Project.Server.APIs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Project.Server.Classes.DetailedReservation;

public class ReservationAPI {
    private static final String URL = "jdbc:sqlite:reservation_com.db";

    public static String reserveTimeSlot(int timeSlotId, int userId) {
        // 1. Validate User Exists
        String checkUserQuery = "SELECT user_id FROM Users WHERE user_id = ?;";
        // 2. Validate TimeSlot Exists, get restaurant_id and current table_count
        String checkSlotQuery = "SELECT restaurant_id, table_count FROM TimeSlots WHERE time_slot_id = ?;";
        // 3. Update table count
        String updateSlotQuery = "UPDATE TimeSlots SET table_count = table_count - 1 WHERE time_slot_id = ?;";
        // 4. Insert Reservation
        String insertReservationQuery = "INSERT INTO Reservations (user_id, restaurant_id, time_slot_id, status) VALUES (?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(URL)) {
            // Disable auto-commit to handle transactions safely
            conn.setAutoCommit(false);

            // Step 1: Check if User is valid
            try (PreparedStatement pstmt = conn.prepareStatement(checkUserQuery)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        return "Error: Invalid user ID. User does not exist.";
                    }
                }
            }

            // Step 2: Check if TimeSlot is valid and fetch restaurant_id & table_count
            int restaurantId = -1;
            int tableCount = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(checkSlotQuery)) {
                pstmt.setInt(1, timeSlotId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        restaurantId = rs.getInt("restaurant_id");
                        tableCount = rs.getInt("table_count");
                    } else {
                        return "Error: Invalid time slot ID. Time slot does not exist.";
                    }
                }
            }

            // Step 3: Check if at least 1 table is available
            if (tableCount <= 0) {
                return "No tables left: This time slot is fully booked.";
            }

            // Step 4: Decrement the table count in TimeSlots
            try (PreparedStatement pstmt = conn.prepareStatement(updateSlotQuery)) {
                pstmt.setInt(1, timeSlotId);
                pstmt.executeUpdate();
            }

            // Step 5: Create the reservation record
            try (PreparedStatement pstmt = conn.prepareStatement(insertReservationQuery)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, restaurantId);
                pstmt.setInt(3, timeSlotId);
                pstmt.setString(4, "Confirmed"); // Initial status
                pstmt.executeUpdate();
            }

            // Commit transaction if all steps succeed
            conn.commit();
            return "Success: Reservation successfully created and confirmed!";

        } catch (SQLException e) {
            System.out.println("SQL Error during reservation: " + e.getMessage());
            return "SQL error: " + e.getMessage();
        }
    }

    public static String convertTo12HourFormat(String time24) {
        try {
            // Parse the 24-hour time string (e.g., "13:00")
            LocalTime time = LocalTime.parse(time24, DateTimeFormatter.ofPattern("HH:mm"));
            
            // Format it to 12-hour format with AM/PM (e.g., "1:00 PM")
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
            return time.format(formatter);
            
        } catch (Exception e) {
            System.out.println("Invalid time format: " + time24);
            return time24; // Returns the original string as a fallback if parsing fails
        }
    }

    public static List<DetailedReservation> getDetailedConfirmedReservationsForUser(int userId) {
        List<DetailedReservation> reservations = new ArrayList<>();
        
        // SQL Query joining Reservations, TimeSlots, and Restaurants
        String query = "SELECT r.reservation_id, r.user_id, r.restaurant_id, rest.name AS restaurant_name, " +
                    "r.time_slot_id, t.date, t.time, t.table_size, r.status " +
                    "FROM Reservations r " +
                    "JOIN TimeSlots t ON r.time_slot_id = t.time_slot_id " +
                    "JOIN Restaurants rest ON r.restaurant_id = rest.restaurant_id " +
                    "WHERE r.user_id = ? AND r.status = 'Confirmed';";

        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int reservationId = rs.getInt("reservation_id");
                    int restId = rs.getInt("restaurant_id");
                    String restaurantName = rs.getString("restaurant_name");
                    int timeSlotId = rs.getInt("time_slot_id");
                    String date = rs.getString("date");
                    String time = rs.getString("time");
                    int tableSize = rs.getInt("table_size");
                    String status = rs.getString("status");

                    DetailedReservation detail = new DetailedReservation(
                        reservationId, userId, restId, restaurantName, timeSlotId, date, time, tableSize, status
                    );
                    reservations.add(detail);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching detailed confirmed reservations: " + e.getMessage());
        }

        return reservations;
    }

    public static String cancelReservation(int reservationId) {
        // 1. Fetch the time_slot_id and current status for the given reservation
        String selectQuery = "SELECT time_slot_id, status FROM Reservations WHERE reservation_id = ?;";
        // 2. Update reservation status to Cancelled
        String updateReservationQuery = "UPDATE Reservations SET status = 'Cancelled' WHERE reservation_id = ?;";
        // 3. Increment the table count back in TimeSlots
        String updateTimeSlotQuery = "UPDATE TimeSlots SET table_count = table_count + 1 WHERE time_slot_id = ?;";

        try (Connection conn = DriverManager.getConnection(URL)) {
            // Disable auto-commit for transaction safety
            conn.setAutoCommit(false);

            int timeSlotId = -1;
            String currentStatus = "";

            // Step 1: Validate reservation existence and get its details
            try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                pstmt.setInt(1, reservationId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        timeSlotId = rs.getInt("time_slot_id");
                        currentStatus = rs.getString("status");
                    } else {
                        return "Error: Invalid reservation ID. Reservation does not exist.";
                    }
                }
            }

            // Step 2: Prevent double-cancelling and incorrect table incrementing
            if ("Cancelled".equalsIgnoreCase(currentStatus)) {
                return "Error: This reservation has already been cancelled.";
            }

            // Step 3: Update the reservation status
            try (PreparedStatement pstmt = conn.prepareStatement(updateReservationQuery)) {
                pstmt.setInt(1, reservationId);
                pstmt.executeUpdate();
            }

            // Step 4: Increment the table count in TimeSlots by 1
            try (PreparedStatement pstmt = conn.prepareStatement(updateTimeSlotQuery)) {
                pstmt.setInt(1, timeSlotId);
                pstmt.executeUpdate();
            }

            // Commit transaction if all steps succeed
            conn.commit();
            return "Success: Your Reservation has been cancelled!";

        } catch (SQLException e) {
            System.out.println("SQL Error during cancellation: " + e.getMessage());
            return "SQL error: " + e.getMessage();
        }
    }
}