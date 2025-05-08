package tk.project.goodsstorage.product.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductImageRepository extends JpaRepository<Image, UUID> {

    List<Image> findAllByProductId(final UUID productId);
}
