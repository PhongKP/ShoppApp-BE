package com.project.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {

    @NotBlank(message = "Số điện thoại hoặc email không được để trống")
    @JsonProperty("phone_or_email")
    private String phoneNumberOrEmail;

    @NotBlank(message = "Password không được để trống")
    private String password;

    @JsonProperty("role_id")
    private Long roleId;

}
