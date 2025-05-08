package tk.project.goodsstorage.product.image.minio;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.image.MinioDownloadImageException;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.image.MinioUploadImageException;
import tk.project.goodsstorage.product.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    private static final Integer FIVE_MB = 5_242_880;
    private final MinioConfig minioConfig;
    private String bucketName;
    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        bucketName = minioConfig.getBucketName();

        try {
            minioClient = MinioClient.builder()
                    .endpoint(minioConfig.getEndpoint())
                    .credentials(minioConfig.getAccessKeyId(), minioConfig.getSecretAccessKey())
                    .build();

            if (!minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName).build())
            ) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build());
            }
            log.info("MinioServiceImpl initialized");

        } catch (Exception e) {
            log.warn("Error creating minio client");
        }
    }

    @Override
    public void uploadImage(final MultipartFile file, final String fileName) {
        try (final InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, file.getSize(), FIVE_MB)
                    .build());

        } catch (Exception e) {
            final String msg = "Error sending request to add a file to minio";
            log.warn(msg);
            throw new MinioUploadImageException(msg, e);
        }
    }

    public void downloadImagesData(final List<Image> images, final ZipOutputStream zipOutputStream) {
        InputStream content = null;

        try {
            for (final Image image : images) {
                content = minioClient.getObject(GetObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(image.getName().toString())
                        .build());

                zipOutputStream.putNextEntry(new ZipEntry(image.getOriginalName()));
                IOUtils.copy(content, zipOutputStream);

                content.close();
                zipOutputStream.closeEntry();
            }
        } catch (Exception e) {
            final String msg = "Error sending request to get files from minio";
            log.warn(msg);
            throw new MinioDownloadImageException(msg, e);

        } finally {
            if (content != null) {
                try {
                    content.close();
                } catch (IOException e) {
                    log.warn("Error closing stream of files from minio");
                }
            }
        }
    }
}
