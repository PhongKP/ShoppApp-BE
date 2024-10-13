package com.project.shopapp.services.serviceImpl;

import com.project.shopapp.dto.ProductDTO;
import com.project.shopapp.dto.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.model.Categories;
import com.project.shopapp.model.Product;
import com.project.shopapp.model.ProductImage;
import com.project.shopapp.repository.CategoriesRepository;
import com.project.shopapp.repository.ProductImageRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.response.ProductResponse;
import com.project.shopapp.services.IProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoriesRepository categoriesRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {

        // Kiểm tra category có tồn tại không
        Categories existingCategories = categoriesRepository.findById((long) productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy category của product"));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .categories(existingCategories)
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .thumbnail(productDTO.getThumbnail())
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Long id) throws DataNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy product với id = "+ id));
    }

    @Override
    public Page<ProductResponse> getAllProduct(String keyword, Long categoryId, PageRequest pageRequest) {
        // Get Product với page và limit và trả về ProductResponseDTO
        return productRepository.searchProducts(keyword,categoryId,pageRequest)
                .map(ProductResponse::fromProduct);
    }

    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) throws DataNotFoundException {
        Product existingProduct = getProductById(id);
        if (existingProduct != null){
            Categories existingCategories = categoriesRepository.findById((long)productDTO.getCategoryId())
                            .orElseThrow(() -> new DataNotFoundException("Không tìm thấy category này"));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategories(existingCategories);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    public void deleteProduct(Long id) throws DataNotFoundException {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty())
            throw new DataNotFoundException("Không tìm thấy product với id = " + id + " để xóa");
        productRepository.deleteById(id);
    }

    @Override
    public ProductImage createFileProductImage (Long productId, ProductImageDTO productImageDTO)
            throws Exception
    {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() ->
                        new DataNotFoundException("Không tìm thấy product để thêm ảnh giới thiệu"));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .image_url(productImageDTO.getImageUrl())
                .build();

        // Kiểm tra trong DB product đó đã có bao nhiêu ảnh (MAX = 5)
       int size =  productImageRepository.findByProductId(productId).size();
       if (size >= ProductImage.MAXIMUM_IMAGE_PER_PRODUCT){
           throw new InvalidParamException("Number of images in Product must be < " +
                   ProductImage.MAXIMUM_IMAGE_PER_PRODUCT);
       }
       return productImageRepository.save(newProductImage);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public List<Product> findProductsByListId(List<Long> productIds) {
        return productRepository.findProductsByListId(productIds);
    }
}
