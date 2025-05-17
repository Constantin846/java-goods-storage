package tk.project.goodsstorage.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tk.project.goodsstorage.dto.customer.create.CreateCustomerDto;
import tk.project.goodsstorage.dto.customer.create.CreateCustomerRequest;
import tk.project.goodsstorage.dto.customer.find.FindCustomerDto;
import tk.project.goodsstorage.dto.customer.find.FindCustomerResponse;
import tk.project.goodsstorage.dto.customer.update.UpdateCustomerDto;
import tk.project.goodsstorage.dto.customer.update.UpdateCustomerRequest;
import tk.project.goodsstorage.dto.customer.update.UpdateCustomerResponse;
import tk.project.goodsstorage.models.Customer;

@Mapper(componentModel = "spring")
public interface CustomerDtoMapper {
    CustomerDtoMapper MAPPER = Mappers.getMapper(CustomerDtoMapper.class);

    CreateCustomerDto toCreateCustomerDto(final CreateCustomerRequest customerRequest);

    @Mapping(target = "id", ignore = true)
    Customer toCustomer(final CreateCustomerDto customerDto);

    FindCustomerResponse toFindCustomerResponse(final FindCustomerDto customerDto);

    FindCustomerDto toFindCustomerDto(final Customer customer);

    @Mapping(target = "id", ignore = true)
    UpdateCustomerDto toUpdateCustomerDto(final UpdateCustomerRequest customerRequest);

    UpdateCustomerResponse toUpdateCustomerResponse(final UpdateCustomerDto customerDto);

    Customer toCustomer(final UpdateCustomerDto customerDto);

    UpdateCustomerDto toUpdateCustomerDto(final Customer customer);
}
