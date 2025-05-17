package tk.project.goodsstorage.interaction.crm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CrmClient {
    CompletableFuture<Map<String, String>> sendRequestInnByLogins(final List<String> logins);
}
