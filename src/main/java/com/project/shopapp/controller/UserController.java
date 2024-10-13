package com.project.shopapp.controller;

import com.project.shopapp.dto.UserDTO;
import com.project.shopapp.dto.UserLoginDTO;
import com.project.shopapp.dto.UserUpdateDTO;
import com.project.shopapp.model.User;
import com.project.shopapp.response.LoginResponse;
import com.project.shopapp.response.RegisterResponse;
import com.project.shopapp.response.UserListResponse;
import com.project.shopapp.response.UserResponse;
import com.project.shopapp.services.ITokenService;
import com.project.shopapp.services.IUserService;
import com.project.shopapp.utils.LocalizationUtils;
import com.project.shopapp.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final IUserService userService;
    private final ITokenService tokenService;
    private final LocalizationUtils localizationUtils;

    @Value("${api.prefix}")
    private String apiPrefix;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ){
        try{
            if (result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        RegisterResponse.builder()
                                .message(localizationUtils
                                        .getLocalizedMessage(MessageKeys.REGISTER_FAILED,errorMessages))
                                .build()
                );
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword()))
                return ResponseEntity.badRequest().body(
                        RegisterResponse.builder()
                                .message(localizationUtils
                                        .getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                                .build()
                );
            User user = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body(
                    RegisterResponse.builder()
                            .message(localizationUtils
                                    .getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                            .user(user)
                            .build()
            );
        } catch (Exception e){
            return ResponseEntity.badRequest().body(
                    RegisterResponse.builder()
                            .message(localizationUtils
                                    .getLocalizedMessage(MessageKeys.REGISTER_FAILED,e.getMessage()))
                            .build()
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            BindingResult result,
            HttpServletRequest request
    ){
        try {
            if (result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        LoginResponse.builder()
                                .message(localizationUtils
                                        .getLocalizedMessage(MessageKeys.LOGIN_FAILED,errorMessages))
                                .build()
                );
            }
            String token = userService.login(
                    userLoginDTO.getPhoneNumberOrEmail(),
                    userLoginDTO.getPassword(),
                    userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId());
            String userAgent = request.getHeader("User-Agent");
            User user = userService.getUserDetailsByToken(token);
            tokenService.addToken(user,token,isMobileDevice(userAgent));
            return ResponseEntity.ok(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                            .token(token)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils
                                    .getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
        }
    }

    private boolean isMobileDevice(String userAgent){
        return userAgent.toLowerCase().contains("mobile");
    }

    @GetMapping("/details")
    public ResponseEntity<?> getUserDetailsByToken (@RequestHeader("Authorization") String token){
        try {
            String extractedToken = token.substring(7); // Bearer Token => index = 7
            User user = userService.getUserDetailsByToken(extractedToken);
            if (user.getAvatarUrl() == null){
                user.setAvatarUrl(String.format("%s/products/images/notfound.png",apiPrefix));
            }
            return ResponseEntity.ok().body(UserResponse.fromUser(user));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/details/{userId}")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authorizationHeader,
                                        @PathVariable("userId") Long userId,
                                        @ModelAttribute UserUpdateDTO updatedUserDTO,
                                        @RequestPart("file") MultipartFile file){
        try {
            String token = authorizationHeader.substring(7);
            User user = userService.getUserDetailsByToken(token);
            // Check việc update này chỉ có thể là chính bản thân mình mới làm được
            if (user.getId() != userId){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updatedUser = userService.updateUser(userId,updatedUserDTO,file);
            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUser(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ){
        try {
            PageRequest pageRequest = PageRequest.of(
                    page-1,limit,
                    Sort.by("id").ascending()
            );
            Page<UserResponse> userResponsePage = userService.findAll(keyword,pageRequest)
                    .map(user -> UserResponse.fromUser(user));
            int totalPage = userResponsePage.getTotalPages();
            List<UserResponse> userResponseList = userResponsePage.getContent();
            return ResponseEntity.ok().body(UserListResponse.builder()
                    .userResponseList(userResponseList)
                    .totalPage(totalPage)
                    .build()
            );
        } catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("block/{user_id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> blockOrEnableUser(
            @PathVariable("user_id") Long userId,
            @RequestParam("is_active") Boolean isActive
    ){
        try {
            userService.blockOrEnable(userId,isActive);
            return ResponseEntity.ok(new String("Successfully blocked or unblocked user"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Cannot block or enabled user");
        }
    }

}
