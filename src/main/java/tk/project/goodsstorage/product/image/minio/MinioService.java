package tk.project.goodsstorage.product.image.minio;

import org.springframework.web.multipart.MultipartFile;
import tk.project.goodsstorage.product.image.Image;

import java.util.List;
import java.util.zip.ZipOutputStream;

public interface MinioService {
    void uploadImage(final MultipartFile file, final String fileName);

    void downloadImagesData(final List<Image> images, final ZipOutputStream zipOutputStream);
}
