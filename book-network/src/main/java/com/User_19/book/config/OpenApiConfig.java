package com.User_19.book.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name="user19",
                        email = "contact@user19.com",
                        url = "http://user19.com/courses"
                ),
                description = "OpenApi documentaion for Spring security",
                title = "OpenApi specification - user19",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "https:/some-url.com"
                ),
                termsOfService = "Terms of service"


        ),servers = {
                @Server(
                        description = "Local ENV",
                        url="http://localhost:9090/api/v1"
                ),
        @Server(
                description = "PROD ENV",
                url="http://something-else/api/v1"
        )
},
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }

)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type= SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
