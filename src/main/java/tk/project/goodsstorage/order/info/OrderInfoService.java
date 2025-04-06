package tk.project.goodsstorage.order.info;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderInfoService {
    Map<UUID, List<OrderInfo>> findProductIdOrdersInfo();
}
