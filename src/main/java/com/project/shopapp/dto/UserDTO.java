package com.project.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @JsonProperty("fullname")
    private String fullName;

    @NotBlank(message = "Số điện thoại là bắt buộc")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotBlank(message = "Email không được để trống")
    private String email;

    private String address;

    @NotBlank(message = "Password là bắt buộc")
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("face_account_id")
    private int facebookAccountId;

    @JsonProperty("google_account_id")
    private int googleAccountId;

    @NotNull(message = "Role ID là bắt buộc")
    @JsonProperty("role_id")
    private Long roleId;
}
