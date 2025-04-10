package tk.project.goodsstorage.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@IdClass(OrderedProductPK.class)
@Table(name = "ordered_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"order", "productId"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderedProduct {
    @Id
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @Id
    @Column(name = "product_id", nullable = false)
    UUID productId;

    @Column(name = "product_price", nullable = false)
    BigDecimal price;

    @Column(name = "product_count", nullable = false)
    Long count;
}
