package tk.project.goodsstorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tk.project.goodsstorage.models.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByLogin(final String login);

    @Query(value = "select * from customer c where c.id = :id for update", nativeQuery = true)
    Optional<Customer> findByIdLocked(final Long id);
}
