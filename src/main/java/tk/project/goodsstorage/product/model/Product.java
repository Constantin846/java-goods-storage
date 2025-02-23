package tk.project.goodsstorage.product.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    //@UuidGenerator
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    UUID id;

    @Column(name = "name", nullable = false, length = 64)
    String name;

    @Column(name = "article", nullable = false, length = 64, unique = true)
    String article;

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "category", nullable = false, length = 64)
    @Enumerated(EnumType.STRING)
    CategoryType category;

    @Column(name = "price", nullable = false)
    BigDecimal price;

    @Column(name = "count", nullable = false)
    Long count;

    @CreationTimestamp
    @Column(name = "last_count_update_time", nullable = false)
    Instant lastCountUpdateTime;

    @CreationTimestamp
    @Column(name = "create_date", nullable = false)
    LocalDate createDate;
}
