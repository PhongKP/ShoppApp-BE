package com.project.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponse {

    @JsonProperty("products")
    private List<ProductResponse> productResponseList;

    @JsonProperty("total_page")
    private int totalPage;

}
