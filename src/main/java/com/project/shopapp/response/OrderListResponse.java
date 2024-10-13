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
public class OrderListResponse {

    @JsonProperty("orders")
    private List<OrderResponse> orderResponseList;

    @JsonProperty("total_page")
    private int totalPage;
}
