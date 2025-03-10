package tk.project.goodsstorage.customer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tk.project.goodsstorage.customer.Customer;
import tk.project.goodsstorage.customer.dto.create.CreateCustomerDto;
import tk.project.goodsstorage.customer.dto.create.CreateCustomerRequest;
import tk.project.goodsstorage.customer.dto.find.FindCustomerDto;
import tk.project.goodsstorage.customer.dto.find.FindCustomerResponse;
import tk.project.goodsstorage.customer.dto.update.UpdateCustomerDto;
import tk.project.goodsstorage.customer.dto.update.UpdateCustomerRequest;
import tk.project.goodsstorage.customer.dto.update.UpdateCustomerResponse;

@Mapper(componentModel = "spring")
public interface CustomerDtoMapper {
    CustomerDtoMapper MAPPER = Mappers.getMapper(CustomerDtoMapper.class);

    CreateCustomerDto toCreateCustomerDto(CreateCustomerRequest customerRequest);

    Customer toCustomer(CreateCustomerDto customerDto);

    FindCustomerResponse toFindCustomerResponse(FindCustomerDto customerDto);

    FindCustomerDto toFindCustomerDto(Customer customer);

    UpdateCustomerDto toUpdateCustomerDto(UpdateCustomerRequest customerRequest);

    UpdateCustomerResponse toUpdateCustomerResponse(UpdateCustomerDto customerDto);

    Customer toCustomer(UpdateCustomerDto customerDto);

    UpdateCustomerDto toUpdateCustomerDto(Customer customer);
}
