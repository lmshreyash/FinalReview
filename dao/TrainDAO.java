// TrainDAO.java
package dao;

import model.Train;
import util.ConsoleColors;
import util.FileHelper;
import events.EventManager;
import events.RailwayEvent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

public class TrainDAO {
    private final String trainFile = "data/trains.txt";
    private final Scanner scanner = new Scanner(System.in);
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    private static final Pattern TRAIN_ID_PATTERN = Pattern.compile("^TRAIN\\d{3}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void addTrain() {
        try {
            System.out.println("\n--- Add Train ---");
            
            // Train ID validation
            String id;
            while(true) {
                System.out.print("Train ID (format TRAIN001): ");
                id = scanner.nextLine().trim().toUpperCase();
                if(!TRAIN_ID_PATTERN.matcher(id).matches()) {
                    System.out.println(ConsoleColors.RED + "Invalid format! Must be TRAIN followed by 3 digits (e.g. TRAIN001)" + ConsoleColors.RESET);
                    continue;
                }
                if(getTrainById(id) != null) {
                    System.out.println(ConsoleColors.RED + "Train ID already exists!" + ConsoleColors.RESET);
                    continue;
                }
                break;
            }

            // Name validation
            String name;
            while(true) {
                System.out.print("Name: ");
                name = scanner.nextLine().trim();
                if(name.isEmpty()) {
                    System.out.println(ConsoleColors.RED + "Name cannot be empty!" + ConsoleColors.RESET);
                    continue;
                }
                if(name.length() > 100) {
                    System.out.println(ConsoleColors.RED + "Name too long! Max 100 characters." + ConsoleColors.RESET);
                    continue;
                }
                break;
            }

            // Source and destination validation
            String src, dest;
            while(true) {
                System.out.print("Source Station: ");
                src = scanner.nextLine().trim();
                System.out.print("Destination Station: ");
                dest = scanner.nextLine().trim();
                
                if(src.equalsIgnoreCase(dest)) {
                    System.out.println(ConsoleColors.RED + "Source and destination cannot be same!" + ConsoleColors.RESET);
                    continue;
                }
                if(src.isEmpty() || dest.isEmpty()) {
                    System.out.println(ConsoleColors.RED + "Station names cannot be empty!" + ConsoleColors.RESET);
                    continue;
                }
                break;
            }

            // Date validation
            String date;
            while(true) {
                System.out.print("Date (YYYY-MM-DD): ");
                date = scanner.nextLine().trim();
                try {
                    LocalDate parsedDate = LocalDate.parse(date, DATE_FORMATTER);
                    if(parsedDate.isBefore(LocalDate.now())) {
                        System.out.println(ConsoleColors.RED + "Date cannot be in the past!" + ConsoleColors.RESET);
                        continue;
                    }
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println(ConsoleColors.RED + "Invalid date format! Please use YYYY-MM-DD." + ConsoleColors.RESET);
                }
            }

            // Time validation
            String time;
            while(true) {
                System.out.print("Departure Time (HH:MM): ");
                time = scanner.nextLine().trim();
                if(!TIME_PATTERN.matcher(time).matches()) {
                    System.out.println(ConsoleColors.RED + "Invalid time format! Use 24-hour format (HH:MM)" + ConsoleColors.RESET);
                    continue;
                }
                break;
            }

            // Seats validation
            int seats = 0;
            while(seats <= 0 || seats > 1000) {
                System.out.print("Seats Available (1-1000): ");
                try {
                    seats = Integer.parseInt(scanner.nextLine());
                    if(seats <= 0 || seats > 1000) {
                        System.out.println(ConsoleColors.RED + "Please enter a value between 1 and 1000" + ConsoleColors.RESET);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(ConsoleColors.RED + "Please enter a valid number!" + ConsoleColors.RESET);
                }
            }

            // Fare validation
            double fare = 0;
            while(fare <= 0 || fare > 100000) {
                System.out.print("Fare (1-100000): ");
                try {
                    fare = Double.parseDouble(scanner.nextLine());
                    if(fare <= 0 || fare > 100000) {
                        System.out.println(ConsoleColors.RED + "Please enter a value between 1 and 100000" + ConsoleColors.RESET);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(ConsoleColors.RED + "Please enter a valid amount!" + ConsoleColors.RESET);
                }
            }

            Train train = new Train(id, name, src, dest, date, time, seats, fare);
            FileHelper.appendToFile(trainFile, train.toString());
            System.out.println(ConsoleColors.GREEN + "Train added successfully!" + ConsoleColors.RESET);

            // Dispatch TRAIN_ADDED event
            EventManager.getInstance().dispatchEvent(RailwayEvent.TRAIN_ADDED, train);

        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error adding train: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    public List<Train> getAllTrains() {
        List<Train> trains = new ArrayList<>();
        try {
            List<String> lines = FileHelper.readFile(trainFile);
            for (String line : lines) {
                try {
                    String[] data = line.split(",");
                    if (data.length == 8) {
                        trains.add(new Train(data[0], data[1], data[2], data[3], 
                                 data[4], data[5], Integer.parseInt(data[6]), 
                                 Double.parseDouble(data[7])));
                    }
                } catch (Exception e) {
                    System.out.println(ConsoleColors.RED + "Skipping corrupted train entry: " + line + ConsoleColors.RESET);
                }
            }
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error reading trains: " + e.getMessage() + ConsoleColors.RESET);
        }
        return trains;
    }

    public void viewTrains() {
        try {
            List<Train> trains = getAllTrains();
            if (trains.isEmpty()) {
                System.out.println(ConsoleColors.YELLOW + "No trains available in the system." + ConsoleColors.RESET);
                return;
            }
            
            System.out.println("\n--- Available Trains ---");
            for (Train train : trains) {
                printTrainSummary(train);
            }
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error viewing trains: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    public Train getTrainById(String trainId) {
        for (Train train : getAllTrains()) {
            if (train.getTrainId().equalsIgnoreCase(trainId)) {
                return train;
            }
        }
        return null;
    }

    public void updateTrainSeats(String trainId, int newSeats) {
        List<Train> trains = getAllTrains();
        boolean updated = false;
        for (int i = 0; i < trains.size(); i++) {
            if (trains.get(i).getTrainId().equalsIgnoreCase(trainId)) {
                trains.get(i).setSeats(newSeats);
                updated = true;
                break;
            }
        }
        if (updated) {
            saveTrains(trains);
        }
    }

    public void modifyTrain() {
        try {
            System.out.println("\n--- Modify Train ---");
            System.out.print("Enter Train ID to modify: ");
            String id = scanner.nextLine().trim().toUpperCase();
            
            Train trainToModify = getTrainById(id);
            if (trainToModify == null) {
                System.out.println(ConsoleColors.RED + "Train not found." + ConsoleColors.RESET);
                return;
            }

            printTrainDetails(trainToModify);

            // Allow modification of name, source, destination, date, time, seats, fare
            System.out.println("\nEnter new values (leave blank to keep current):");

            System.out.print("New Name (" + trainToModify.getName() + "): ");
            String newName = scanner.nextLine().trim();
            if (!newName.isEmpty()) {
                if(newName.length() > 100) {
                    System.out.println(ConsoleColors.RED + "Name too long! Max 100 characters. Keeping old name." + ConsoleColors.RESET);
                } else {
                    trainToModify.setTrainName(newName);
                }
            }

            String newSrc = "";
            String newDest = "";
            boolean stationChangeValid = false;
            while(!stationChangeValid) {
                System.out.print("New Source Station (" + trainToModify.getSource() + "): ");
                newSrc = scanner.nextLine().trim();
                System.out.print("New Destination Station (" + trainToModify.getDestination() + "): ");
                newDest = scanner.nextLine().trim();

                if (newSrc.isEmpty() && newDest.isEmpty()) {
                    stationChangeValid = true; // No change
                } else if (!newSrc.isEmpty() && newSrc.equalsIgnoreCase(newDest.isEmpty() ? trainToModify.getDestination() : newDest)) {
                    System.out.println(ConsoleColors.RED + "Source and destination cannot be the same! Please re-enter." + ConsoleColors.RESET);
                } else if (newDest.isEmpty() && newSrc.equalsIgnoreCase(trainToModify.getDestination())) {
                     System.out.println(ConsoleColors.RED + "Source and destination cannot be the same! Please re-enter." + ConsoleColors.RESET);
                }
                 else {
                    if (!newSrc.isEmpty()) trainToModify.setSource(newSrc);
                    if (!newDest.isEmpty()) trainToModify.setDestination(newDest);
                    stationChangeValid = true;
                }
            }

            System.out.print("New Date (YYYY-MM-DD) (" + trainToModify.getDate() + "): ");
            String newDate = scanner.nextLine().trim();
            if (!newDate.isEmpty()) {
                try {
                    LocalDate parsedDate = LocalDate.parse(newDate, DATE_FORMATTER);
                    if(parsedDate.isBefore(LocalDate.now())) {
                        System.out.println(ConsoleColors.RED + "Date cannot be in the past! Keeping old date." + ConsoleColors.RESET);
                    } else {
                        trainToModify.setDate(newDate);
                    }
                } catch (DateTimeParseException e) {
                    System.out.println(ConsoleColors.RED + "Invalid date format! Keeping old date." + ConsoleColors.RESET);
                }
            }

            System.out.print("New Departure Time (HH:MM) (" + trainToModify.getTime() + "): ");
            String newTime = scanner.nextLine().trim();
            if (!newTime.isEmpty()) {
                if(!TIME_PATTERN.matcher(newTime).matches()) {
                    System.out.println(ConsoleColors.RED + "Invalid time format! Keeping old time." + ConsoleColors.RESET);
                } else {
                    trainToModify.setTime(newTime);
                }
            }

            System.out.print("New Seats Available (" + trainToModify.getSeats() + "): ");
            String newSeatsStr = scanner.nextLine().trim();
            if (!newSeatsStr.isEmpty()) {
                try {
                    int newSeats = Integer.parseInt(newSeatsStr);
                    if (newSeats > 0 && newSeats <= 1000) {
                        trainToModify.setSeats(newSeats);
                    } else {
                        System.out.println(ConsoleColors.RED + "Invalid seat count (1-1000)! Keeping old seat count." + ConsoleColors.RESET);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(ConsoleColors.RED + "Invalid number for seats! Keeping old seat count." + ConsoleColors.RESET);
                }
            }

            System.out.print("New Fare (" + trainToModify.getFare() + "): ");
            String newFareStr = scanner.nextLine().trim();
            if (!newFareStr.isEmpty()) {
                try {
                    double newFare = Double.parseDouble(newFareStr);
                    if (newFare > 0 && newFare <= 100000) {
                        trainToModify.setFare(newFare);
                    } else {
                        System.out.println(ConsoleColors.RED + "Invalid fare amount (1-100000)! Keeping old fare." + ConsoleColors.RESET);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(ConsoleColors.RED + "Invalid number for fare! Keeping old fare." + ConsoleColors.RESET);
                }
            }

            List<Train> trains = getAllTrains();
            for (int i = 0; i < trains.size(); i++) {
                if (trains.get(i).getTrainId().equalsIgnoreCase(id)) {
                    trains.set(i, trainToModify);
                    break;
                }
            }
            saveTrains(trains);
            System.out.println(ConsoleColors.GREEN + "Train modified successfully!" + ConsoleColors.RESET);

            // Dispatch TRAIN_MODIFIED event
            EventManager.getInstance().dispatchEvent(RailwayEvent.TRAIN_MODIFIED, trainToModify);

        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error modifying train: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    public void deleteTrain() {
        try {
            System.out.println("\n--- Delete Train ---");
            System.out.print("Enter Train ID to delete: ");
            String id = scanner.nextLine().trim().toUpperCase();
            
            Train trainToDelete = getTrainById(id);
            if (trainToDelete == null) {
                System.out.println(ConsoleColors.RED + "Train not found." + ConsoleColors.RESET);
                return;
            }

            System.out.print(ConsoleColors.YELLOW + "Are you sure you want to delete Train " + id + "? (yes/no): " + ConsoleColors.RESET);
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (!confirmation.equals("yes")) {
                System.out.println(ConsoleColors.BLUE + "Train deletion cancelled." + ConsoleColors.RESET);
                return;
            }

            List<Train> trains = getAllTrains();
            if (trains.removeIf(train -> train.getTrainId().equalsIgnoreCase(id))) {
                saveTrains(trains);
                System.out.println(ConsoleColors.GREEN + "Train " + id + " deleted successfully!" + ConsoleColors.RESET);
                // Dispatch TRAIN_DELETED event
                EventManager.getInstance().dispatchEvent(RailwayEvent.TRAIN_DELETED, id);
            } else {
                System.out.println(ConsoleColors.RED + "Train not found." + ConsoleColors.RESET);
            }
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error deleting train: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    public void searchTrainMenu() {
        try {
            System.out.println("\n--- Search Train ---");
            System.out.println("1. By Source & Destination");
            System.out.println("2. By Date");
            System.out.println("3. By Train ID or Name");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter choice: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "Please enter a valid number!" + ConsoleColors.RESET);
                return;
            }

            List<Train> trains = getAllTrains();
            if (trains.isEmpty()) {
                System.out.println(ConsoleColors.YELLOW + "No trains available to search." + ConsoleColors.RESET);
                return;
            }

            boolean found = false;
            switch (choice) {
                case 1:
                    System.out.print("Source: ");
                    String src = scanner.nextLine().trim();
                    System.out.print("Destination: ");
                    String dest = scanner.nextLine().trim();
                    System.out.println("\n--- Search Results ---");
                    for (Train t : trains) {
                        if (t.getSource().equalsIgnoreCase(src) && t.getDestination().equalsIgnoreCase(dest)) {
                            printTrainDetails(t);
                            found = true;
                        }
                    }
                    break;
                case 2:
                    System.out.print("Date (YYYY-MM-DD): ");
                    String date = scanner.nextLine().trim();
                    System.out.println("\n--- Search Results ---");
                    for (Train t : trains) {
                        if (t.getDate().equalsIgnoreCase(date)) {
                            printTrainDetails(t);
                            found = true;
                        }
                    }
                    break;
                case 3:
                    System.out.print("Enter Train ID or Name: ");
                    String query = scanner.nextLine().trim();
                    System.out.println("\n--- Search Results ---");
                    for (Train t : trains) {
                        if (t.getTrainId().equalsIgnoreCase(query) || t.getName().toLowerCase().contains(query.toLowerCase())) {
                            printTrainDetails(t);
                            found = true;
                        }
                    }
                    break;
                case 4:
                    return;
                default:
                    System.out.println(ConsoleColors.RED + "Invalid choice." + ConsoleColors.RESET);
                    break;
            }

            if (!found) {
                System.out.println(ConsoleColors.YELLOW + "No trains found matching your criteria." + ConsoleColors.RESET);
            }

        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error during train search: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    public void sortTrains() {
        try {
            List<Train> trains = getAllTrains();
            if (trains.isEmpty()) {
                System.out.println(ConsoleColors.YELLOW + "No trains to sort." + ConsoleColors.RESET);
                return;
            }

            System.out.println("\n--- Sort Trains By ---");
            System.out.println("1. Train ID\n2. Name\n3. Source\n4. Destination\n5. Date\n6. Fare\n7. Back");
            System.out.print("Enter choice: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "Invalid input. Please enter a number." + ConsoleColors.RESET);
                return;
            }

            Comparator<Train> comparator = null;
            String sortCriteria = "";

            switch (choice) {
                case 1:
                    comparator = Comparator.comparing(Train::getTrainId);
                    sortCriteria = "Train ID";
                    break;
                case 2:
                    comparator = Comparator.comparing(Train::getName);
                    sortCriteria = "Name";
                    break;
                case 3:
                    comparator = Comparator.comparing(Train::getSource);
                    sortCriteria = "Source";
                    break;
                case 4:
                    comparator = Comparator.comparing(Train::getDestination);
                    sortCriteria = "Destination";
                    break;
                case 5:
                    comparator = Comparator.comparing(Train::getDate); // Assuming YYYY-MM-DD allows string comparison
                    sortCriteria = "Date";
                    break;
                case 6:
                    comparator = Comparator.comparingDouble(Train::getFare);
                    sortCriteria = "Fare";
                    break;
                case 7:
                    return;
                default:
                    System.out.println(ConsoleColors.RED + "Invalid choice." + ConsoleColors.RESET);
                    return;
            }

            if (comparator != null) {
                trains.sort(comparator);
                System.out.println(ConsoleColors.GREEN + "\nTrains sorted by " + sortCriteria + ":" + ConsoleColors.RESET);
                for (Train train : trains) {
                    printTrainSummary(train);
                }
            }

        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error sorting trains: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    private void saveTrains(List<Train> trains) {
        try {
            List<String> lines = new ArrayList<>();
            for(Train t : trains) {
                lines.add(t.toString());
            }
            FileHelper.overwriteFile(trainFile, lines);
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error saving trains: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    private void printTrainSummary(Train t) {
        System.out.println(ConsoleColors.CYAN + "ID: " + t.getTrainId() + ConsoleColors.RESET + 
                         " | " + t.getName() + " | " + t.getSource() + " → " + t.getDestination());
        System.out.println("Date: " + t.getDate() + " | Time: " + t.getTime() + 
                         " | Seats: " + t.getSeats() + " | Fare: ₹" + t.getFare());
        System.out.println("----------------------------------");
    }

    private void printTrainDetails(Train t) {
        System.out.println(ConsoleColors.CYAN + "\n=== TRAIN DETAILS ===" + ConsoleColors.RESET);
        System.out.println("ID: " + t.getTrainId());
        System.out.println("Name: " + t.getName());
        System.out.println("Route: " + t.getSource() + " to " + t.getDestination());
        System.out.println("Date: " + t.getDate());
        System.out.println("Departure Time: " + t.getTime());
        System.out.println("Available Seats: " + t.getSeats());
        System.out.printf("Fare: ₹%.2f%n", t.getFare());
        System.out.println(ConsoleColors.CYAN + "=======================" + ConsoleColors.RESET);
    }
}