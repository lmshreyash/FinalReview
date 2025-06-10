package model;

import java.util.regex.Pattern;

public class User {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z ]{2,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    
    private String name;
    private int age;
    private String gender;
    private String email;
    private String phone;

    public User(String name, int age, String gender, String email, String phone) 
        throws IllegalArgumentException {
        
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Invalid name! Must be 2-50 alphabetic characters");
        }
        if (!isValidAge(age)) {
            throw new IllegalArgumentException("Invalid age! Must be 1-120");
        }
        if (!isValidGender(gender)) {
            throw new IllegalArgumentException("Invalid gender! Must be M, F, or O");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException("Invalid phone! Must be 10 digits");
        }
        
        this.name = name.trim();
        this.age = age;
        this.gender = gender.toUpperCase();
        this.email = email.toLowerCase().trim();
        this.phone = phone.trim();
    }

    // Validation methods
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    public static boolean isValidAge(int age) {
        return age > 0 && age <= 120;
    }
    
    public static boolean isValidGender(String gender) {
        return gender != null && gender.toUpperCase().matches("[MFO]");
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    // Getters
    public String getEmail() { return email; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    
    // Additional helper methods
    public String getFormattedDetails() {
        return String.format("""
            User Details:
            Name: %s
            Age: %d
            Gender: %s
            Email: %s
            Phone: %s
            """, name, age, gender, email, phone);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, email);
    }

    public String toCSV() {
        return String.join(",", name, String.valueOf(age), gender, email, phone);
    }
}