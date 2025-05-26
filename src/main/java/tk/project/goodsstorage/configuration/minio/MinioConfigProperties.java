package tk.project.goodsstorage.configuration.minio;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.minio-service")
public class MinioConfigProperties {

    private final String bucketName;

    private final String accessKeyId;

    private final String secretAccessKey;

    private final String endpoint;
}
