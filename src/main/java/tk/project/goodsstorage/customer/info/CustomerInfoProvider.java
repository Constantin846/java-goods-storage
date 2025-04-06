package tk.project.goodsstorage.customer.info;

import tk.project.goodsstorage.customer.Customer;
import tk.project.goodsstorage.customer.dto.find.CustomerInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface CustomerInfoProvider {
    Map<Long, CustomerInfo> getIdCustomerInfoMap(Set<Customer> customers);

    CompletableFuture<Map<String, String>> getLoginAccountNumberMap(List<String> logins);

    CompletableFuture<Map<String, String>> getLoginInn(List<String> logins);
}
