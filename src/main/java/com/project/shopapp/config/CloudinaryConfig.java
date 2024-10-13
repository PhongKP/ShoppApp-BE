package com.project.shopapp.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(){
        Map config = new HashMap();
        config.put("cloud_name", "dnziatudh");
        config.put("api_key", "562738571532324");
        config.put("api_secret", "FIfDXQOCnC6JFSoLQnxkzoW3K8E");
        config.put("secure", true);
        return new Cloudinary(config);
    }

}
