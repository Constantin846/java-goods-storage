package tk.project.goodsstorage.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tk.project.goodsstorage.BaseIntegrationTest;
import tk.project.goodsstorage.BaseWireMockTest;
import tk.project.goodsstorage.dto.CurrenciesDto;
import tk.project.goodsstorage.dto.product.create.CreateProductRequest;
import tk.project.goodsstorage.dto.product.find.ProductResponse;
import tk.project.goodsstorage.dto.product.update.UpdateProductRequest;
import tk.project.goodsstorage.dto.product.update.UpdateProductResponse;
import tk.project.goodsstorage.enums.CategoryType;
import tk.project.goodsstorage.enums.Currency;
import tk.project.goodsstorage.exceptionhandler.exceptions.ApiError;
import tk.project.goodsstorage.exceptionhandler.exceptions.product.ArticleExistsException;
import tk.project.goodsstorage.exceptionhandler.exceptions.product.ProductNotFoundException;
import tk.project.goodsstorage.models.product.Product;
import tk.project.goodsstorage.models.product.objectmother.ProductMother;
import tk.project.goodsstorage.services.currency.provider.CurrenciesProviderImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductIntegrationTest extends BaseIntegrationTest implements BaseWireMockTest {
    @Autowired
    private CurrenciesProviderImpl currenciesProviderImpl;

    @RegisterExtension
    private static final WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort().dynamicPort())
            .build();

    @DynamicPropertySource
    private static void setWireMockExtension(DynamicPropertyRegistry registry) {
        registry.add("currency-service.host", wireMockExtension::baseUrl);
    }

    @Override
    public WireMockExtension getWireMockExtension() {
        return wireMockExtension;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @AfterEach
    void clearCurrenciesCache() {
        currenciesProviderImpl.emptyCurrenciesCache();
    }

    @Test
    @SneakyThrows
    @DisplayName("Create product successfully")
    void createProduct() {
        // GIVEN
        final CreateProductRequest productRequest = CreateProductRequest.builder()
                .name("name")
                .article("article")
                .description("description")
                .price(BigDecimal.valueOf(111L))
                .count(10L)
                .build();

        // WHEN
        final String result = mockMvc.perform(post("/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertNotNull(result);
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to create product when product article exists")
    void createProductWithArticleExistsException() {
        // GIVEN
        final String article = "article";
        Product product = ProductMother.createDefaultProduct().withArticle(article).build();
        product = productRepository.save(product);

        final CreateProductRequest productRequest = CreateProductRequest.builder()
                .name("name")
                .article(article)
                .description("description")
                .price(BigDecimal.valueOf(111L))
                .count(10L)
                .build();

        // WHEN
        final String result = mockMvc.perform(post("/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(ArticleExistsException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(article));
        assertTrue(apiError.getMessage().contains(product.getId().toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Find product by id with currency RUS successfully")
    void findProductByIdWithCurrencyRUS() {
        // GIVEN
        Product product = ProductMother.createDefaultProduct()
                .withPrice(BigDecimal.valueOf(19987, 2))
                .build();
        product = productRepository.save(product);

        final ProductResponse expectedResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory().name())
                .price(product.getPrice())
                .count(product.getCount())
                .lastCountUpdateTime(product.getLastCountUpdateTime())
                .createDate(product.getCreateDate())
                .isAvailable(product.getIsAvailable())
                .currency(Currency.RUS.name())
                .build();

        // WHEN
        final String result = mockMvc.perform(get(String.format("/products/%s", product.getId()))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertEquals(objectMapper.writeValueAsString(expectedResponse), result);
    }

    @Test
    @SneakyThrows
    @DisplayName("Find product by id with currency is not RUS successfully when currency service responses")
    void findProductByIdWithCurrencyIsNotRUSWhenCurrencyServiceResponses() {
        // GIVEN
        Product product = ProductMother.createDefaultProduct()
                .withPrice(BigDecimal.valueOf(18500, 2)).
                build();
        product = productRepository.save(product);

        final Currency expectedCurrency = Currency.EUR;

        final CurrenciesDto expectedCurrenciesDto = CurrenciesDto.builder()
                .cny(BigDecimal.valueOf(50L))
                .eur(BigDecimal.valueOf(100L))
                .usd(BigDecimal.valueOf(98L))
                .build();

        final ProductResponse expectedResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory().name())
                .price(product.getPrice().divide(
                        switch (expectedCurrency) {
                            case CNY -> expectedCurrenciesDto.getCny();
                            case EUR -> expectedCurrenciesDto.getEur();
                            case USD -> expectedCurrenciesDto.getUsd();
                            case RUS -> BigDecimal.ONE;
                        },
                        RoundingMode.HALF_EVEN))
                .count(product.getCount())
                .lastCountUpdateTime(product.getLastCountUpdateTime())
                .createDate(product.getCreateDate())
                .isAvailable(product.getIsAvailable())
                .currency(expectedCurrency.name())
                .build();

        addWireMockExtensionStubGet("/app-currency/v1/currencies", expectedCurrenciesDto);

        // WHEN
        final String result = mockMvc.perform(get(String.format("/products/%s", product.getId()))
                        .header("X-Currency", expectedCurrency)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertEquals(objectMapper.writeValueAsString(expectedResponse), result);
    }

    @Test
    @SneakyThrows
    @DisplayName("Find product by id with currency is not RUS successfully when currencies is taken from file")
    void findProductByIdWithCurrencyIsNotRUSWhenCurrenciesIsTakenFromFile() {
        // GIVEN
        Product product = ProductMother.createDefaultProduct()
                .withPrice(BigDecimal.valueOf(18500, 2)).
                build();
        product = productRepository.save(product);

        final Currency expectedCurrency = Currency.USD;

        final String content = Files.readString(Path.of("src/main/resources/app-info/exchange-rate.json"));
        final CurrenciesDto expectedCurrenciesDto = objectMapper.readValue(content, CurrenciesDto.class);

        final ProductResponse expectedResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory().name())
                .price(product.getPrice().divide(
                        switch (expectedCurrency) {
                            case CNY -> expectedCurrenciesDto.getCny();
                            case EUR -> expectedCurrenciesDto.getEur();
                            case USD -> expectedCurrenciesDto.getUsd();
                            case RUS -> BigDecimal.ONE;
                        },
                        RoundingMode.HALF_EVEN))
                .count(product.getCount())
                .lastCountUpdateTime(product.getLastCountUpdateTime())
                .createDate(product.getCreateDate())
                .isAvailable(product.getIsAvailable())
                .currency(expectedCurrency.name())
                .build();

        // WHEN
        final String result = mockMvc.perform(get(String.format("/products/%s", product.getId()))
                        .header("X-Currency", expectedCurrency)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertEquals(objectMapper.writeValueAsString(expectedResponse), result);
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to find product by id when product was not found")
    void findProductByIdWithProductNotFoundException() {
        // GIVEN
        final UUID productId = UUID.randomUUID();

        // WHEN
        final String result = mockMvc.perform(get(String.format("/products/%s", productId))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(ProductNotFoundException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(productId.toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Find all products by id with currency is not RUS successfully when currency service responses")
    void findAllProductsByIdWithCurrencyIsNotRUSWhenCurrencyServiceResponses() {
        // GIVEN
        final Currency expectedCurrency = Currency.EUR;

        Product firstProduct = ProductMother.createDefaultProduct()
                .withPrice(BigDecimal.valueOf(18500, 2)).
                build();
        firstProduct = productRepository.save(firstProduct);
        Product secondProduct = ProductMother.createDefaultProduct()
                .withPrice(BigDecimal.valueOf(215, 2)).
                build();
        secondProduct = productRepository.save(secondProduct);

        final UUID firstProductId = firstProduct.getId();
        final UUID secondProductId = secondProduct.getId();

        final CurrenciesDto expectedCurrenciesDto = CurrenciesDto.builder()
                .cny(BigDecimal.valueOf(50L))
                .eur(BigDecimal.valueOf(100L))
                .usd(BigDecimal.valueOf(98L))
                .build();

        final BigDecimal expectedFirstProductPrice = firstProduct.getPrice().divide(
                switch (expectedCurrency) {
                    case CNY -> expectedCurrenciesDto.getCny();
                    case EUR -> expectedCurrenciesDto.getEur();
                    case USD -> expectedCurrenciesDto.getUsd();
                    case RUS -> BigDecimal.ONE;
                }, RoundingMode.HALF_EVEN);

        final BigDecimal expectedSecondProductPrice = secondProduct.getPrice().divide(
                switch (expectedCurrency) {
                    case CNY -> expectedCurrenciesDto.getCny();
                    case EUR -> expectedCurrenciesDto.getEur();
                    case USD -> expectedCurrenciesDto.getUsd();
                    case RUS -> BigDecimal.ONE;
                }, RoundingMode.HALF_EVEN);

        addWireMockExtensionStubGet("/app-currency/v1/currencies", expectedCurrenciesDto);

        // WHEN
        final String result = mockMvc.perform(get("/products")
                        .header("X-Currency", expectedCurrency)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final List<ProductResponse> actualProductsResponses = objectMapper
                .readValue(result, new TypeReference<List<ProductResponse>>(){});

        // THEN
        assertThat(actualProductsResponses)
                .anySatisfy(actualProductResponse -> {
                    assertEquals(firstProductId, actualProductResponse.getId());
                    assertEquals(expectedFirstProductPrice, actualProductResponse.getPrice());
                })
                .anySatisfy(actualProductResponse -> {
                    assertEquals(secondProductId, actualProductResponse.getId());
                    assertEquals(expectedSecondProductPrice, actualProductResponse.getPrice());
                })
                .allSatisfy(actualProductResponse -> {
                    assertEquals(expectedCurrency.name(), actualProductResponse.getCurrency());
                })
                .hasSize(2);
    }

    @Test
    @SneakyThrows
    @DisplayName("Update product without count change successfully")
    void updateProductWithoutCountChange() {
        // GIVEN
        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        final UpdateProductRequest productRequest = UpdateProductRequest.builder()
                .name("new " + product.getName())
                .article("new " + product.getArticle())
                .description("new " + product.getDescription())
                .category(CategoryType.MEAT.name())
                .price(product.getPrice().add(BigDecimal.valueOf(111L)))
                .isAvailable(!product.getIsAvailable())
                .build();

        final UpdateProductResponse expectedResponse = UpdateProductResponse.builder()
                .id(product.getId())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .category(productRequest.getCategory())
                .price(productRequest.getPrice())
                .count(product.getCount())
                .lastCountUpdateTime(product.getLastCountUpdateTime())
                .createDate(product.getCreateDate())
                .isAvailable(productRequest.getIsAvailable())
                .build();

        // WHEN
        final String result = mockMvc.perform(patch(String.format("/products/%s", product.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertEquals(objectMapper.writeValueAsString(expectedResponse), result);
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed to update product when product was not found")
    void updateProductWithProductNotFoundException() {
        // GIVEN
        final UUID productId = UUID.randomUUID();

        final UpdateProductRequest productRequest = UpdateProductRequest.builder()
                .name("new")
                .article("new")
                .description("new")
                .category(CategoryType.MEAT.name())
                .price(BigDecimal.valueOf(111L))
                .isAvailable(false)
                .build();

        // WHEN
        final String result = mockMvc.perform(patch(String.format("/products/%s", productId))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(ProductNotFoundException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(productId.toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Update product count successfully")
    void updateProductCount() {
        // GIVEN
        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        final Instant unexpectedLastCountUpdateTime = product.getLastCountUpdateTime();
        final Long expectedCount = product.getCount() + 12L;

        final UpdateProductRequest productRequest = UpdateProductRequest.builder()
                .count(expectedCount)
                .build();

        // WHEN
        final String result = mockMvc.perform(patch(String.format("/products/%s", product.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final UpdateProductResponse actualResponse = objectMapper.readValue(result, UpdateProductResponse.class);

        // THEN
        assertEquals(expectedCount, actualResponse.getCount());
        assertNotEquals(unexpectedLastCountUpdateTime, actualResponse.getLastCountUpdateTime());
    }

    @Test
    @SneakyThrows
    @DisplayName("Delete product by id successfully")
    void deleteProductById() {
        // GIVEN
        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        // WHEN
        mockMvc.perform(delete(String.format("/products/%s", product.getId()))
                        .contentType("application/json"))
                .andDo(print())
                // THEN
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}