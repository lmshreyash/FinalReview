// events/listeners/BookingNotificationListener.java
package events.listeners;

import events.EventListener;
import events.RailwayEvent;
import model.Ticket;
import model.Train;
import util.ConsoleColors;

public class BookingNotificationListener implements EventListener {
    @Override
    public void onEvent(RailwayEvent event, Object data) {
        if (event == RailwayEvent.TICKET_BOOKED && data instanceof Object[]) {
            Object[] eventData = (Object[]) data;
            if (eventData.length == 2 && eventData[0] instanceof Ticket && eventData[1] instanceof Train) {
                Ticket ticket = (Ticket) eventData[0];
                Train train = (Train) eventData[1];
                System.out.println(ConsoleColors.YELLOW + "\n--- NOTIFICATION: Ticket Booked ---" + ConsoleColors.RESET);
                System.out.println("User: " + ticket.getUserEmail() + " booked a ticket for " + train.getName() + " (PNR: " + ticket.getPnr() + ")");
                System.out.println("Train: " + train.getSource() + " to " + train.getDestination() + " on " + train.getDate());
            }
        } else if (event == RailwayEvent.TICKET_CANCELLED && data instanceof Object[]) {
            Object[] eventData = (Object[]) data;
            if (eventData.length == 2 && eventData[0] instanceof Ticket && eventData[1] instanceof String) {
                Ticket ticket = (Ticket) eventData[0];
                String userEmail = (String) eventData[1];
                System.out.println(ConsoleColors.YELLOW + "\n--- NOTIFICATION: Ticket Cancelled ---" + ConsoleColors.RESET);
                System.out.println("User: " + userEmail + " cancelled ticket with PNR: " + ticket.getPnr());
            }
        } else if (event == RailwayEvent.WAITLIST_PROCESSED && data instanceof Object[]) {
            Object[] eventData = (Object[]) data;
            if (eventData.length == 2 && eventData[0] instanceof Ticket && eventData[1] instanceof String) {
                Ticket confirmedTicket = (Ticket) eventData[0];
                String originalWaitlistEntry = (String) eventData[1];
                String userEmail = originalWaitlistEntry.split(",")[0];
                System.out.println(ConsoleColors.YELLOW + "\n--- NOTIFICATION: Waitlist Confirmed ---" + ConsoleColors.RESET);
                System.out.println("User: " + userEmail + "'s waitlist ticket confirmed! PNR: " + confirmedTicket.getPnr());
                System.out.println("Train: " + confirmedTicket.getTrainId());
            }
        }
    }
}