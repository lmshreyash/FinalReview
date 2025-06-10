package model;

import java.util.regex.Pattern;

public class Ticket {
    private static final Pattern PNR_PATTERN = Pattern.compile("^PNR\\d{5}$");
    private static final Pattern CLASS_PATTERN = Pattern.compile("^(General|Sleeper|AC)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z ]{2,50}$");
    
    private String pnr;
    private String trainId;
    private String userEmail;
    private String passengerName;
    private int passengerAge;
    private String travelClass;

    public Ticket(String pnr, String trainId, String userEmail, 
                 String passengerName, int passengerAge, String travelClass) 
        throws IllegalArgumentException {
        
        if (!isValidPNR(pnr)) {
            throw new IllegalArgumentException("Invalid PNR! Must be in format PNR12345");
        }
        if (!Train.isValidTrainId(trainId)) {
            throw new IllegalArgumentException("Invalid Train ID");
        }
        if (!User.isValidEmail(userEmail)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!isValidPassengerName(passengerName)) {
            throw new IllegalArgumentException("Invalid passenger name");
        }
        if (!User.isValidAge(passengerAge)) {
            throw new IllegalArgumentException("Invalid passenger age");
        }
        if (!isValidTravelClass(travelClass)) {
            throw new IllegalArgumentException("Invalid travel class");
        }
        
        this.pnr = pnr.toUpperCase();
        this.trainId = trainId.toUpperCase();
        this.userEmail = userEmail.toLowerCase().trim();
        this.passengerName = passengerName.trim();
        this.passengerAge = passengerAge;
        this.travelClass = travelClass.trim();
    }

    // Validation methods
    public static boolean isValidPNR(String pnr) {
        return pnr != null && PNR_PATTERN.matcher(pnr.toUpperCase()).matches();
    }
    
    public static boolean isValidPassengerName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    public static boolean isValidTravelClass(String travelClass) {
        return travelClass != null && CLASS_PATTERN.matcher(travelClass.trim()).matches();
    }

    // Getters
    public String getPnr() { return pnr; }
    public String getTrainId() { return trainId; }
    public String getUserEmail() { return userEmail; }
    public String getPassengerName() { return passengerName; }
    public int getPassengerAge() { return passengerAge; }
    public String getTravelClass() { return travelClass; }

    // Setters with validation
    public void setTravelClass(String travelClass) {
        if (!isValidTravelClass(travelClass)) {
            throw new IllegalArgumentException("Invalid travel class");
        }
        this.travelClass = travelClass.trim();
    }

    // Helper methods
    public String getFormattedDetails() {
        return String.format("""
            Ticket Details:
            PNR: %s
            Train ID: %s
            Passenger: %s (Age: %d)
            Class: %s
            Booked by: %s
            """, pnr, trainId, passengerName, passengerAge, travelClass, userEmail);
    }

    public String toCSV() {
        return String.join(",", pnr, trainId, userEmail, 
                         passengerName, String.valueOf(passengerAge), travelClass);
    }

    @Override
    public String toString() {
        return String.format("PNR: %s | Train: %s | Passenger: %s | Class: %s", 
                            pnr, trainId, passengerName, travelClass);
    }
}