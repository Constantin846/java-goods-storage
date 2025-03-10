package tk.project.goodsstorage.customer.service;

import tk.project.goodsstorage.customer.dto.create.CreateCustomerDto;
import tk.project.goodsstorage.customer.dto.find.FindCustomerDto;
import tk.project.goodsstorage.customer.dto.update.UpdateCustomerDto;

public interface CustomerService {

    Long create(CreateCustomerDto customerDto);

    FindCustomerDto findById(long id);

    UpdateCustomerDto update(UpdateCustomerDto customerDto);

    void deleteById(long id);
}
