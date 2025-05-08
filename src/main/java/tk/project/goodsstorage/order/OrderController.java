package tk.project.goodsstorage.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tk.project.goodsstorage.orchestrator.OrchestratorIdWrapper;
import tk.project.goodsstorage.order.dto.create.CreateOrderDto;
import tk.project.goodsstorage.order.dto.create.CreateOrderRequest;
import tk.project.goodsstorage.order.dto.find.FindOrderDto;
import tk.project.goodsstorage.order.dto.find.FindOrderResponse;
import tk.project.goodsstorage.order.dto.update.SetOrderStatusRequest;
import tk.project.goodsstorage.order.dto.update.UpdateOrderDto;
import tk.project.goodsstorage.order.dto.update.UpdateOrderRequest;
import tk.project.goodsstorage.order.dto.update.UpdateOrderResponse;
import tk.project.goodsstorage.order.dto.update.UpdateOrderStatusDto;
import tk.project.goodsstorage.order.info.OrderInfo;
import tk.project.goodsstorage.order.info.OrderInfoService;
import tk.project.goodsstorage.order.mapper.OrderDtoMapper;
import tk.project.goodsstorage.order.service.OrderService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {
    private static final String ID = "id";
    private static final String ORDER_ID = "orderId";
    private static final String ORDER_ID_PATH = "/{orderId}";
    private final OrchestratorIdWrapper orchestratorIdWrapper;
    private final OrderDtoMapper mapper;
    private final OrderInfoService orderInfoService;
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, UUID> create(@Valid @RequestBody final CreateOrderRequest orderRequest) {
        log.info("Create order: {}", orderRequest);
        CreateOrderDto orderDto = mapper.toCreateOrderDto(orderRequest);
        return Map.of(ID, orderService.create(orderDto));
    }

    @PostMapping(ORDER_ID_PATH + "/confirm")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, UUID> confirmById(@PathVariable(ORDER_ID) final UUID orderId) {
        log.info("Confirm order with id: {}", orderId);
        UUID businessKey = orderService.confirmById(orderId);
        return Map.of("businessKey", businessKey);
    }

    @GetMapping(ORDER_ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public FindOrderResponse findById(@PathVariable(ORDER_ID) final UUID orderId) {
        log.info("Find order by id: {}", orderId);
        FindOrderDto orderDto = orderService.findById(orderId);
        return mapper.toFindOrderResponse(orderDto);
    }

    @GetMapping("/info/group-by-product-id")
    @ResponseStatus(HttpStatus.OK)
    public Map<UUID, List<OrderInfo>> findProductIdOrdersInfo() {
        log.info("Find orders info and group by product id");
        return orderInfoService.findProductIdOrdersInfo();
    }

    @PatchMapping(ORDER_ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public UpdateOrderResponse update(@Valid @RequestBody final UpdateOrderRequest orderRequest,
                                      @PathVariable(ORDER_ID) final UUID orderId) {
        log.info("Update order with id: {}", orderId);
        UpdateOrderDto orderDto = mapper.toUpdateOrderDto(orderRequest);
        return mapper.toUpdateOrderResponse(orderService.update(orderDto, orderId));
    }

    @PatchMapping(ORDER_ID_PATH + "/status")
    @ResponseStatus(HttpStatus.OK)
    public UpdateOrderStatusDto setStatusDone(@PathVariable(ORDER_ID) final UUID orderId) {
        log.info("Set status DONE to order with id: {}", orderId);
        return orderService.setStatusDone(orderId);
    }

    @PatchMapping("/set-status")
    @ResponseStatus(HttpStatus.OK)
    public UpdateOrderStatusDto setStatusByOrchestrator(@RequestBody final SetOrderStatusRequest statusRequest) {
        log.info("Change status to order: {}", statusRequest);
        orchestratorIdWrapper.checkOrchestratorAccess();
        return orderService.setStatusByOrchestrator(statusRequest);
    }

    @DeleteMapping(ORDER_ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable(ORDER_ID) final UUID orderId) {
        log.info("Delete order by id: {}", orderId);
        orderService.deleteById(orderId);
    }
}
