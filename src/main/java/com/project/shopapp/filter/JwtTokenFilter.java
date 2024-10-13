package com.project.shopapp.filter;

import com.project.shopapp.component.JwtTokenUtils;
import com.project.shopapp.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    @Value("${api.prefix}")
    private String apiPrefix;

    private boolean initialized = false;

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtil;

    private List<Pair<String,String>> bypassTokens;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        //  filterChain.doFilter(request,response); // Cho request đi qua
        init();
        try{
            if(isByPassToken(request)){
                filterChain.doFilter(request,response);
                return;
            }
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null && !authHeader.startsWith("Bearer ")){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
            final String token = authHeader.substring(7);
            final String subject = jwtTokenUtil.extractPhoneNumber(token);
            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null){
                User existingUser = (User) userDetailsService.loadUserByUsername(subject);
                if (jwtTokenUtil.validateToken(token, existingUser)){
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    existingUser,
                                    null,
                                    existingUser.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request,response);
        } catch (Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    private void init(){
        if (!initialized){
            bypassTokens = Arrays.asList(
                    Pair.of(String.format("%s/products",apiPrefix),"GET"),
                    Pair.of(String.format("%s/categories",apiPrefix),"GET"),
                    Pair.of(String.format("%s/users/register",apiPrefix),"POST"),
                    Pair.of(String.format("%s/users/login",apiPrefix),"POST"),
                    Pair.of(String.format("%s/roles", apiPrefix), "GET"),
                    Pair.of(String.format("%s/carts", apiPrefix), "POST"),
                    Pair.of(String.format("%s/carts", apiPrefix), "GET"),
                    Pair.of(String.format("%s/carts", apiPrefix), "PUT"),
                    Pair.of(String.format("%s/carts", apiPrefix), "DELETE"),
                    Pair.of(String.format("%s/orders",apiPrefix), "GET"),
                    Pair.of(String.format("%s/order_detail/order", apiPrefix), "GET")
//                    Pair.of(String.format("%s/products",apiPrefix), "POST")
            );
        }
    }

    private boolean isByPassToken(@NonNull HttpServletRequest request){

        if (request.getServletPath().equals(String.format("/%s/orders/get-orders",apiPrefix)) &&
                request.getMethod().equals("GET")){
            return false;
        }

        for(Pair<String,String> bypassToken : bypassTokens){
            if (request.getServletPath().contains(bypassToken.getFirst()) &&
                    request.getMethod().equals(bypassToken.getSecond())){
                return true;
            }
        }
        return false;
    }
}
