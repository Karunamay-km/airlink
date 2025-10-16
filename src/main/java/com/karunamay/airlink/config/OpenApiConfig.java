package com.karunamay.airlink.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Value("${app.description}")
    private String appDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API Information
                .info(new Info()
                        .title(appName)
                        .version(appVersion)
                        .description(appDescription + "\n\n" +
                                "## Features\n" +
                                "- 🔐 JWT Authentication\n" +
                                "- 👥 User Management\n" +
                                "- 🛡️ Role-Based Access Control\n" +
                                "- 📊 Pagination & Sorting\n" +
                                "- 🔍 Advanced Search\n\n" +
                                "## Getting Started\n" +
                                "1. Register a new user via `/api/v1/users/register`\n" +
                                "2. Login via `/api/v1/users/login` to get JWT token\n" +
                                "3. Click **Authorize** button and enter: `Bearer {your-token}`\n" +
                                "4. Try protected endpoints")
                        .contact(new Contact()
                                .name("Karunamay Murmu")
                                .email("Karunamaymurmu@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))

                // Servers
                .servers(Arrays.asList(
                        new Server()
                                .url("http://127.0.0.1:8080/api")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://airlink-0-0-1.onrender.com/")
                                .description("Development Server"),
                        new Server()
                                .url("https://airlink-0-0-1.onrender.com/")
                                .description("Production Server")
                ))

                // Security Schemes
                .components(new Components()
                        // JWT Bearer Token
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token obtained from /api/user/login endpoint"))

                        // Basic Auth (for testing)
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Basic authentication using username and password"))
                )

                // Global Security Requirement
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"));

    }
}
