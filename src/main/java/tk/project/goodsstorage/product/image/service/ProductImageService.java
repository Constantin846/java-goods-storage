package tk.project.goodsstorage.product.image.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.zip.ZipOutputStream;

public interface ProductImageService {
    String upload(UUID productId, MultipartFile file);

    void downloadImages(UUID productId, ZipOutputStream zipOutputStream);
}
