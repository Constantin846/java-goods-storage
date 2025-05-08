package tk.project.goodsstorage.services.product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.ProductNotFoundException;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.image.ImageNotFoundException;
import tk.project.goodsstorage.models.product.Product;
import tk.project.goodsstorage.models.product.ProductImage;
import tk.project.goodsstorage.repositories.product.ProductImageRepository;
import tk.project.goodsstorage.repositories.product.ProductRepository;
import tk.project.goodsstorage.services.minio.MinioService;

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
    public String upload(final UUID productId, final MultipartFile file) {
        final Product product = getProductById(productId);

        ProductImage image = new ProductImage();
        image.setOriginalName(file.getOriginalFilename());
        image.setProductId(product.getId());
        image = imageRepository.save(image);

        minioService.uploadFile(file, image.getName().toString());
        return image.getName().toString();
    }

    @Override
    public void downloadImages(final UUID productId, final ZipOutputStream zipOutputStream) {
        final Product product = getProductById(productId);
        final List<ProductImage> images = imageRepository.findAllByProductId(product.getId());

        if (images.isEmpty()) {
            final String message = String.format("Image was not found by product id: %s", productId);
            log.warn(message);
            throw new ImageNotFoundException(message);
        }
        minioService.downloadFiles(images, zipOutputStream);
    }

    private Product getProductById(final UUID productId) {
        return productRepository.findById(productId).orElseThrow(() -> {
            final String message = String.format("Product was not found by id: %s", productId);
            log.warn(message);
            return new ProductNotFoundException(message);
        });
    }
}
