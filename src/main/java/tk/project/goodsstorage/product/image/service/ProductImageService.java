package tk.project.goodsstorage.product.image.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.zip.ZipOutputStream;

public interface ProductImageService {
    String upload(final UUID productId, final MultipartFile file);

    void downloadImages(final UUID productId, final ZipOutputStream zipOutputStream);
}
