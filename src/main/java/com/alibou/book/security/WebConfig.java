package com.alibou.book.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${application.file.upload-dir}")  // Use same property you used in your service
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // Resolve absolute path to your upload folder
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();

        System.out.println("Serving static files from: " + absolutePath);

            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(absolutePath);
        }
}
