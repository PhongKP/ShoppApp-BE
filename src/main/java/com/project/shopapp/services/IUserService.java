package com.project.shopapp.services;

import com.project.shopapp.dto.UserDTO;
import com.project.shopapp.dto.UserUpdateDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {

    User createUser(UserDTO userDTO) throws Exception;

    String login (String phoneNumber, String password, Long roleId) throws Exception;

    User getUserDetailsByToken (String token) throws Exception;

    User updateUser(Long userId, UserUpdateDTO updateUserDTO, MultipartFile file) throws Exception;

    Page<User> findAll(String keyword, Pageable pageable) throws Exception;

    void blockOrEnable(Long userId, Boolean isActive) throws Exception;

}
