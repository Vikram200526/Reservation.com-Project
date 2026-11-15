package Project.Server.Classes;

public class DetailedReservation {
    private int reservationId;
    private int userId;
    private int restaurantId;
    private String restaurantName;
    private int timeSlotId;
    private String date;
    private String time;
    private int tableSize;
    private String status;

    public DetailedReservation(int reservationId, int userId, int restaurantId, String restaurantName, int timeSlotId, String date, String time, int tableSize, String status) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.timeSlotId = timeSlotId;
        this.date = date;
        this.time = time;
        this.tableSize = tableSize;
        this.status = status;
    }

    // Getters
    public int getReservationId() { return reservationId; }
    public int getUserId() { return userId; }
    public int getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public int getTimeSlotId() { return timeSlotId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getTableSize() { return tableSize; }
    public String getStatus() { return status; }
}