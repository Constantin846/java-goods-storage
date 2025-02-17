package tk.project.goodsstorage.exceptions;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiError {

    String exceptionName;

    String message;

    Instant time;
}
