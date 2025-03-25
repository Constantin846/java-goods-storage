package tk.project.goodsstorage.product.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tk.project.goodsstorage.product.image.service.ProductImageService;

import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductImageController {
    private static final String PRODUCT_ID_IMAGES_PATH = "/{productId}/images";
    private static final String PRODUCT_ID = "productId";
    private static final String URL = "url";
    private final ProductImageService productImageService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(PRODUCT_ID_IMAGES_PATH)
    public Map<String, String> addProductImage(@PathVariable(PRODUCT_ID) UUID productId,
                                               @RequestPart("image") MultipartFile file) {
        log.info("Add image to product with id: {}", productId);
        String url = productImageService.upload(productId, file);
        return Map.of(URL, url);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(PRODUCT_ID_IMAGES_PATH)
    public ResponseEntity<ZipOutputStream> findImagesByProductId(@PathVariable(PRODUCT_ID) UUID productId) {
        log.info("Find images by product id: {}", productId);
        ZipOutputStream zipOut = productImageService.downloadImages(productId);
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment;filename=export.zip")
                .header("Content-Type","application/octet-stream")
                .body(zipOut);
    }
}
