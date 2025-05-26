package tk.project.goodsstorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@WireMockTest
public interface BaseWireMockTest {

    WireMockExtension getWireMockExtension();

    ObjectMapper getObjectMapper();

    @SneakyThrows
    default void addWireMockExtensionStubPost(final String url, final Object responseBody) {
        addWireMockExtensionStubPost(HttpStatus.OK, url, responseBody);
    }

    @SneakyThrows
    default void addWireMockExtensionStubPost(final HttpStatus status, final String url, final Object responseBody) {
        getWireMockExtension().stubFor(
                WireMock.post(url)
                        .willReturn(aResponse()
                                .withStatus(status.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                                .withBody(getObjectMapper().writeValueAsString(responseBody)))
        );
    }

    @SneakyThrows
    default void addWireMockExtensionStubGet(final String url, final Object responseBody) {
        addWireMockExtensionStubGet(HttpStatus.OK, url, responseBody);
    }

    @SneakyThrows
    default void addWireMockExtensionStubGet(final HttpStatus status, final String url, final Object responseBody) {
        getWireMockExtension().stubFor(
                WireMock.get(url)
                        .willReturn(aResponse()
                                .withStatus(status.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                                .withBody(getObjectMapper().writeValueAsString(responseBody)))
        );
    }
}
