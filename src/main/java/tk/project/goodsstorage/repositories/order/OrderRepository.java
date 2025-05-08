package tk.project.goodsstorage.repositories.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tk.project.goodsstorage.enums.OrderStatus;
import tk.project.goodsstorage.models.order.Order;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query(value = """
            select o
            from Order o
            left join fetch o.customer
            left join fetch o.products op
            where o.id = :id
            """)
    Optional<Order> findByIdFetch(final UUID id);

    @Query(value = """
            select o
            from Order o
            left join fetch o.customer
            left join fetch o.products op
            where o.status in (:statuses)
            """)
    List<Order> findByOrderStatus(final Collection<OrderStatus> statuses);
}

