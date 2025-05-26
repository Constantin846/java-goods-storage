package tk.project.goodsstorage.services.handlers;

import tk.project.goodsstorage.dto.kafka.event.EventSource;
import tk.project.goodsstorage.enums.Event;

public interface EventHandler<T extends EventSource> {

    Event getEvent();

    void handleEvent(T eventSource);
}
