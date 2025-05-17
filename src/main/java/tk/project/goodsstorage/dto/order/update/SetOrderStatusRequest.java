package tk.project.goodsstorage.dto.order.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tk.project.goodsstorage.enums.OrderStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class SetOrderStatusRequest {

    private final UUID orderId;

    private final OrderStatus status;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private final LocalDate deliveryDate;

    @JsonCreator
    public SetOrderStatusRequest(final UUID orderId, final OrderStatus status, final String deliveryDate) {
        this.orderId = orderId;
        this.status = status;
        this.deliveryDate = LocalDate.parse(deliveryDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public SetOrderStatusRequest(final UUID orderId, final OrderStatus status, final LocalDate deliveryDate) {
        this.orderId = orderId;
        this.status = status;
        this.deliveryDate = deliveryDate;
    }
}
