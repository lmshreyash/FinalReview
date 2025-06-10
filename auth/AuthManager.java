package auth;

import model.User;
import util.FileHelper;
import java.util.*;
import java.util.regex.Pattern;

public class AuthManager {
    private final Scanner scanner = new Scanner(System.in);
    private final String userFile = "data/users.txt";
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10}$");

    public void register() {
        System.out.println("\n--- User Registration ---");
        
        try {
            // Name validation
            String name;
            while(true) {
                System.out.print("Name: ");
                name = scanner.nextLine().trim();
                if(name.isEmpty()) {
                    System.out.println("Name cannot be empty!");
                    continue;
                }
                if(!name.matches("[a-zA-Z ]+")) {
                    System.out.println("Name should contain only letters and spaces!");
                    continue;
                }
                break;
            }

            // Age validation
            int age = 0;
            while(age <= 0 || age > 120) {
                System.out.print("Age: ");
                try {
                    age = Integer.parseInt(scanner.nextLine());
                    if(age <= 0) {
                        System.out.println("Age must be positive!");
                    } else if(age > 120) {
                        System.out.println("Please enter a valid age!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number for age!");
                }
            }

            // Gender validation
            String gender;
            while(true) {
                System.out.print("Gender (M/F/O): ");
                gender = scanner.nextLine().trim().toUpperCase();
                if(gender.matches("[MFO]")) {
                    break;
                }
                System.out.println("Please enter M, F, or O!");
            }

            // Email validation
            String email;
            while(true) {
                System.out.print("Email: ");
                email = scanner.nextLine().trim();
                if(!isValidEmail(email)) {
                    System.out.println("Invalid email format! Please try again.");
                    continue;
                }
                if(isEmailExists(email)) {
                    System.out.println("Email already registered! Please use another email.");
                    continue;
                }
                break;
            }

            // Phone validation
            String phone;
            while(true) {
                System.out.print("Phone (10 digits): ");
                phone = scanner.nextLine().trim();
                if(!PHONE_PATTERN.matcher(phone).matches()) {
                    System.out.println("Invalid phone number! Please enter 10 digits.");
                    continue;
                }
                break;
            }

            // Password validation
            String password;
            while(true) {
                System.out.print("Password (min 6 chars): ");
                password = scanner.nextLine().trim();
                if(password.length() < 6) {
                    System.out.println("Password must be at least 6 characters!");
                    continue;
                }
                
                System.out.print("Confirm Password: ");
                String confirmPassword = scanner.nextLine().trim();
                
                if(!password.equals(confirmPassword)) {
                    System.out.println("Passwords don't match! Please try again.");
                    continue;
                }
                break;
            }

            String userData = String.join(",", name, String.valueOf(age), gender, email, phone, password);
            FileHelper.appendToFile(userFile, userData);
            System.out.println("Registration successful!");

        } catch (Exception e) {
            System.out.println("An error occurred during registration: " + e.getMessage());
        }
    }

    public User login() {
        try {
            System.out.println("\n--- User Login ---");
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            List<String> lines = FileHelper.readFile(userFile);
            for (String line : lines) {
                try {
                    String[] data = line.split(",");
                    if (data.length >= 6) {
                        String storedEmail = data[3].trim();
                        String storedPassword = data[5].trim();
                        
                        if (storedEmail.equalsIgnoreCase(email)) {
                            if (storedPassword.equals(password)) {
                                return new User(data[0], Integer.parseInt(data[1]), data[2], data[3], data[4]);
                            } else {
                                System.out.println("Incorrect password!");
                                return null;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error processing user data. Skipping corrupted entry.");
                    continue;
                }
            }
            System.out.println("No account found with that email!");
        } catch (Exception e) {
            System.out.println("An error occurred during login: " + e.getMessage());
        }
        return null;
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isEmailExists(String email) {
        List<String> lines = FileHelper.readFile(userFile);
        for (String line : lines) {
            String[] data = line.split(",");
            if (data.length >= 4 && data[3].trim().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
}