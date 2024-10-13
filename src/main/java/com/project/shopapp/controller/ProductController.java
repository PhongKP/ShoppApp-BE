package com.project.shopapp.controller;

import com.github.javafaker.Faker;
import com.project.shopapp.dto.ProductDTO;
import com.project.shopapp.dto.ProductImageDTO;
import com.project.shopapp.model.Product;
import com.project.shopapp.model.ProductImage;
import com.project.shopapp.response.ProductListResponse;
import com.project.shopapp.response.ProductResponse;
import com.project.shopapp.services.IProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @Value("${upload.path}")
    private String uploadPath;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("")
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "", name = "keyword") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        PageRequest pageRequest = PageRequest.of(
                page-1,limit,
                Sort.by("create_at").descending()
        );

        Page<ProductResponse> productPage = productService.getAllProduct(keyword,categoryId,pageRequest);

        //Get tổng số trang
        int totalPage = productPage.getTotalPages();
        List<ProductResponse> productList = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                .productResponseList(productList)
                .totalPage(totalPage)
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById (@PathVariable("id") String productId){
        try {
            Product product = productService.getProductById(Long.valueOf(productId));
            return ResponseEntity.ok(ProductResponse.fromProduct(product));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByListId (@RequestParam("ids") String ids){
        try {
            List<Long> listId = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> productList = productService.findProductsByListId(listId);
            return ResponseEntity.ok(productList.stream()
                    .map(ProductResponse::fromProduct)
                    .collect(Collectors.toList())
            );
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName){
        try {
            Path path = Paths.get("src/main/resources/uploads");
            Resource resource = new UrlResource(path.resolve(imageName).toUri());
            if (resource.exists()){
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(path.resolve("notfound.png").toUri()));
            }
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    @Transactional
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result
    ){
        try {
            if (result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errorMessages);
            }
            // Lưu product vào database trước sau đó ms có thể lưu vào product_image
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Created an product successfully" + productDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/upload_images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> CreateProductImage(
            @PathVariable Long id,
            @ModelAttribute("files") List<MultipartFile> files)
    {
        try{
            // Khởi tạo và check xem product cần lưu ảnh có tồn tại không
            Product existingProduct = productService.getProductById(id);
            List<ProductImage> productImageList = new ArrayList<>();

            // Kiểm tra nếu trong form-header upload 5 cái ảnh thì hủy luôn
            if (files.size() > ProductImage.MAXIMUM_IMAGE_PER_PRODUCT){
                return ResponseEntity.badRequest().body("You only have choose 5 images once uploads");
            }

            // Kiểm tra files null
            files = files == null ? new ArrayList<MultipartFile>() : files;
            for (MultipartFile file : files){
                // Nếu file ảnh = 0 thì bỏ qua
                if (file.getSize() == 0)
                    continue;

                // Kiểm tra kích thước file ảnh < 10MB và định dạng ảnh hợp lệ
                if (file.getSize() > 10 * 1024 * 1024){
                    return ResponseEntity
                            .status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File upload to large");
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")){
                    return ResponseEntity
                            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File ảnh có định dạng không hợp lệ");
                }
                String fileName = storeFile(file);
                // Lưu các ảnh vào database product_image (Tạo Object ProductImage chỉ để debug xong xóa đi)
                ProductImage productImage = productService.createFileProductImage(
                        existingProduct.getId(),
                        ProductImageDTO.builder()
                                .imageUrl(fileName)
                                .build());
                productImageList.add(productImage);
            }
            return ResponseEntity.ok().body(productImageList);
        } catch (Exception e){
           return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String storeFile (MultipartFile file) throws IOException{
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "-" + fileName;

        //Đường dẫn đến thư mục lưu file
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();

        // Nếu chưa có thư mục thì tạo
        if (!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }

        // Lấy ra đường dẫn thư mục đầy đủ
        Path destination = Paths.get(uploadDir.toString(),uniqueFileName);
        //Copy file cần lưu vào thư mục đầy đủ
        Files.copy(file.getInputStream(),destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateProduct (
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO){
        try{
            Product product = productService.updateProduct(id,productDTO);
            return ResponseEntity.ok(ProductResponse.fromProduct(product));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    @Transactional
    public ResponseEntity<?> deleteById (@PathVariable Long id){
        try{
            productService.deleteProduct(id);
            return ResponseEntity.ok("Deleted Product with id = " + id);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //@PostMapping("/generate_fake_products")
    private ResponseEntity<?> generateFakeProducts(){
        Faker faker = new Faker();
        for (int i=0; i < 30; i++){
            String productName = faker.commerce().productName();
            if (productService.existsByName(productName))
                continue;
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(100_000, 100_000_000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId(faker.number().numberBetween(2,6))
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (Exception e){
                ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake Product created successfully");
    }

}
