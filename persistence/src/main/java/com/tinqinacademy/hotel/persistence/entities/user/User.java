package com.tinqinacademy.hotel.persistence.entities.user;

import com.tinqinacademy.hotel.persistence.entities.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Builder
@ToString
@Entity(name = "users")
public class User extends BaseEntity {

  @Column(name = "first_name", nullable = false, length = 40)
  private String firstName;
  @Column(name = "last_name", nullable = false, length = 40)
  private String lastName;
  @Column(name = "phone_number", nullable = false, length = 14)
  private String phoneNumber;
  @Column(name = "email", nullable = false)
  private String email;
  @Column(name = "password", nullable = false)
  private String password;
}
