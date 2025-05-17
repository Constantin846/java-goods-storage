package tk.project.goodsstorage.controllers;

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
import tk.project.goodsstorage.dto.order.create.CreateOrderDto;
import tk.project.goodsstorage.dto.order.create.CreateOrderRequest;
import tk.project.goodsstorage.dto.order.find.FindOrderDto;
import tk.project.goodsstorage.dto.order.find.FindOrderResponse;
import tk.project.goodsstorage.dto.order.find.OrderInfo;
import tk.project.goodsstorage.dto.order.update.SetOrderStatusRequest;
import tk.project.goodsstorage.dto.order.update.UpdateOrderDto;
import tk.project.goodsstorage.dto.order.update.UpdateOrderRequest;
import tk.project.goodsstorage.dto.order.update.UpdateOrderResponse;
import tk.project.goodsstorage.dto.order.update.UpdateOrderStatusDto;
import tk.project.goodsstorage.headerfilter.OrchestratorIdWrapper;
import tk.project.goodsstorage.mappers.OrderDtoMapper;
import tk.project.goodsstorage.services.order.OrderService;
import tk.project.goodsstorage.services.order.info.OrderInfoService;

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
    public Map<String, UUID> confirmById(@PathVariable(ORDER_ID) final UUID orderId) {
        log.info("Confirm order with id: {}", orderId);
        UUID businessKey = orderService.confirmById(orderId);
        return Map.of("businessKey", businessKey);
    }

    @GetMapping(ORDER_ID_PATH)
    public FindOrderResponse findById(@PathVariable(ORDER_ID) final UUID orderId) {
        log.info("Find order by id: {}", orderId);
        FindOrderDto orderDto = orderService.findById(orderId);
        return mapper.toFindOrderResponse(orderDto);
    }

    @GetMapping("/info/group-by-product-id")
    public Map<UUID, List<OrderInfo>> findProductIdOrdersInfo() {
        log.info("Find orders info and group by product id");
        return orderInfoService.findProductIdOrdersInfo();
    }

    @PatchMapping(ORDER_ID_PATH)
    public UpdateOrderResponse update(@Valid @RequestBody final UpdateOrderRequest orderRequest,
                                      @PathVariable(ORDER_ID) final UUID orderId) {
        log.info("Update order with id: {}", orderId);
        UpdateOrderDto orderDto = mapper.toUpdateOrderDto(orderRequest);
        return mapper.toUpdateOrderResponse(orderService.update(orderDto, orderId));
    }

    @PatchMapping(ORDER_ID_PATH + "/status")
    public UpdateOrderStatusDto setStatusDone(@PathVariable(ORDER_ID) final UUID orderId) {
        log.info("Set status DONE to order with id: {}", orderId);
        return orderService.setStatusDone(orderId);
    }

    @PatchMapping("/set-status")
    public UpdateOrderStatusDto setStatusByOrchestrator(@RequestBody final SetOrderStatusRequest statusRequest) {
        log.info("Change status to order: {}", statusRequest);
        orchestratorIdWrapper.checkOrchestratorAccess();
        return orderService.setStatusByOrchestrator(statusRequest);
    }

    @DeleteMapping(ORDER_ID_PATH)
    public void deleteById(@PathVariable(ORDER_ID) final UUID orderId) {
        log.info("Delete order by id: {}", orderId);
        orderService.deleteById(orderId);
    }
}
