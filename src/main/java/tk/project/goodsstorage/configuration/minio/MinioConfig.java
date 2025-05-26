package tk.project.goodsstorage.configuration.minio;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MinioConfigProperties.class)
public class MinioConfig {
    private final MinioConfigProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKeyId(), minioProperties.getSecretAccessKey())
                .build();
    }
}
