package Project.Server.Classes;

import java.util.ArrayList;

public class Restaurant {
    private int restaurantId;
    private String name;
    private String description;
    private String phone;
    private double rating;
    private String address;
    private ArrayList<String> foodCategories;

    // Constructor
    public Restaurant(int restaurantId, String name, String description, String phone, double rating, String address, ArrayList<String> foodCategories) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.phone = phone;
        this.rating = rating;
        this.address = address;
        this.foodCategories = foodCategories;
    }

    // Getters
    public int getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPhone() {
        return phone;
    }

    public double getRating() {
        return rating;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<String> getFoodCategories() {
        return foodCategories;
    }

    // Setters (useful if you ever need to update restaurant details)
    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + restaurantId +
                ", name='" + name + '\'' +
                ", rating=" + rating +
                ", address='" + address + '\'' +
                '}';
    }
}