package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class Train {
    private static final Pattern ID_PATTERN = Pattern.compile("^TRAIN\\d{3}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private String trainId;
    private String name;
    private String source;
    private String destination;
    private String date;
    private String time;
    private int seats;
    private double fare;
    private String departureTime;

    public Train(String trainId, String name, String source, String destination, 
                String date, String time, int seats, double fare) throws IllegalArgumentException {
        
        if (!isValidTrainId(trainId)) {
            throw new IllegalArgumentException("Invalid Train ID! Must be in format TRAIN001");
        }
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Invalid train name! Must be 2-100 characters");
        }
        if (!isValidStation(source) || !isValidStation(destination)) {
            throw new IllegalArgumentException("Invalid station name! Must be 2-50 characters");
        }
        if (!isValidDate(date)) {
            throw new IllegalArgumentException("Invalid date! Must be YYYY-MM-DD and not in past");
        }
        if (!isValidTime(time)) {
            throw new IllegalArgumentException("Invalid time! Must be HH:MM in 24-hour format");
        }
        if (!isValidSeats(seats)) {
            throw new IllegalArgumentException("Invalid seats! Must be 1-1000");
        }
        if (!isValidFare(fare)) {
            throw new IllegalArgumentException("Invalid fare! Must be 1-100000");
        }
        
        this.trainId = trainId.toUpperCase();
        this.name = name.trim();
        this.source = source.trim();
        this.destination = destination.trim();
        this.date = date;
        this.time = time;
        this.seats = seats;
        this.fare = fare;
    }

    // Validation methods
    public static boolean isValidTrainId(String trainId) {
        return trainId != null && ID_PATTERN.matcher(trainId.toUpperCase()).matches();
    }
    
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && name.trim().length() <= 100;
    }
    
    public static boolean isValidStation(String station) {
        return station != null && station.trim().length() >= 2 && station.trim().length() <= 50;
    }
    
    public static boolean isValidDate(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date, DATE_FORMATTER);
            return !parsedDate.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    public static boolean isValidTime(String time) {
        return time != null && TIME_PATTERN.matcher(time.trim()).matches();
    }
    
    public static boolean isValidSeats(int seats) {
        return seats > 0 && seats <= 1000;
    }
    
    public static boolean isValidFare(double fare) {
        return fare > 0 && fare <= 100000;
    }

    // Getters
    public String getTrainId() { return trainId; }
    public String getName() { return name; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getSeats() { return seats; }
    public double getFare() { return fare; }

    // Setters with validation
    public void setSeats(int seats) {
        if (!isValidSeats(seats)) throw new IllegalArgumentException("Invalid seats value");
        this.seats = seats;
    }
    
    public void setFare(double fare) {
        if (!isValidFare(fare)) throw new IllegalArgumentException("Invalid fare value");
        this.fare = fare;
    }public void setTrainName(String name) {
    this.name = name;
}

public void setSource(String source) {
    this.source = source;
}

public void setDestination(String destination) {
    this.destination = destination;
}

public void setDate(String date) {
    this.date = date;
}

public void setTime(String departureTime) {
    this.departureTime = departureTime;
}

    // Helper methods
    public String getRoute() {
        return source + " → " + destination;
    }
    
    public String getFormattedDetails() {
        return String.format("""
            Train Details:
            ID: %s
            Name: %s
            Route: %s to %s
            Date: %s
            Time: %s
            Available Seats: %d
            Fare: ₹%.2f
            """, trainId, name, source, destination, date, time, seats, fare);
    }

    @Override
    public String toString() {
        return String.join(",", trainId, name, source, destination, date, time, 
                          String.valueOf(seats), String.valueOf(fare));
    }

    public void copyFrom(Train other) {
        if (other == null) return;
        this.name = other.name;
        this.source = other.source;
        this.destination = other.destination;
        this.date = other.date;
        this.time = other.time;
        this.seats = other.seats;
        this.fare = other.fare;
    }
}