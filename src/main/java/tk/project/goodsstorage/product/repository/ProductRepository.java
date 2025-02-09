package tk.project.goodsstorage.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tk.project.goodsstorage.product.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
