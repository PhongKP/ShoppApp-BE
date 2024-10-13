package com.project.shopapp.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.api-key}")
    private String api_key;

    @Value("${cloudinary.api-secret}")
    private String api_secret;

    @Value("${cloudinary.name}")
    private String cloudinaryName;

    @Bean
    public Cloudinary cloudinary(){
        Map config = new HashMap();
        config.put("cloud_name", cloudinaryName);
        config.put("api_key", api_key);
        config.put("api_secret", api_secret);
        config.put("secure", true);
        return new Cloudinary(config);
    }

}
