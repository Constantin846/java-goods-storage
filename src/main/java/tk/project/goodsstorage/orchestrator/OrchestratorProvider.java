package tk.project.goodsstorage.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.customer.info.CustomerInfoProvider;
import tk.project.goodsstorage.orchestrator.dto.ConfirmOrderDto;
import tk.project.goodsstorage.orchestrator.dto.OrchestratorConfirmOrderDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class OrchestratorProvider {
    private final CustomerInfoProvider customerInfoProvider;
    private final OrchestratorService orchestratorService;

    public UUID confirmOrder(final ConfirmOrderDto orderDto) {
        final List<String> login = List.of(orderDto.getCustomerLogin());
        final CompletableFuture<Map<String, String>> inn = customerInfoProvider.getLoginInn(login);
        final CompletableFuture<Map<String, String>> accountNumber = customerInfoProvider.getLoginAccountNumberMap(login);

        final OrchestratorConfirmOrderDto orchestratorConfirmOrderDto = OrchestratorConfirmOrderDto.builder()
                .id(orderDto.getId())
                .deliveryAddress(orderDto.getDeliveryAddress())
                .customerInn(inn.join().get(orderDto.getCustomerLogin()))
                .customerAccountNumber(accountNumber.join().get(orderDto.getCustomerLogin()))
                .price(orderDto.getPrice())
                .customerLogin(orderDto.getCustomerLogin())
                .build();

        return orchestratorService.sendRequestConfirmOrder(orchestratorConfirmOrderDto);
    }
}
