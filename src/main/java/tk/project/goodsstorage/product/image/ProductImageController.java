package tk.project.goodsstorage.product.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import tk.project.goodsstorage.product.image.service.ProductImageService;

import java.io.IOException;
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
    private static final String FILE_NAME = "fileName";
    private final ProductImageService productImageService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(PRODUCT_ID_IMAGES_PATH)
    public Map<String, String> addProductImage(@PathVariable(PRODUCT_ID) final UUID productId,
                                               @RequestPart("image") final MultipartFile file) {
        log.info("Add image to product with id: {}", productId);
        final String fileName = productImageService.upload(productId, file);
        return Map.of(FILE_NAME, fileName);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = PRODUCT_ID_IMAGES_PATH, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> findImagesByProductId(
            @PathVariable(PRODUCT_ID) final UUID productId) throws IOException
    {
        log.info("Find images by product id: {}", productId);

        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=\"files.zip\"")
                .body(out -> {
                    final ZipOutputStream zipOutputStream = new ZipOutputStream(out);
                    productImageService.downloadImages(productId, zipOutputStream);
                    zipOutputStream.close();
                });
    }
}
