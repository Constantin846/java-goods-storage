package tk.project.goodsstorage.product.image.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.ProductNotFoundException;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.image.ImageNotFoundException;
import tk.project.goodsstorage.product.image.Image;
import tk.project.goodsstorage.product.image.ProductImageRepository;
import tk.project.goodsstorage.product.image.minio.MinioService;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductRepository;

import java.util.List;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final MinioService minioService;

    @Transactional
    @Override
    public String upload(UUID productId, MultipartFile file) {
        Product product = getProductById(productId);

        Image image = new Image();
        image.setOriginalName(file.getOriginalFilename());
        image.setProductId(product.getId());
        image = imageRepository.save(image);

        minioService.uploadImage(file, image.getName().toString());
        return image.getName().toString();
    }

    @Override
    public void downloadImages(UUID productId, ZipOutputStream zipOutputStream) {
        Product product = getProductById(productId);
        List<Image> images = imageRepository.findAllByProductId(product.getId());

        if (images.isEmpty()) {
            String message = String.format("Image was not found by product id: %s", productId);
            log.warn(message);
            throw new ImageNotFoundException(message);
        }
        minioService.downloadImagesData(images, zipOutputStream);
    }

    private Product getProductById(UUID productId) {
        return productRepository.findById(productId).orElseThrow(() -> {
            String message = String.format("Product was not found by id: %s", productId);
            log.warn(message);
            return new ProductNotFoundException(message);
        });
    }
}
