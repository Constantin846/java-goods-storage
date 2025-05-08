package tk.project.goodsstorage.services.customer;

import tk.project.goodsstorage.dto.customer.find.CustomerInfo;
import tk.project.goodsstorage.models.Customer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface CustomerInfoProvider {
    Map<Long, CustomerInfo> getIdCustomerInfoMap(final Set<Customer> customers);

    CompletableFuture<Map<String, String>> getLoginAccountNumberMap(final List<String> logins);

    CompletableFuture<Map<String, String>> getLoginInn(final List<String> logins);
}
