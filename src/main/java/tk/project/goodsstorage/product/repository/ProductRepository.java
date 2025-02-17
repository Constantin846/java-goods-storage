package tk.project.goodsstorage.product.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import tk.project.goodsstorage.product.Product;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Product findByArticle(String article);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Product> findAll();
}
