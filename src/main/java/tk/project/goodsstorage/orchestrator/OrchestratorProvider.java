package tk.project.goodsstorage.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.customer.info.CustomerInfoProvider;
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

    public UUID confirmOrder(OrchestratorConfirmOrderDto orderDto) {
        List<String> login = List.of(orderDto.getCustomerLogin());
        CompletableFuture<Map<String, String>> inn = customerInfoProvider.getLoginInn(login);
        CompletableFuture<Map<String, String>> accountNumber = customerInfoProvider.getLoginAccountNumberMap(login);

        orderDto.setCustomerInn(inn.join().get(orderDto.getCustomerLogin()));
        orderDto.setCustomerAccountNumber(accountNumber.join().get(orderDto.getCustomerLogin()));

        return orchestratorService.sendRequestConfirmOrder(orderDto);
    }
}
