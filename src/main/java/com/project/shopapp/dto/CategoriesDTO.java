package com.project.shopapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesDTO {

    @NotBlank(message = "Category name must be have an value, not be empty")
    private String name;

}
