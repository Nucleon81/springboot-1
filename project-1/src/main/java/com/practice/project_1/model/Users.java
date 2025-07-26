package com.practice.project_1.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "userDetails")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Users {
    @Id
    private String mobileNumber;

    @NotBlank(message = "Name is required")
    private String username;
    private String userEmail;
    private String userLocation;

}
