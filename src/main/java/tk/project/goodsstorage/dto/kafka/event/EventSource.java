package tk.project.goodsstorage.dto.kafka.event;

import tk.project.goodsstorage.enums.Event;

public interface EventSource {
    Event getEvent();
}
