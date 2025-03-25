package tk.project.goodsstorage.product.image.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.project.exceptionhandler.goodsstorage.exceptions.product.ProductNotFoundException;
import tk.project.goodsstorage.product.image.Image;
import tk.project.goodsstorage.product.image.ProductImageRepository;
import tk.project.goodsstorage.product.image.s3client.S3Service;
import tk.project.goodsstorage.product.model.Product;
import tk.project.goodsstorage.product.repository.ProductRepository;

import java.util.List;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private static final String URL_UNDEFINED = "Url undefined";
    private final ProductImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final S3Service s3Service;

    @Transactional
    @Override
    public String upload(UUID productId, MultipartFile file) {
        Product product = getProductById(productId);

        Image image = new Image();
        image.setOriginalName(file.getOriginalFilename());
        image.setProductId(product.getId());
        image.setUrl(URL_UNDEFINED);
        image = imageRepository.save(image);

        String url = s3Service.uploadImage(file, image.getName().toString());
        image.setUrl(url);
        imageRepository.save(image);
        return url;
    }

    @Override
    public ZipOutputStream downloadImages(UUID productId) {
        List<Image> images = imageRepository.findAllByProductId(productId);
        return s3Service.downloadImagesData(images);
    }

    private Product getProductById(UUID productId) {
        return productRepository.findById(productId).orElseThrow(() -> {
            String message = String.format("Product was not found by id: %s", productId);
            log.warn(message);
            return new ProductNotFoundException(message);
        });
    }
}
