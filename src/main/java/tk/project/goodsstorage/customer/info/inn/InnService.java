package tk.project.goodsstorage.customer.info.inn;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface InnService {
    CompletableFuture<Map<String, String>> sendRequestInnByLogins(final List<String> logins);
}
