package tk.project.goodsstorage.models.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import tk.project.goodsstorage.services.minio.MinioFile;

import java.util.UUID;

@Entity
@Table(name = "image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImage implements MinioFile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "name", nullable = false, updatable = false)
    UUID name;

    @Column(name = "product_id", nullable = false)
    UUID productId;

    @Column(name = "original_name", nullable = false, length = 64)
    String originalName;
}
