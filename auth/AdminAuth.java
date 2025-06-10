package auth;

import java.util.Scanner;

public class AdminAuth {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";
    private static final int MAX_ATTEMPTS = 3;

    public static boolean login() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Admin Login ---");
        
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            try {
                System.out.print("Username: ");
                String username = scanner.nextLine().trim();
                
                System.out.print("Password: ");
                String password = scanner.nextLine().trim();
                
                if (username.equals(USERNAME) && password.equals(PASSWORD)) {
                    System.out.println("Authentication successful!");
                    return true;
                }
                
                attempts++;
                int remaining = MAX_ATTEMPTS - attempts;
                if (remaining > 0) {
                    System.out.println("Invalid credentials. " + remaining + " attempt(s) remaining.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred during login. Please try again.");
                attempts++;
            }
        }
        
        System.out.println("Maximum attempts reached. Access denied.");
        return false;
    }
}