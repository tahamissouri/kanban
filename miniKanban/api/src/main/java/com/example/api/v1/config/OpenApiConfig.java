package com.example.api.v1.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("miniKanban API")
                        .version("1.0.0")
                        .description("""
                                RESTful API for miniKanban - A modern Kanban board application.
                                
                                ## Features
                                - User authentication with JWT
                                - Board management
                                - Column and card operations
                                - Drag and drop support
                                - Team collaboration
                                
                                ## Authentication
                                Most endpoints require authentication. Use the `/api/v1/auth/login` endpoint 
                                to obtain a JWT token, then include it in the Authorization header as:
                                `Authorization: Bearer <token>`
                                """)
                        .contact(new Contact()
                                .name("miniKanban Team")
                                .email("support@minikanban.com")
                                .url("https://github.com/yourusername/miniKanban"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.minikanban.com")
                                .description("Production Server")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token obtained from /api/v1/auth/login")));
    }
}

// Made with Bob
