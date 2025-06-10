// events/listeners/AdminActivityLogger.java
package events.listeners;

import events.EventListener;
import events.RailwayEvent;
import model.Train;
import util.ConsoleColors;
import util.FileHelper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminActivityLogger implements EventListener {
    private static final String LOG_FILE = "logs/admin_activity.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onEvent(RailwayEvent event, Object data) {
        String logMessage = null;
        String timestamp = LocalDateTime.now().format(FORMATTER);

        switch (event) {
            case TRAIN_ADDED:
                if (data instanceof Train) {
                    Train train = (Train) data;
                    logMessage = String.format("[%s] ADMIN: Added Train %s - %s (%s to %s)",
                            timestamp, train.getTrainId(), train.getName(), train.getSource(), train.getDestination());
                }
                break;
            case TRAIN_MODIFIED:
                if (data instanceof Train) {
                    Train train = (Train) data;
                    logMessage = String.format("[%s] ADMIN: Modified Train %s - %s (New Seats: %d, New Fare: %.2f)",
                            timestamp, train.getTrainId(), train.getName(), train.getSeats(), train.getFare());
                }
                break;
            case TRAIN_DELETED:
                if (data instanceof String) {
                    String trainId = (String) data;
                    logMessage = String.format("[%s] ADMIN: Deleted Train %s", timestamp, trainId);
                }
                break;
            default:
                // No action for other events
                break;
        }

        if (logMessage != null) {
            FileHelper.appendToFile(LOG_FILE, logMessage);
            System.out.println(ConsoleColors.BLUE + "ADMIN LOG: " + logMessage + ConsoleColors.RESET);
        }
    }
}