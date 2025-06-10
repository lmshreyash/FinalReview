// events/EventListener.java
package events;

public interface EventListener {
    void onEvent(RailwayEvent event, Object data);
}