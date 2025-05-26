package tk.project.goodsstorage.controllers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tk.project.goodsstorage.BaseIntegrationTest;
import tk.project.goodsstorage.dto.customer.create.CreateCustomerRequest;
import tk.project.goodsstorage.dto.customer.find.FindCustomerResponse;
import tk.project.goodsstorage.dto.customer.update.UpdateCustomerRequest;
import tk.project.goodsstorage.dto.customer.update.UpdateCustomerResponse;
import tk.project.goodsstorage.exceptionhandler.exceptions.ApiError;
import tk.project.goodsstorage.exceptionhandler.exceptions.customer.CustomerNotFoundException;
import tk.project.goodsstorage.exceptionhandler.exceptions.customer.LoginExistsException;
import tk.project.goodsstorage.models.Customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerIntegrationTest extends BaseIntegrationTest {

    @Test
    @SneakyThrows
    @DisplayName("Create customer successfully")
    void createCustomer() {
        // GIVEN
        final CreateCustomerRequest customerRequest =
                new CreateCustomerRequest("login", "email@mail.com", null);

        // WHEN
        final String result = mockMvc.perform(post("/customers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertNotNull(result);
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed create customer when customer login exists")
    void createCustomerExceptionCustomerLoginExists() {
        // GIVEN
        Customer customer = new Customer();
        customer.setLogin("login");
        customer.setEmail("e@mail.com");
        customer.setIsActive(true);
        customer = customerRepository.save(customer);

        final CreateCustomerRequest customerRequest =
                new CreateCustomerRequest("login", "email@mail.com", null);

        // WHEN
        final String result = mockMvc.perform(post("/customers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(LoginExistsException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(customer.getId().toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Find customer by id successfully")
    void findCustomerById() {
        // GIVEN
        Customer customer = new Customer();
        customer.setLogin("login");
        customer.setEmail("email@mail.com");
        customer.setIsActive(true);
        customer = customerRepository.save(customer);

        final FindCustomerResponse expectedResponse = FindCustomerResponse.builder()
                .id(customer.getId())
                .login(customer.getLogin())
                .email(customer.getEmail())
                .isActive(customer.getIsActive())
                .build();

        // WHEN
        final String result = mockMvc.perform(get(String.format("/customers/%s", customer.getId()))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertEquals(objectMapper.writeValueAsString(expectedResponse), result);
    }

    @Test
    @SneakyThrows
    @DisplayName("Customer was not found by id")
    void findCustomerByIdFailed() {
        // GIVEN
        final Long customerId = 1L;

        // WHEN
        final String result = mockMvc.perform(get(String.format("/customers/%s", customerId))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(CustomerNotFoundException.class.getSimpleName(), apiError.getExceptionName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Update customer successfully")
    void updateCustomer() {
        // GIVEN
        Customer customer = new Customer();
        customer.setLogin("login");
        customer.setEmail("email@mail.com");
        customer.setIsActive(true);
        customer = customerRepository.save(customer);

        final UpdateCustomerRequest customerRequest = UpdateCustomerRequest.builder()
                .login("new login")
                .email("new_email@mail.com")
                .build();

        final UpdateCustomerResponse expectedResponse = UpdateCustomerResponse.builder()
                .id(customer.getId())
                .login(customerRequest.getLogin())
                .email(customerRequest.getEmail())
                .isActive(customer.getIsActive())
                .build();

        // WHEN
        final String result = mockMvc.perform(patch(String.format("/customers/%s", customer.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // THEN
        assertEquals(objectMapper.writeValueAsString(expectedResponse), result);
    }

    @Test
    @SneakyThrows
    @DisplayName("Failed update customer when customer login exists")
    void updateCustomerExceptionCustomerLoginExists() {
        // GIVEN
        Customer customerWithExistedLogin = new Customer();
        customerWithExistedLogin.setLogin("existed login");
        customerWithExistedLogin.setEmail("e@m.com");
        customerWithExistedLogin.setIsActive(true);
        customerWithExistedLogin = customerRepository.save(customerWithExistedLogin);

        Customer customer = new Customer();
        customer.setLogin("login");
        customer.setEmail("email@mail.com");
        customer.setIsActive(true);
        customer = customerRepository.save(customer);

        final UpdateCustomerRequest customerRequest = UpdateCustomerRequest.builder()
                .login("existed login")
                .email("new_email@mail.com")
                .build();

        // WHEN
        final String result = mockMvc.perform(patch(String.format("/customers/%s", customer.getId()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(LoginExistsException.class.getSimpleName(), apiError.getExceptionName());
        assertTrue(apiError.getMessage().contains(customerWithExistedLogin.getId().toString()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Filed update when customer was not found by id")
    void updateCustomerFailed() {
        // GIVEN
        final Long customerId = 1L;

        final UpdateCustomerRequest customerRequest = UpdateCustomerRequest.builder()
                .login("existed login")
                .email("new_email@mail.com")
                .build();

        // WHEN
        final String result = mockMvc.perform(patch(String.format("/customers/%s", customerId))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ApiError apiError = objectMapper.readValue(result, ApiError.class);

        // THEN
        assertEquals(CustomerNotFoundException.class.getSimpleName(), apiError.getExceptionName());
    }

    @Test
    @SneakyThrows
    @DisplayName("Delete customer by id")
    void deleteCustomerById() {
        // GIVEN
        Customer customer = new Customer();
        customer.setLogin("login");
        customer.setEmail("email@mail.com");
        customer.setIsActive(true);
        customer = customerRepository.save(customer);

        // WHEN
        mockMvc.perform(delete(String.format("/customers/%s", customer.getId()))
                        .contentType("application/json"))
                .andDo(print())
                // THEN
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}