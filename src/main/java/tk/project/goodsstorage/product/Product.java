package tk.project.goodsstorage.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "article", nullable = false, unique = true)
    String article;

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "category", nullable = false)
    String category;

    @Column(name = "price", nullable = false)
    Double price;

    @Column(name = "count", nullable = false)
    Long count;

    @Column(name = "last_count_update_time", nullable = false)
    Instant lastCountUpdateTime;

    @Column(name = "create_date", nullable = false)
    LocalDate createDate;
}
