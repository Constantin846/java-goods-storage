package tk.project.goodsstorage.customer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.project.goodsstorage.customer.Customer;
import tk.project.goodsstorage.customer.dto.create.CreateCustomerDto;
import tk.project.goodsstorage.customer.dto.find.FindCustomerDto;
import tk.project.goodsstorage.customer.dto.update.UpdateCustomerDto;
import tk.project.goodsstorage.customer.mapper.CustomerDtoMapper;
import tk.project.goodsstorage.customer.repository.CustomerRepository;
import tk.project.goodsstorage.exceptions.customer.CustomerNotFoundException;
import tk.project.goodsstorage.exceptions.customer.LoginExistsException;

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
    public Long create(CreateCustomerDto customerDto) {
        throwExceptionIfLoginExists(customerDto.getLogin());
        Customer customer = mapper.toCustomer(customerDto);
        customer = customerRepository.save(customer);
        return customer.getId();
    }

    @Override
    public FindCustomerDto findById(long id) {
        Customer customer = getById(id);
        return mapper.toFindCustomerDto(customer);
    }

    @Transactional
    @Override
    public UpdateCustomerDto update(UpdateCustomerDto customerDto) {
        Customer oldCustomer = getByIdForUpdate(customerDto.getId());
        Customer newCustomer = mapper.toCustomer(customerDto);
        oldCustomer = updateFields(oldCustomer, newCustomer);
        customerRepository.save(oldCustomer);
        return mapper.toUpdateCustomerDto(getById(oldCustomer.getId()));
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        if (Objects.nonNull(getById(id))) {
            customerRepository.deleteById(id);
        }
    }

    private Customer getByIdForUpdate(long id) {
        return customerRepository.findByIdLocked(id).orElseThrow(() -> {
            return throwCustomerNotFoundException(id);
        });
    }

    private Customer getById(long id) {
        return customerRepository.findById(id).orElseThrow(() -> {
            return throwCustomerNotFoundException(id);
        });
    }

    private CustomerNotFoundException throwCustomerNotFoundException(long id) {
        String message = String.format(CUSTOMER_WAS_NOT_FOUND_BY_ID, id);
        log.warn(message);
        return new CustomerNotFoundException(message);
    }

    private void throwExceptionIfLoginExists(String login) {
        Optional<Customer> customerOp = customerRepository.findByLogin(login);
        if (customerOp.isPresent()) {
            String message = String.format("Customer login has already existed: %s", login);
            log.warn(message);
            throw new LoginExistsException(message, customerOp.get().getId());
        }
    }

    private Customer updateFields(Customer oldCustomer, Customer newCustomer) {
        if (Objects.nonNull(newCustomer.getLogin())) {
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
