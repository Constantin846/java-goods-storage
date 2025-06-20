package tk.project.goodsstorage.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfiguration {
    private static final int TIMEOUT = 1000;

    @Bean
    public WebClient currencyWebClient(@Value("${currency-service.host}") final String baseUrl,
                                       @Value("${currency-service.timeout}") Integer timeout) {
        timeout = Objects.isNull(timeout) ? TIMEOUT : timeout;
        return webClientWithTimeout(baseUrl, timeout);
    }

    @Bean
    public WebClient accountNumberWebClient(@Value("${account-service.host}") final String baseUrl,
                                            @Value("${account-service.timeout}") Integer timeout) {
        timeout = Objects.isNull(timeout) ? TIMEOUT : timeout;
        return webClientWithTimeout(baseUrl, timeout);
    }

    @Bean
    public WebClient innWebClient(@Value("${crm-service.host}") final String baseUrl,
                                  @Value("${crm-service.timeout}") Integer timeout) {
        timeout = Objects.isNull(timeout) ? TIMEOUT : timeout;
        return webClientWithTimeout(baseUrl, timeout);
    }

    @Bean
    public WebClient orchestratorWebClient(@Value("${orchestrator-goods-storage.host}") final String baseUrl,
                                           @Value("${orchestrator-goods-storage.timeout}") Integer timeout) {
        timeout = Objects.isNull(timeout) ? TIMEOUT : timeout;
        return webClientWithTimeout(baseUrl, timeout);
    }

    private WebClient webClientWithTimeout(final String baseUrl, final Integer timeout) {
        final var tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS));
                });

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }
}
