package tk.project.goodsstorage.services.accountnumber;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AccountNumberClient {
    CompletableFuture<Map<String, String>> sendRequestAccountNumbersByLogins(final List<String> logins);
}
