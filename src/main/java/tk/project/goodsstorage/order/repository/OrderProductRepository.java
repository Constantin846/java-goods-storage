package tk.project.goodsstorage.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tk.project.goodsstorage.order.dto.find.FindOrderProductDto;
import tk.project.goodsstorage.order.dto.update.UpdateOrderProductDtoRes;
import tk.project.goodsstorage.order.model.OrderProduct;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    @Query(value = "select * from order_product op where op.order_id = :orderId for update", nativeQuery = true)
    Set<OrderProduct> findAllByOrderIdForUpdate(UUID orderId);

    @Query(value = """
            select new tk.project.goodsstorage.order.dto.update.UpdateOrderProductDtoRes(op.productId, op.count)
            from OrderProduct op
            where op.order.id = :orderId
            """)
    List<UpdateOrderProductDtoRes> findAllWithoutPriceByOrderId(UUID orderId);

    @Query(value = """
            select new tk.project.goodsstorage.order.dto.find.FindOrderProductDto(op.productId, p.name, op.price, op.count)
            from OrderProduct op
            join Product p on p.id = op.productId
            where op.order.id = :orderId
            """)
    List<FindOrderProductDto> findAllWithNameByOrderId(UUID orderId);
}
