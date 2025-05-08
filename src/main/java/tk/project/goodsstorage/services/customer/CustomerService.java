package tk.project.goodsstorage.services.customer;

import tk.project.goodsstorage.dto.customer.create.CreateCustomerDto;
import tk.project.goodsstorage.dto.customer.find.FindCustomerDto;
import tk.project.goodsstorage.dto.customer.update.UpdateCustomerDto;

public interface CustomerService {

    Long create(final CreateCustomerDto customerDto);

    FindCustomerDto findById(final long id);

    UpdateCustomerDto update(final UpdateCustomerDto customerDto, final long id);

    void deleteById(final long id);
}
