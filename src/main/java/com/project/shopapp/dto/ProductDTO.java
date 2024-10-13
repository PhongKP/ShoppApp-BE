package com.project.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    @NotBlank(message = "Tên khóa học không được để trống")
    @Size(min = 3, max = 200, message = "Tên khóa học phải có tối thiểu 3-200 ký tự")
    private String name;

    @Min(value = 0, message = "Giá tiền phải lớn hơn hoặc bằng 0")
    @Max(value = 10000000, message = "Giá tiền phải nhỏ hơn hoặc bằng 10,000,000")
    private float price;

    @JsonProperty("url_img")
    private String thumbnail;
    private String description;

    @JsonProperty("category_id")
    private int categoryId;
}
