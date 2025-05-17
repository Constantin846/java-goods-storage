package tk.project.goodsstorage;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public interface MinioTestContainer {

    @Container
    MinIOContainer MINIO_CONTAINER = new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z");

    @DynamicPropertySource
    static void registerMinioConfigProperties(DynamicPropertyRegistry registry) {
        registry.add("app.minio-service.bucket-name", MINIO_CONTAINER::getUserName);
        registry.add("app.minio-service.access-key-id", MINIO_CONTAINER::getUserName);
        registry.add("app.minio-service.secret-access-key", MINIO_CONTAINER::getPassword);
        registry.add("app.minio-service.endpoint", MINIO_CONTAINER::getS3URL);
    }
}
