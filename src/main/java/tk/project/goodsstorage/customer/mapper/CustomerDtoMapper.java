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

    CreateCustomerDto toCreateCustomerDto(final CreateCustomerRequest customerRequest);

    Customer toCustomer(final CreateCustomerDto customerDto);

    FindCustomerResponse toFindCustomerResponse(final FindCustomerDto customerDto);

    FindCustomerDto toFindCustomerDto(final Customer customer);

    UpdateCustomerDto toUpdateCustomerDto(final UpdateCustomerRequest customerRequest);

    UpdateCustomerResponse toUpdateCustomerResponse(final UpdateCustomerDto customerDto);

    Customer toCustomer(final UpdateCustomerDto customerDto);

    UpdateCustomerDto toUpdateCustomerDto(final Customer customer);
}
