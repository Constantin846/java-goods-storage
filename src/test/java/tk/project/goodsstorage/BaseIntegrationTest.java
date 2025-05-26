package tk.project.goodsstorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tk.project.goodsstorage.repositories.CustomerRepository;
import tk.project.goodsstorage.repositories.order.OrderRepository;
import tk.project.goodsstorage.repositories.order.OrderedProductRepository;
import tk.project.goodsstorage.repositories.product.ProductImageRepository;
import tk.project.goodsstorage.repositories.product.ProductRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BaseIntegrationTest {
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected CustomerRepository customerRepository;
    @Autowired
    protected OrderRepository orderRepository;
    @Autowired
    protected OrderedProductRepository orderedProductRepository;
    @Autowired
    protected ProductRepository productRepository;
    @Autowired
    protected ProductImageRepository productImageRepository;

    @AfterEach
    protected void clearDatabase() {
        orderedProductRepository.deleteAll();
        orderRepository.deleteAll();
        productImageRepository.findAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
    }
}
