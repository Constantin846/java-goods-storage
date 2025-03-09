package tk.project.goodsstorage.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tk.project.goodsstorage.order.model.Order;

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
    Optional<Order> findByIdFetch(UUID id);
}
