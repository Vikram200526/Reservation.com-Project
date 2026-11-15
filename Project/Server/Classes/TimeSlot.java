
package Project.Server.Classes;

public class TimeSlot {
    private int timeSlotId;
    private int restaurantId;
    private String date;
    private String time;
    private int tableSize;
    private int tableCount;

    public TimeSlot(int timeSlotId, int restaurantId, String date, String time, int tableSize, int tableCount) {
        this.timeSlotId = timeSlotId;
        this.restaurantId = restaurantId;
        this.date = date;
        this.time = time;
        this.tableSize = tableSize;
        this.tableCount = tableCount;
    }

    // Getters
    public int getTimeSlotId() { return timeSlotId; }
    public int getRestaurantId() { return restaurantId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getTableSize() { return tableSize; }
    public int getTableCount() { return tableCount; }
}