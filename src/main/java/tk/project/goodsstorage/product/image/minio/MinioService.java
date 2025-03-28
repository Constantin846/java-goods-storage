package tk.project.goodsstorage.product.image.minio;

import org.springframework.web.multipart.MultipartFile;
import tk.project.goodsstorage.product.image.Image;

import java.util.List;
import java.util.zip.ZipOutputStream;

public interface MinioService {
    void uploadImage(MultipartFile file, String fileName);

    void downloadImagesData(List<Image> images, ZipOutputStream zipOutputStream);
}
