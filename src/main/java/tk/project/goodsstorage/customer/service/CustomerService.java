package tk.project.goodsstorage.customer.service;

import tk.project.goodsstorage.customer.dto.create.CreateCustomerDto;
import tk.project.goodsstorage.customer.dto.find.FindCustomerDto;
import tk.project.goodsstorage.customer.dto.update.UpdateCustomerDto;

public interface CustomerService {

    Long create(final CreateCustomerDto customerDto);

    FindCustomerDto findById(final long id);

    UpdateCustomerDto update(final UpdateCustomerDto customerDto, final long id);

    void deleteById(final long id);
}
