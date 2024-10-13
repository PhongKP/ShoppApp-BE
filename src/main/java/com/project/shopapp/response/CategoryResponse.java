package com.project.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.model.Categories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("created_category")
    private Categories categories;

    @JsonProperty("categories")
    private List<Categories> categoriesList;

}
