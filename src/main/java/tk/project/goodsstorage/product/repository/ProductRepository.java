package tk.project.goodsstorage.product.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import tk.project.goodsstorage.product.model.Product;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByArticle(String article);

    @Query(value = "select * from product p where p.id = :id for update", nativeQuery = true)
    Optional<Product> findByIdLocked(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Product> findAll();

    @Query(value = "select * from product p where p.id in :ids for update", nativeQuery = true)
    Set<Product> findAllByIdsForUpdate(Collection<UUID> ids);

    default Map<UUID, Product> findMapByIdsForUpdate(Collection<UUID> ids) {
        return this.findAllByIdsForUpdate(ids).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }
}
