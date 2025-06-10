// Main.java
package main;

import auth.AdminAuth;
import auth.AuthManager;
import dao.TicketDAO;
import dao.TrainDAO;
import model.User;
import util.ConsoleColors;
import java.util.Scanner;
import events.EventManager;
import events.listeners.BookingNotificationListener;
import events.listeners.AdminActivityLogger;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static AuthManager authManager = new AuthManager();
    static TrainDAO trainDAO = new TrainDAO();
    static TicketDAO ticketDAO = new TicketDAO();

    public static void main(String[] args) {
        // Initialize Event Manager and register listeners
        EventManager eventManager = EventManager.getInstance();
        eventManager.registerListener(new BookingNotificationListener());
        eventManager.registerListener(new AdminActivityLogger());

        while (true) {
            System.out.println(ConsoleColors.CYAN + "\n===== Railway Reservation System =====" + ConsoleColors.RESET);
            System.out.println("1. User Login\n2. User Registration\n3. Admin Login\n4. Exit");
            System.out.print("Enter choice: ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        User user = authManager.login();
                        if (user != null) userMenu(user);
                        break;
                    case 2:
                        authManager.register();
                        break;
                    case 3:
                        if (AdminAuth.login()) adminMenu();
                        break;
                    case 4:
                        System.out.println(ConsoleColors.GREEN + "Thank you for using Railway Reservation System. Goodbye!" + ConsoleColors.RESET);
                        return;
                    default:
                        System.out.println(ConsoleColors.RED + "Invalid choice. Please enter 1-4." + ConsoleColors.RESET);
                }
            } catch (Exception e) {
                System.out.println(ConsoleColors.RED + "Invalid input. Please enter a number." + ConsoleColors.RESET);
                scanner.nextLine(); // clear buffer
            }
        }
    }

    private static void userMenu(User user) {
        while (true) {
            System.out.println(ConsoleColors.GREEN + "\n--- User Dashboard (" + user.getName() + ") ---" + ConsoleColors.RESET);
            System.out.println("1. Search Trains\n2. Book Ticket\n3. Cancel Ticket\n4. View My Tickets\n5. Check PNR Status\n6. Logout");
            System.out.print("Enter choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        trainDAO.searchTrainMenu();
                        break;
                    case 2:
                        ticketDAO.bookTicket(user);
                        break;
                    case 3:
                        ticketDAO.cancelTicket(user);
                        break;
                    case 4:
                        ticketDAO.viewMyTickets(user);
                        break;
                    case 5:
                        ticketDAO.checkPNRStatus();
                        break;
                    case 6:
                        System.out.println(ConsoleColors.GREEN + "Logged out successfully." + ConsoleColors.RESET);
                        return;
                    default:
                        System.out.println(ConsoleColors.RED + "Invalid choice. Please enter 1-6." + ConsoleColors.RESET);
                }
            } catch (Exception e) {
                System.out.println(ConsoleColors.RED + "Invalid input. Please enter a number." + ConsoleColors.RESET);
                scanner.nextLine(); // clear buffer
            }
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println(ConsoleColors.PURPLE + "\n--- Admin Dashboard ---" + ConsoleColors.RESET);
            System.out.println("1. Add Train\n2. View Trains\n3. Modify Train\n4. Delete Train\n5. Sort Trains\n6. Generate Admin Report\n7. View All Tickets\n8. Logout");
            System.out.print("Enter choice: ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        trainDAO.addTrain();
                        break;
                    case 2:
                        trainDAO.viewTrains();
                        break;
                    case 3:
                        trainDAO.modifyTrain();
                        break;
                    case 4:
                        trainDAO.deleteTrain();
                        break;
                    case 5:
                        trainDAO.sortTrains();
                        break;
                    case 6:
                        ticketDAO.generateAdminReport();
                        break;
                    case 7:
                        ticketDAO.viewAllTickets();
                        break;
                    case 8:
                        System.out.println(ConsoleColors.GREEN + "Logged out successfully." + ConsoleColors.RESET);
                        return;
                    default:
                        System.out.println(ConsoleColors.RED + "Invalid choice. Please enter 1-8." + ConsoleColors.RESET);
                }
            } catch (Exception e) {
                System.out.println(ConsoleColors.RED + "Invalid input. Please enter a number." + ConsoleColors.RESET);
                scanner.nextLine(); // clear buffer
            }
        }
    }
}