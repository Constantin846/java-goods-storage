package tk.project.goodsstorage.services.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.dto.customer.find.CustomerInfo;
import tk.project.goodsstorage.interaction.account.AccountClient;
import tk.project.goodsstorage.interaction.crm.CrmClient;
import tk.project.goodsstorage.models.Customer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerInfoProviderImpl implements CustomerInfoProvider {
    private final AccountClient accountClient;
    private final CrmClient crmClient;

    @Override
    public Map<Long, CustomerInfo> getIdCustomerInfoMap(final Set<Customer> customers) {
        List<String> logins = customers.stream()
                .map(Customer::getLogin)
                .toList();

        CompletableFuture<Map<String, String>> loginAccountNumberMapFuture =
                accountClient.sendRequestAccountNumbersByLogins(logins);
        CompletableFuture<Map<String, String>> loginInnMapFuture =
                crmClient.sendRequestInnByLogins(logins);

        Map<String, String> loginAccountNumberMap = loginAccountNumberMapFuture.join();
        Map<String, String> loginInnMap = loginInnMapFuture.join();

        return customers.stream()
                .collect(Collectors.toMap(Customer::getId, customer -> {
                    return CustomerInfo.builder()
                            .id(customer.getId())
                            .email(customer.getEmail())
                            .accountNumber(loginAccountNumberMap.get(customer.getLogin()))
                            .inn(loginInnMap.get(customer.getLogin()))
                            .build();
                }));
    }
}
