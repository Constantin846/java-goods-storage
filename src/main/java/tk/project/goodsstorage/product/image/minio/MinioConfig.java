package tk.project.goodsstorage.product.image.minio;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MinioConfig {

    private final String bucketName;
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String endpoint;

    public MinioConfig (
            @Value("${app.minio-service.bucket-name}")
            final String bucketName,
            @Value("${app.minio-service.access-key-id}")
            final String accessKeyId,
            @Value("${app.minio-service.secret-access-key}")
            final String secretAccessKey,
            @Value("${app.minio-service.endpoint}")
            final String endpoint
    ) {
        this.bucketName = bucketName;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.endpoint = endpoint;
    }
}
