// events/EventManager.java
package events;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private static EventManager instance;
    private final List<EventListener> listeners;

    private EventManager() {
        listeners = new ArrayList<>();
    }

    public static synchronized EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void registerListener(EventListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(EventListener listener) {
        listeners.remove(listener);
    }

    public void dispatchEvent(RailwayEvent event, Object data) {
        for (EventListener listener : listeners) {
            listener.onEvent(event, data);
        }
    }
}