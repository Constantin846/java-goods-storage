package tk.project.goodsstorage.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tk.project.goodsstorage.customer.dto.create.CreateCustomerDto;
import tk.project.goodsstorage.customer.dto.create.CreateCustomerRequest;
import tk.project.goodsstorage.customer.dto.find.FindCustomerDto;
import tk.project.goodsstorage.customer.dto.find.FindCustomerResponse;
import tk.project.goodsstorage.customer.dto.update.UpdateCustomerDto;
import tk.project.goodsstorage.customer.dto.update.UpdateCustomerRequest;
import tk.project.goodsstorage.customer.dto.update.UpdateCustomerResponse;
import tk.project.goodsstorage.customer.mapper.CustomerDtoMapper;
import tk.project.goodsstorage.customer.service.CustomerService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("customers")
@RequiredArgsConstructor
public class CustomerController {
    private static final String ID = "id";
    private static final String ID_PATH = "/{id}";
    private final CustomerDtoMapper mapper;
    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> create(@Valid @RequestBody final CreateCustomerRequest customerRequest) {
        log.info("Create customer: {}", customerRequest);
        final CreateCustomerDto createCustomerDto = mapper.toCreateCustomerDto(customerRequest);
        return Map.of(ID, customerService.create(createCustomerDto));
    }

    @GetMapping(ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public FindCustomerResponse findById(@PathVariable(ID) final Long id) {
        log.info("Find customer by id: {}", id);
        final FindCustomerDto customerDto = customerService.findById(id);
        return mapper.toFindCustomerResponse(customerDto);
    }

    @PatchMapping(ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public UpdateCustomerResponse update(@Valid @RequestBody final UpdateCustomerRequest customerRequest,
                                         @PathVariable(ID) final Long id) {
        log.info("Update customer with id: {}", id);
        final UpdateCustomerDto updateCustomerDto = mapper.toUpdateCustomerDto(customerRequest);
        final UpdateCustomerDto resultUpdateCustomerDto = customerService.update(updateCustomerDto, id);
        return mapper.toUpdateCustomerResponse(resultUpdateCustomerDto);
    }

    @DeleteMapping(ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable(ID) final Long id) {
        log.info("Delete customer by id: {}", id);
        customerService.deleteById(id);
    }
}
