package tk.project.goodsstorage.repositories.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tk.project.goodsstorage.dto.order.find.FindOrderedProductDto;
import tk.project.goodsstorage.models.order.OrderedProduct;
import tk.project.goodsstorage.models.order.OrderedProductPK;

import java.util.List;
import java.util.UUID;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, OrderedProductPK> {

    @Query(value = """
            select new tk.project.goodsstorage.dto.order.find.FindOrderedProductDto(op.productId, p.name, op.price, op.count)
            from OrderedProduct op
            join Product p on p.id = op.productId
            where op.order.id = :orderId
            """)
    List<FindOrderedProductDto> findAllWithNameByOrderId(final UUID orderId);
}
