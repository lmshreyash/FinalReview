// TicketDAO.java
package dao;

import model.Ticket;
import model.Train;
import model.User;
import util.ConsoleColors;
import util.FileHelper;
import events.EventManager;
import events.RailwayEvent;

import java.util.*;
import java.util.regex.Pattern;

public class TicketDAO {
    private final String ticketFile = "data/tickets.txt";
    private final String waitlistFile = "data/waitlist.txt";
    private final TrainDAO trainDAO = new TrainDAO();
    private final Scanner scanner = new Scanner(System.in);
    private static final Pattern PNR_PATTERN = Pattern.compile("^PNR[0-9]{5}$");
    private static final Pattern CLASS_PATTERN = Pattern.compile("^(General|Sleeper|AC)$", Pattern.CASE_INSENSITIVE);

    public void bookTicket(User user) {
        try {
            System.out.println("\n--- Book Ticket ---");
            
            // Train ID validation
            String trainId;
            Train train;
            while(true) {
                System.out.print("Enter Train ID: ");
                trainId = scanner.nextLine().trim();
                train = trainDAO.getTrainById(trainId);
                if(train == null) {
                    System.out.println(ConsoleColors.RED + "Train not found. Please enter a valid Train ID." + ConsoleColors.RESET);
                    continue;
                }
                break;
            }

            // Passenger name validation
            String pname;
            while(true) {
                System.out.print("Passenger Name: ");
                pname = scanner.nextLine().trim();
                if(pname.isEmpty() || !pname.matches("[a-zA-Z ]+")) {
                    System.out.println(ConsoleColors.RED + "Invalid name! Only letters and spaces allowed." + ConsoleColors.RESET);
                    continue;
                }
                break;
            }

            // Passenger age validation
            int page = 0;
            while(page <= 0 || page > 120) {
                System.out.print("Passenger Age: ");
                try {
                    page = Integer.parseInt(scanner.nextLine());
                    if(page <= 0 || page > 120) {
                        System.out.println(ConsoleColors.RED + "Please enter a valid age (1-120)." + ConsoleColors.RESET);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(ConsoleColors.RED + "Please enter a valid number for age." + ConsoleColors.RESET);
                }
            }

            // Travel class validation
            String travelClass;
            while(true) {
                System.out.print("Enter Travel Class (General/Sleeper/AC): ");
                travelClass = scanner.nextLine().trim();
                if(!CLASS_PATTERN.matcher(travelClass).matches()) {
                    System.out.println(ConsoleColors.RED + "Invalid class! Please choose General, Sleeper, or AC." + ConsoleColors.RESET);
                    continue;
                }
                break;
            }

            if (train.getSeats() <= 0) {
                System.out.println(ConsoleColors.YELLOW + "No seats available. Adding to waitlist..." + ConsoleColors.RESET);
                String waitlistEntry = String.join(",", user.getEmail(), trainId, pname, 
                    String.valueOf(page), travelClass);
                FileHelper.appendToFile(waitlistFile, waitlistEntry);
                System.out.println(ConsoleColors.GREEN + "Added to waitlist successfully!" + ConsoleColors.RESET);
                return;
            }

            String pnr = generatePNR();
            Ticket ticket = new Ticket(pnr, trainId, user.getEmail(), pname, page, travelClass);
            FileHelper.appendToFile(ticketFile, ticket.toCSV());
            trainDAO.updateTrainSeats(trainId, train.getSeats() - 1);

            System.out.println(ConsoleColors.GREEN + "\nBooking successful!" + ConsoleColors.RESET);
            printTicketReceipt(ticket, train);

            // Dispatch TICKET_BOOKED event
            EventManager.getInstance().dispatchEvent(RailwayEvent.TICKET_BOOKED, new Object[]{ticket, train});

        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error during ticket booking: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    public void cancelTicket(User user) {
        try {
            System.out.print("Enter PNR to cancel: ");
            String pnr = scanner.nextLine().trim();
            
            if(!PNR_PATTERN.matcher(pnr).matches()) {
                System.out.println(ConsoleColors.RED + "Invalid PNR format! PNR should be in format PNR12345" + ConsoleColors.RESET);
                return;
            }

            List<Ticket> tickets = getAllTickets();
            boolean found = false;
            String trainIdToFreeSeat = "";
            Ticket cancelledTicket = null; // To store the cancelled ticket for event dispatch

            Iterator<Ticket> it = tickets.iterator();
            while (it.hasNext()) {
                Ticket t = it.next();
                if (t.getPnr().equalsIgnoreCase(pnr) && t.getUserEmail().equalsIgnoreCase(user.getEmail())) {
                    trainIdToFreeSeat = t.getTrainId();
                    cancelledTicket = t;
                    it.remove();
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println(ConsoleColors.RED + "Ticket not found or you don't have permission to cancel this ticket." + ConsoleColors.RESET);
                return;
            }

            List<String> updated = new ArrayList<>();
            for (Ticket t : tickets) updated.add(t.toCSV());
            FileHelper.overwriteFile(ticketFile, updated);
            
            Train train = trainDAO.getTrainById(trainIdToFreeSeat);
            if (train != null) {
                trainDAO.updateTrainSeats(trainIdToFreeSeat, train.getSeats() + 1);
            }
            System.out.println(ConsoleColors.GREEN + "Ticket cancelled successfully." + ConsoleColors.RESET);
            
            // Dispatch TICKET_CANCELLED event
            if (cancelledTicket != null) {
                EventManager.getInstance().dispatchEvent(RailwayEvent.TICKET_CANCELLED, new Object[]{cancelledTicket, user.getEmail()});
            }

            processWaitlist(trainIdToFreeSeat);

        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error during ticket cancellation: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    private void processWaitlist(String trainId) {
        try {
            List<String> waitlist = FileHelper.readFile(waitlistFile);
            if (waitlist.isEmpty()) return;

            List<String> updatedWaitlist = new ArrayList<>();
            boolean assigned = false;
            Train train = trainDAO.getTrainById(trainId);

            for (String entry : waitlist) {
                if (assigned || train == null || train.getSeats() <= 0) {
                    updatedWaitlist.add(entry);
                    continue;
                }

                String[] parts = entry.split(",");
                if (parts.length == 5 && parts[1].equalsIgnoreCase(trainId)) {
                    String userEmail = parts[0];
                    String passengerName = parts[2];
                    int passengerAge = Integer.parseInt(parts[3]);
                    String travelClass = parts[4];

                    // Check if a seat became available
                    if (train.getSeats() > 0) {
                        String pnr = generatePNR();
                        Ticket confirmedTicket = new Ticket(pnr, trainId, userEmail, passengerName, passengerAge, travelClass);
                        FileHelper.appendToFile(ticketFile, confirmedTicket.toCSV());
                        trainDAO.updateTrainSeats(trainId, train.getSeats() - 1); // Decrease seat count

                        System.out.println(ConsoleColors.GREEN + "Waitlist ticket confirmed for " + passengerName + " on " + train.getName() + " (PNR: " + pnr + ")!" + ConsoleColors.RESET);
                        printTicketReceipt(confirmedTicket, train);
                        assigned = true; // Only assign one waitlist ticket per cancellation for simplicity

                        // Dispatch WAITLIST_PROCESSED event
                        EventManager.getInstance().dispatchEvent(RailwayEvent.WAITLIST_PROCESSED, new Object[]{confirmedTicket, entry});

                    } else {
                        updatedWaitlist.add(entry); // No seats, keep on waitlist
                    }
                } else {
                    updatedWaitlist.add(entry); // Not for this train or malformed entry
                }
            }
            FileHelper.overwriteFile(waitlistFile, updatedWaitlist);

        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error processing waitlist: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    public void viewMyTickets(User user) {
        try {
            List<Ticket> myTickets = new ArrayList<>();
            for (Ticket ticket : getAllTickets()) {
                if (ticket.getUserEmail().equalsIgnoreCase(user.getEmail())) {
                    myTickets.add(ticket);
                }
            }

            if (myTickets.isEmpty()) {
                System.out.println(ConsoleColors.YELLOW + "You have not booked any tickets yet." + ConsoleColors.RESET);
                return;
            }

            System.out.println(ConsoleColors.CYAN + "\n--- Your Booked Tickets ---" + ConsoleColors.RESET);
            for (Ticket ticket : myTickets) {
                printTicketDetails(ticket);
            }
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error viewing your tickets: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    public void checkPNRStatus() {
        try {
            System.out.print("Enter PNR to check status: ");
            String pnr = scanner.nextLine().trim();

            if(!PNR_PATTERN.matcher(pnr).matches()) {
                System.out.println(ConsoleColors.RED + "Invalid PNR format! PNR should be in format PNR12345" + ConsoleColors.RESET);
                return;
            }

            Ticket foundTicket = null;
            for (Ticket ticket : getAllTickets()) {
                if (ticket.getPnr().equalsIgnoreCase(pnr)) {
                    foundTicket = ticket;
                    break;
                }
            }

            if (foundTicket != null) {
                System.out.println(ConsoleColors.GREEN + "\n--- PNR Status: CONFIRMED ---" + ConsoleColors.RESET);
                printTicketDetails(foundTicket);
            } else {
                // Check waitlist
                List<String> waitlist = FileHelper.readFile(waitlistFile);
                boolean onWaitlist = false;
                for (String entry : waitlist) {
                    // Waitlist entry format: userEmail,trainId,pname,page,travelClass
                    // We don't store PNR in waitlist, so can't directly check PNR.
                    // This is a limitation based on current waitlist file format.
                    // A real system would have unique waitlist IDs.
                }

                if (onWaitlist) {
                    System.out.println(ConsoleColors.YELLOW + "PNR: " + pnr + " is currently on waitlist (Status: PENDING)." + ConsoleColors.RESET);
                } else {
                    System.out.println(ConsoleColors.RED + "PNR not found. It might be invalid, cancelled, or never existed." + ConsoleColors.RESET);
                }
            }
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error checking PNR status: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        try {
            List<String> lines = FileHelper.readFile(ticketFile);
            for (String line : lines) {
                try {
                    String[] data = line.split(",");
                    if (data.length == 6) {
                        tickets.add(new Ticket(data[0], data[1], data[2], data[3], 
                                 Integer.parseInt(data[4]), data[5]));
                    }
                } catch (Exception e) {
                    System.out.println(ConsoleColors.RED + "Skipping corrupted ticket entry: " + line + ConsoleColors.RESET);
                }
            }
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error reading tickets: " + e.getMessage() + ConsoleColors.RESET);
        }
        return tickets;
    }

    public void viewAllTickets() {
        try {
            List<Ticket> tickets = getAllTickets();
            if (tickets.isEmpty()) {
                System.out.println(ConsoleColors.YELLOW + "No tickets booked in the system yet." + ConsoleColors.RESET);
                return;
            }

            System.out.println(ConsoleColors.CYAN + "\n--- All Booked Tickets ---" + ConsoleColors.RESET);
            for (Ticket ticket : tickets) {
                printTicketDetails(ticket);
            }
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error viewing all tickets: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    public void generateAdminReport() {
        try {
            System.out.println(ConsoleColors.CYAN + "\n--- Admin Report ---" + ConsoleColors.RESET);
            List<Train> trains = trainDAO.getAllTrains();
            List<Ticket> tickets = getAllTickets();
            List<String> waitlist = FileHelper.readFile(waitlistFile);

            System.out.println("\nTotal Trains: " + trains.size());
            System.out.println("Total Booked Tickets: " + tickets.size());
            System.out.println("Current Waitlist Entries: " + waitlist.size());

            System.out.println("\n--- Train Occupancy ---");
            Map<String, Integer> bookedSeatsPerTrain = new HashMap<>();
            for (Ticket ticket : tickets) {
                bookedSeatsPerTrain.put(ticket.getTrainId(), 
                                        bookedSeatsPerTrain.getOrDefault(ticket.getTrainId(), 0) + 1);
            }

            for (Train train : trains) {
                int booked = bookedSeatsPerTrain.getOrDefault(train.getTrainId(), 0);
                System.out.printf("Train %s (%s): Booked Seats: %d/%d (Available: %d)%n",
                                  train.getTrainId(), train.getName(), booked, 
                                  (booked + train.getSeats()), train.getSeats());
            }

            System.out.println("\n--- Top 5 Most Booked Trains ---");
            bookedSeatsPerTrain.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    Train train = trainDAO.getTrainById(entry.getKey());
                    if (train != null) {
                        System.out.printf("%s (%s): %d tickets%n", train.getName(), train.getTrainId(), entry.getValue());
                    }
                });

            System.out.println(ConsoleColors.CYAN + "--------------------" + ConsoleColors.RESET);

        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error generating admin report: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    private String generatePNR() {
        Random rand = new Random();
        String pnr;
        Set<String> existingPnrs = new HashSet<>();
        for (Ticket t : getAllTickets()) {
            existingPnrs.add(t.getPnr());
        }

        do {
            pnr = "PNR" + String.format("%05d", rand.nextInt(100000));
        } while (existingPnrs.contains(pnr));
        return pnr;
    }

    private void printTicketReceipt(Ticket ticket, Train train) {
        System.out.println(ConsoleColors.CYAN + "\n=====================" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "    TICKET RECEIPT" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.CYAN + "=====================" + ConsoleColors.RESET);
        System.out.println("PNR: " + ticket.getPnr());
        System.out.println("Train: " + train.getName() + " (" + train.getTrainId() + ")");
        System.out.println("Route: " + train.getSource() + " to " + train.getDestination());
        System.out.println("Date: " + train.getDate() + " | Time: " + train.getTime());
        System.out.println("Passenger: " + ticket.getPassengerName() + " (Age: " + ticket.getPassengerAge() + ")");
        System.out.println("Class: " + ticket.getTravelClass());
        System.out.printf("Fare: ₹%.2f\\n", train.getFare());
        System.out.println("Status: CONFIRMED");
        System.out.println(ConsoleColors.CYAN + "=====================" + ConsoleColors.RESET);
    }

    private void printTicketDetails(Ticket ticket) {
        Train train = trainDAO.getTrainById(ticket.getTrainId());
        String trainInfo = (train != null) ? train.getName() + " (" + ticket.getTrainId() + ")" : ticket.getTrainId();
        
        System.out.println(ConsoleColors.BLUE + "PNR: " + ticket.getPnr() + ConsoleColors.RESET);
        System.out.println("Train: " + trainInfo);
        System.out.println("Passenger: " + ticket.getPassengerName() + " (Age: " + ticket.getPassengerAge() + ")");
        System.out.println("Class: " + ticket.getTravelClass());
        System.out.println("Booked by: " + ticket.getUserEmail());
        if (train != null) {
            System.out.println("Route: " + train.getSource() + " to " + train.getDestination());
            System.out.println("Date: " + train.getDate() + " | Time: " + train.getTime());
        }
        System.out.println(ConsoleColors.CYAN + "----------------------------------" + ConsoleColors.RESET);
    }
}