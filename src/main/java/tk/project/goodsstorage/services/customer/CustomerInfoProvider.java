package tk.project.goodsstorage.services.customer;

import tk.project.goodsstorage.dto.customer.find.CustomerInfo;
import tk.project.goodsstorage.models.Customer;

import java.util.Map;
import java.util.Set;

public interface CustomerInfoProvider {
    Map<Long, CustomerInfo> getIdCustomerInfoMap(final Set<Customer> customers);
}
