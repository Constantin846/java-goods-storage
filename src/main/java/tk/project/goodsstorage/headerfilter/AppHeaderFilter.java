package tk.project.goodsstorage.headerfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tk.project.goodsstorage.enums.Currency;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AppHeaderFilter extends OncePerRequestFilter {
    private static final String X_CURRENCY = "X-Currency";
    private static final String X_ORCHESTRATOR_ID = "X_Orchestrator_ID";
    private static final String X_CUSTOMER_ID = "X-Customer-ID";
    private final CustomerIdWrapper customerIdWrapper;
    private final OrchestratorIdWrapper orchestratorIdWrapper;
    private final SessionCurrencyWrapper currencyWrapper;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        final String currencyHeader = request.getHeader(X_CURRENCY);
        if (Objects.nonNull(currencyHeader)) {
            currencyWrapper.setCurrency(Currency.valueOf(currencyHeader));
        }

        final String customerIdHeader = request.getHeader(X_CUSTOMER_ID);
        if (Objects.nonNull(customerIdHeader)) {
            customerIdWrapper.setCustomerId(Long.parseLong(customerIdHeader));
        }

        final String orchestratorIdHeader = request.getHeader(X_ORCHESTRATOR_ID);
        if (Objects.nonNull(orchestratorIdHeader)) {
            orchestratorIdWrapper.setOrchestratorId(orchestratorIdHeader);
        }

        filterChain.doFilter(request, response);
    }
}
