package tk.project.goodsstorage.product.image.s3client;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.project.goodsstorage.product.image.Image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final S3Config s3Config;
    private String bucketName;
    private AmazonS3 s3Client;

    @PostConstruct
    public void init() {
        try {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(
                            new com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration(
                                    "https://localhost:9191",
                                    "ru-central1"
                            )
                    )
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(s3Config.getAccessKeyId(), s3Config.getSecretAccessKey())))
                    .build();
        } catch (SdkClientException e) {
            String msg = "Error creating s3 client";
            log.warn(msg);
            throw new RuntimeException(msg); // todo
        }
        bucketName = s3Config.getBucketName();
        log.info("S3ServiceImpl initialized");
    }

    @Override
    public String uploadImage(MultipartFile file, String fileName) {
        try {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(
                            new com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration(
                                    "https://localhost:9191",
                                    "ru-central1"
                            )
                    )
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(s3Config.getAccessKeyId(), s3Config.getSecretAccessKey())))
                    .build();
        } catch (SdkClientException e) {
            String msg = "Error creating s3 client";
            log.warn(msg);
            throw new RuntimeException(msg); // todo
        }


        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            String msg = "Error converting file to byte array";
            log.warn(msg);
            throw new RuntimeException(e); // todo
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileBytes.length);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
        s3Client.putObject(bucketName, fileName, inputStream, metadata);
        log.info("File {} was added to bucket {} successful", fileName, bucketName);

        return s3Client.getUrl(bucketName, fileName).toExternalForm();
    }

    private static final Integer FIVE_KB = 5120;

    public ZipOutputStream downloadImagesData(List<Image> images) {
        byte[] bytes = new byte[FIVE_KB];
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)
        ) {
            for (Image image : images) {
                S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, image.getName().toString()));
                S3ObjectInputStream content = s3Object.getObjectContent();

                zipOutputStream.putNextEntry(new ZipEntry(image.getOriginalName()));
                int bytesRead = -1;

                while ((bytesRead = content.read(bytes)) != -1) {
                    zipOutputStream.write(bytes, 0, bytesRead);
                }
                content.close();
            }
            return zipOutputStream;
        } catch (IOException e) {
            throw new RuntimeException(e); // todo
        }
    }
}
