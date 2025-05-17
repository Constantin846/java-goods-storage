package tk.project.goodsstorage.services.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.dto.orchestrator.ConfirmOrderDto;
import tk.project.goodsstorage.dto.orchestrator.OrchestratorConfirmOrderDto;
import tk.project.goodsstorage.interaction.account.AccountClient;
import tk.project.goodsstorage.interaction.crm.CrmClient;
import tk.project.goodsstorage.interaction.orchestrator.OrchestratorClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class OrchestratorService {
    private final AccountClient accountClient;
    private final CrmClient crmClient;
    private final OrchestratorClient orchestratorClient;

    public UUID confirmOrder(final ConfirmOrderDto orderDto) {
        final List<String> login = List.of(orderDto.getCustomerLogin());

        final CompletableFuture<Map<String, String>> innFuture =
                crmClient.sendRequestInnByLogins(login);
        final CompletableFuture<Map<String, String>> accountNumberFuture =
                accountClient.sendRequestAccountNumbersByLogins(login);

        final String inn = innFuture.join().get(orderDto.getCustomerLogin());
        final String accountNumber = accountNumberFuture.join().get(orderDto.getCustomerLogin());

        final OrchestratorConfirmOrderDto orchestratorConfirmOrderDto = OrchestratorConfirmOrderDto.builder()
                .id(orderDto.getId())
                .deliveryAddress(orderDto.getDeliveryAddress())
                .customerInn(inn)
                .customerAccountNumber(accountNumber)
                .price(orderDto.getPrice())
                .customerLogin(orderDto.getCustomerLogin())
                .build();

        return orchestratorClient.sendRequestConfirmOrder(orchestratorConfirmOrderDto);
    }
}
