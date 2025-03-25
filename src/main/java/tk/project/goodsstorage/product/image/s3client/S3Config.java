package tk.project.goodsstorage.product.image.s3client;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class S3Config {

    final String bucketName;
    final String accessKeyId;
    final String secretAccessKey;

    public S3Config(
            @Value("${app.s3-service.bucket-name}")
            String bucketName,
            @Value("${app.s3-service.access-key-id}")
            String accessKeyId,
            @Value("${app.s3-service.secret-access-key}")
            String secretAccessKey
    ) {
        this.bucketName = bucketName;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
    }
}
