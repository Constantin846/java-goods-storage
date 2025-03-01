package tk.project.goodsstorage.currency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CurrencyHeaderFilter extends OncePerRequestFilter {
    private static final String X_CURRENCY = "X-currency";
    private final SessionCurrencyWrapper currencyWrapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(X_CURRENCY);

        if (Objects.nonNull(header)) {
            currencyWrapper.setCurrency(Currency.valueOf(header));
        }
        filterChain.doFilter(request, response);
    }
}
