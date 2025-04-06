package tk.project.goodsstorage.customer.info.accountnumber;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AccountNumberService {
    CompletableFuture<Map<String, String>> sendRequestAccountNumbersByLogins(List<String> logins);
}
