package tk.project.goodsstorage.kafka.handler;

import tk.project.goodsstorage.kafka.event.Event;
import tk.project.goodsstorage.kafka.event.EventSource;

public interface EventHandler<T extends EventSource> {

    Event getEvent();

    void handleEvent(T eventSource);
}
