package tk.project.goodsstorage.product.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import tk.project.goodsstorage.product.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByArticle(String article);

    @Query(value = "select * from product p where p.id = :id for update", nativeQuery = true)
    Optional<Product> findByIdLocked(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Product> findAll();
}
