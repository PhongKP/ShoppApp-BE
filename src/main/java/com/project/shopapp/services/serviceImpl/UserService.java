package com.project.shopapp.services.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.shopapp.component.JwtTokenUtils;
import com.project.shopapp.dto.UserDTO;
import com.project.shopapp.dto.UserUpdateDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.PermissionDeniedException;
import com.project.shopapp.model.Role;
import com.project.shopapp.model.User;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.repository.UserRepository;
import com.project.shopapp.services.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final Cloudinary cloudinary;

    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {
        if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())){
            throw new DataIntegrityViolationException("Phone number đã tồn tại");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())){
            throw new DataIntegrityViolationException("Email đã tồn tại");
        }

        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));
        if (role.getName().toUpperCase().equalsIgnoreCase("ADMIN")){
            throw new PermissionDeniedException("You cannot register an admin account");
        }

        // Mapping from userDTO => userModel
        User user = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .email(userDTO.getEmail())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();

        user.setRole(role);

        // Kiểm tra nếu có faceID or GoogleID thì không cần password -> Học sau khi tới spring security
        if (userDTO.getFacebookAccountId() == 0  || userDTO.getGoogleAccountId() == 0){
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
        }

        return userRepository.save(user);
    }

    @Override
    public String login(String phoneNumberOrEmail, String password, Long roleId) throws Exception {
        // Check phoneNumber or email
        Optional<User> optionalUser = Optional.empty();
        String subject = null;

        if (phoneNumberOrEmail != null && !phoneNumberOrEmail.isBlank()){
            optionalUser = userRepository.findByPhoneNumber(phoneNumberOrEmail);
            subject = phoneNumberOrEmail;
        }

        if (optionalUser.isEmpty() && phoneNumberOrEmail != null){
            optionalUser = userRepository.findByEmail(phoneNumberOrEmail);
            subject = phoneNumberOrEmail;
        }

        if (optionalUser.isEmpty())
            throw new DataNotFoundException("Invalid Phone Number or Password");

        User existingUser = optionalUser.get();

        // Check Password
        if (existingUser.getFacebookAccountId() == 0 || existingUser.getGoogleAccountId() == 0){
            if (!passwordEncoder.matches(password,existingUser.getPassword()))
                throw new BadCredentialsException("Wrong Username or password");
        }

        //Check Role
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if (optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())){
            throw new DataNotFoundException("Role not exists");
        }

        //Check blocked
        if (existingUser.isActive()){
            throw new Exception("User have been blocked");
        }

        // Authenticate với Spring Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                subject,password, existingUser.getAuthorities()
        );
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public User getUserDetailsByToken(String token) throws Exception {
        if (!jwtTokenUtil.isTokenExpired(token)){
            throw new Exception("Token is expired");
        }
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);

        if (optionalUser.isPresent()){
            return optionalUser.get();
        }else{
            throw new Exception("User Not Found");
        }
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserUpdateDTO updateUserDTO, MultipartFile file) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User Not Exists"));

        if (file != null && !file.isEmpty()){
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String avatarUrl = uploadResult.get("url").toString();
            updateUserDTO.setAvatarUrl(avatarUrl);
        }

        String newPhoneNumber = updateUserDTO.getPhoneNumber();
        if (newPhoneNumber != null){
            existingUser.setPhoneNumber(newPhoneNumber);
        }

        if (!existingUser.getPhoneNumber().equals(newPhoneNumber) &&
            userRepository.existsByPhoneNumber(newPhoneNumber)){
            throw new Exception("Phone already exists");
        }

        if (updateUserDTO.getFullName() != null){
            existingUser.setFullName(updateUserDTO.getFullName());
        }

        if (updateUserDTO.getAddress() != null){
            existingUser.setAddress(updateUserDTO.getAddress());
        }

        if (updateUserDTO.getDateOfBirth() != null){
            existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
        }

        if (updateUserDTO.getFacebookAccountId() > 0){
            existingUser.setFacebookAccountId(updateUserDTO.getFacebookAccountId());
        }

        if (updateUserDTO.getGoogleAccountId() > 0){
            existingUser.setGoogleAccountId(updateUserDTO.getGoogleAccountId());
        }

        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()){
            String newPassword  = updateUserDTO.getPassword();
            String encodePass = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodePass);
        }

        if (updateUserDTO.getAvatarUrl() != null && !updateUserDTO.getAvatarUrl().isEmpty()){
            existingUser.setAvatarUrl(updateUserDTO.getAvatarUrl());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public Page<User> findAll(String keyword, Pageable pageable) throws Exception {
        return userRepository.findAll(keyword,pageable);
    }

    @Override
    public void blockOrEnable(Long userId, Boolean isActive) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User không tồn tại"));

        if (isActive){
            existingUser.setActive(false);
        }else{
            existingUser.setActive(true);
        }
        userRepository.save(existingUser);
    }
}
