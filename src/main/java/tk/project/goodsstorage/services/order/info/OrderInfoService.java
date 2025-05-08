package tk.project.goodsstorage.services.order.info;

import tk.project.goodsstorage.dto.order.find.OrderInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderInfoService {
    Map<UUID, List<OrderInfo>> findProductIdOrdersInfo();
}
