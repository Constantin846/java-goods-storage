package tk.project.goodsstorage.customer.info;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tk.project.goodsstorage.customer.Customer;
import tk.project.goodsstorage.customer.dto.find.CustomerInfo;
import tk.project.goodsstorage.customer.info.accountnumber.AccountNumberService;
import tk.project.goodsstorage.customer.info.inn.InnService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerInfoProviderImpl implements CustomerInfoProvider {
    private final AccountNumberService accountNumberService;
    private final InnService innService;

    @Override
    public Map<Long, CustomerInfo> getIdCustomerInfoMap(final Set<Customer> customers) {
        List<String> logins = customers.stream()
                .map(Customer::getLogin)
                .toList();

        CompletableFuture<Map<String, String>> loginAccountNumberMapFuture = this.getLoginAccountNumberMap(logins);
        CompletableFuture<Map<String, String>> loginInnMapFuture = this.getLoginInn(logins);
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

    @Override
    public CompletableFuture<Map<String, String>> getLoginAccountNumberMap(final List<String> logins) {
        return accountNumberService.sendRequestAccountNumbersByLogins(logins);
    }

    @Override
    public CompletableFuture<Map<String, String>> getLoginInn(final List<String> logins) {
        return innService.sendRequestInnByLogins(logins);
    }
}
