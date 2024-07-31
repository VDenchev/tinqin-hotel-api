package com.tinqinacademy.hotel.api.operations.signup.input;

import com.tinqinacademy.hotel.api.base.OperationInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SignUpInput implements OperationInput {

  @NotBlank(message = "First name cannot be blank")
  @Size(min = 2, max = 40, message = "First name should be between 2 and 40 characters long")
  @Schema(example = "Pewis")
  private String firstName;

  @NotBlank(message = "Last name cannot be blank")
  @Size(min = 2, max = 40, message = "Last name should be between 2 and 40 characters long")
  @Schema(example = "Pamilton")
  private String lastName;

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Invalid email format")
  @Schema(example = "pewis.pamilton@example.com")
  private String email;

  @NotBlank(message = "Password cannot be blank")
  @Size(min=6, max = 200, message = "Password must be between 6 and 200 characters long")
  @Schema(example = "securepassword")
  private String password;

  @NotBlank(message = "Phone number cannot be blank")
  @Size(min = 10, max=15, message="Invalid phone number format")
  @Schema(example = "+359 972947321")
  private String phoneNo;
}
