package tk.project.goodsstorage.product.image.s3client;

import org.springframework.web.multipart.MultipartFile;
import tk.project.goodsstorage.product.image.Image;

import java.util.List;
import java.util.zip.ZipOutputStream;

public interface S3Service {
    String uploadImage(MultipartFile file, String fileName);

    ZipOutputStream downloadImagesData(List<Image> images);
}
