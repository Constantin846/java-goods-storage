package tk.project.goodsstorage.headerfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tk.project.goodsstorage.currency.Currency;
import tk.project.goodsstorage.currency.SessionCurrencyWrapper;
import tk.project.goodsstorage.customer.CustomerIdWrapper;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AppHeaderFilter extends OncePerRequestFilter {
    private static final String X_CURRENCY = "X-Currency";
    private static final String X_CUSTOMER_ID = "X-Customer-ID";
    private final CustomerIdWrapper customerIdWrapper;
    private final SessionCurrencyWrapper currencyWrapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String currencyHeader = request.getHeader(X_CURRENCY);
        if (Objects.nonNull(currencyHeader)) {
            currencyWrapper.setCurrency(Currency.valueOf(currencyHeader));
        }

        String customerIdHeader = request.getHeader(X_CUSTOMER_ID);
        if (Objects.nonNull(customerIdHeader)) {
            customerIdWrapper.setCustomerId(Long.parseLong(customerIdHeader));
        }

        filterChain.doFilter(request, response);
    }
}
