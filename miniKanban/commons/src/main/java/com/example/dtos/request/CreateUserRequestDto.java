package com.example.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto (@NotBlank(message = "user name is required")
                                    @Size(min=2,max=10)  String userName
        , @NotBlank @Email(message = "invalid email format") String email
        , @NotBlank @Size(min=8,max=72) String password) {

}
