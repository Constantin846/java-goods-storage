package tk.project.goodsstorage.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import tk.project.goodsstorage.BaseIntegrationTest;
import tk.project.goodsstorage.MinioTestContainer;
import tk.project.goodsstorage.exceptionhandler.exceptions.ApiError;
import tk.project.goodsstorage.exceptionhandler.exceptions.product.ProductNotFoundException;
import tk.project.goodsstorage.models.product.Product;
import tk.project.goodsstorage.models.product.ProductImage;
import tk.project.goodsstorage.models.product.objectmother.ProductMother;
import tk.project.goodsstorage.services.minio.MinioServiceImpl;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductImageIntegrationTest extends BaseIntegrationTest implements MinioTestContainer {
    @Value("${app.minio-service.bucket-name}")
    private String bucketName;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioServiceImpl minioService;

    @Test
    @SneakyThrows
    @DisplayName("Add product image successfully")
    void addProductImage() {
        // GIVEN
        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        final String expectedFileOriginalName = "file name.png";
        final String expectedFileContent = "file content";

        final MockMultipartFile file = new MockMultipartFile(
                "image",
                expectedFileOriginalName,
                MediaType.IMAGE_PNG_VALUE,
                expectedFileContent.getBytes());

        // WHEN
        final String result = mockMvc.perform(multipart(String.format("/products/%s/images", product.getId()))
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final Map<String, UUID> actualResponses = objectMapper
                .readValue(result, new TypeReference<Map<String, UUID>>() {
                });

        // THEN
        assertTrue(actualResponses.containsKey("fileName"));
        ProductImage savedProductImage = productImageRepository.findById(actualResponses.get("fileName")).get();
        assertEquals(expectedFileOriginalName, savedProductImage.getOriginalName());

        final String savedFileContent = new String(
                minioClient.getObject(GetObjectArgs
                                .builder()
                                .bucket(bucketName)
                                .object(savedProductImage.getName().toString())
                                .build())
                        .readAllBytes());
        assertEquals(expectedFileContent, savedFileContent);
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed add product image when product was not found")
    void addProductImageWithProductNotFoundException() {
        // GIVEN
        final UUID productId = UUID.randomUUID();

        final String fileOriginalName = "file name.png";
        final byte[] fileContent = "file content".getBytes();

        final MockMultipartFile file = new MockMultipartFile(
                "image",
                fileOriginalName,
                MediaType.IMAGE_PNG_VALUE,
                fileContent);

        // WHEN
        final String result = mockMvc.perform(multipart(String.format("/products/%s/images", productId))
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
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
    @DisplayName("Find product images successfully")
    void findImagesByProductId() {
        // GIVEN
        Product product = ProductMother.createDefaultProduct().build();
        product = productRepository.save(product);

        final String expectedFileOriginalName = "file name.png";
        final String expectedFileContent = "file content";

        final MockMultipartFile file = new MockMultipartFile(
                "image",
                expectedFileOriginalName,
                MediaType.IMAGE_PNG_VALUE,
                expectedFileContent.getBytes());

        ProductImage productImage = new ProductImage();
        productImage.setProductId(product.getId());
        productImage.setOriginalName(expectedFileOriginalName);
        productImage = productImageRepository.save(productImage);

        minioService.uploadFile(file, productImage.getName().toString());

        // WHEN
        final byte[] result = mockMvc.perform(get(String.format("/products/%s/images", product.getId()))
                        .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        String actualFileOriginalName = null;
        String actualFileContent = null;

        try (final ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(result))) {
            ZipEntry entry;

            while (Objects.nonNull(entry = zipInputStream.getNextEntry())) {
                actualFileOriginalName = entry.getName();
                actualFileContent = new String(zipInputStream.readAllBytes());

                zipInputStream.closeEntry();
            }
        }

        // THEN
        assertEquals(expectedFileOriginalName, actualFileOriginalName);
        assertEquals(expectedFileContent, actualFileContent);
    }
}
