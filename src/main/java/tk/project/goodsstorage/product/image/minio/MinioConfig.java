package tk.project.goodsstorage.product.image.minio;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinioConfig {

    final String bucketName;
    final String accessKeyId;
    final String secretAccessKey;
    final String endpoint;

    public MinioConfig (
            @Value("${app.minio-service.bucket-name}")
            String bucketName,
            @Value("${app.minio-service.access-key-id}")
            String accessKeyId,
            @Value("${app.minio-service.secret-access-key}")
            String secretAccessKey,
            @Value("${app.minio-service.endpoint}")
            String endpoint
    ) {
        this.bucketName = bucketName;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.endpoint = endpoint;
    }
}
