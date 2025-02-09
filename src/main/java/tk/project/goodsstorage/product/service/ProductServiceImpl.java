package tk.project.goodsstorage.product.service;

import org.springframework.stereotype.Service;
import tk.project.goodsstorage.product.dto.CreateProductDto;
import tk.project.goodsstorage.product.dto.ProductDto;
import tk.project.goodsstorage.product.dto.UpdateProductDto;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public UUID create(CreateProductDto createProductDto) {
        return null;
    }

    @Override
    public ProductDto findById(UUID id) {
        return null;
    }

    @Override
    public List<ProductDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public UpdateProductDto update(UpdateProductDto productDto) {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }
}
