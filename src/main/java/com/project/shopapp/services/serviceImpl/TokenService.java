package com.project.shopapp.services.serviceImpl;

import com.project.shopapp.model.Token;
import com.project.shopapp.model.User;
import com.project.shopapp.repository.TokenRepository;
import com.project.shopapp.services.ITokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {

    @Value("${jwt.expiration}")
    private int expiration;
    private static final int MAX_TOKENS = 3;
    private final TokenRepository tokenRepository;

    @Override
    public void addToken(User user, String token, boolean isMobileDevice) {
        List<Token> userTokens = tokenRepository.findByUser(user);
        int tokenCount = userTokens.size();
        if (tokenCount >= MAX_TOKENS){
            boolean hasNonMobileToken = !userTokens.stream().allMatch(Token::isMobile);
            Token tokenToDel;
            if (hasNonMobileToken){
                tokenToDel = userTokens.stream()
                        .filter(userToken -> !userToken.isMobile())
                        .findFirst()
                        .orElse(userTokens.get(0));
            }else{
                // Trường hợp all deu la mobile thì chọn cái đầu tiên
                tokenToDel = userTokens.get(0);
            }
            tokenRepository.delete(tokenToDel);
        }
        long expirationInSec = expiration;
        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(expirationInSec);
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .revoked(false)
                .isExpired(false)
                .tokenType("Bearer")
                .expirationDate(expirationDate)
                .isMobile(isMobileDevice)
                .build();
        tokenRepository.save(newToken);
    }
}
