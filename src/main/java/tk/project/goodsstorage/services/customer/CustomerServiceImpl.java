package tk.project.goodsstorage.services.customer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.dto.customer.create.CreateCustomerDto;
import tk.project.goodsstorage.dto.customer.find.FindCustomerDto;
import tk.project.goodsstorage.dto.customer.update.UpdateCustomerDto;
import tk.project.goodsstorage.exceptionhandler.exceptions.customer.CustomerNotFoundException;
import tk.project.goodsstorage.exceptionhandler.exceptions.customer.LoginExistsException;
import tk.project.goodsstorage.mappers.CustomerDtoMapper;
import tk.project.goodsstorage.models.Customer;
import tk.project.goodsstorage.repositories.CustomerRepository;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private static final String CUSTOMER_WAS_NOT_FOUND_BY_ID = "Customer was not found by id: %s";
    private final CustomerDtoMapper mapper;
    private final CustomerRepository customerRepository;

    @Transactional
    @Override
    public Long create(final CreateCustomerDto customerDto) {
        throwExceptionIfLoginExists(customerDto.getLogin());
        Customer customer = mapper.toCustomer(customerDto);
        customer = customerRepository.save(customer);
        return customer.getId();
    }

    @Override
    public FindCustomerDto findById(final long id) {
        final Customer customer = getById(id);
        return mapper.toFindCustomerDto(customer);
    }

    @Transactional
    @Override
    public UpdateCustomerDto update(final UpdateCustomerDto customerDto, final long id) {
        Customer oldCustomer = getByIdForUpdate(id);
        final Customer newCustomer = mapper.toCustomer(customerDto);
        oldCustomer = updateFieldsOfOldCustomer(oldCustomer, newCustomer);
        customerRepository.save(oldCustomer);
        return mapper.toUpdateCustomerDto(getById(oldCustomer.getId()));
    }

    @Transactional
    @Override
    public void deleteById(final long id) {
        if (Objects.nonNull(getById(id))) {
            customerRepository.deleteById(id);
        }
    }

    private Customer getByIdForUpdate(final long id) {
        return customerRepository.findByIdLocked(id).orElseThrow(() -> throwCustomerNotFoundException(id));
    }

    private Customer getById(final long id) {
        return customerRepository.findById(id).orElseThrow(() -> throwCustomerNotFoundException(id));
    }

    private CustomerNotFoundException throwCustomerNotFoundException(final long id) {
        final String message = String.format(CUSTOMER_WAS_NOT_FOUND_BY_ID, id);
        log.warn(message);
        return new CustomerNotFoundException(message);
    }

    private void throwExceptionIfLoginExists(final String login) {
        final Optional<Customer> customerOp = customerRepository.findByLogin(login);
        if (customerOp.isPresent()) {
            String message = String.format("Customer login has already existed: %s", login);
            log.warn(message);
            throw new LoginExistsException(message, customerOp.get().getId());
        }
    }

    private Customer updateFieldsOfOldCustomer(Customer oldCustomer, final Customer newCustomer) {
        if (Objects.nonNull(newCustomer.getLogin())) {
            throwExceptionIfLoginExists(newCustomer.getLogin());
            oldCustomer.setLogin(newCustomer.getLogin());
        }
        if (Objects.nonNull(newCustomer.getEmail())) {
            oldCustomer.setEmail(newCustomer.getEmail());
        }
        if (Objects.nonNull(newCustomer.getIsActive())) {
            oldCustomer.setIsActive(newCustomer.getIsActive());
        }
        return oldCustomer;
    }
}
