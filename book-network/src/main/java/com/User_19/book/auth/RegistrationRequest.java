package com.User_19.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder


public class RegistrationRequest {
    @NotEmpty(message = "firstname is required")
    @NotBlank(message = "firstname is required")
    private String firstName;
    @NotEmpty(message = "lastname is required")
    @NotBlank(message = "lastname is required")
    private String lastName;
    @NotEmpty(message = "email is required")
    @NotBlank(message = "email is required")
    @Email
    private String email;
    @NotEmpty(message = "password is required")
    @NotBlank(message = "password is required")
    @Size(min = 8, message ="password should be 8 characters long minimum")
    private String password;
}
