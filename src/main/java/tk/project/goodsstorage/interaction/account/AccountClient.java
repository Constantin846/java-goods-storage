package tk.project.goodsstorage.interaction.account;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AccountClient {
    CompletableFuture<Map<String, String>> sendRequestAccountNumbersByLogins(final List<String> logins);
}
