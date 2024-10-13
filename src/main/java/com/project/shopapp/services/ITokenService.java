package com.project.shopapp.services;

import com.project.shopapp.model.User;

public interface ITokenService {

    void addToken(User user, String token, boolean isMobileDevice);

}
