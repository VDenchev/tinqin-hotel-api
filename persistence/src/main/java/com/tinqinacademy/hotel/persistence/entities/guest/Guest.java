package com.tinqinacademy.hotel.persistence.entities.guest;

import com.tinqinacademy.hotel.persistence.entities.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@Entity(name = "guests")
public class Guest extends BaseEntity {

  @Column(name = "first_name", nullable = false, length = 40)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 40)
  private String lastName;

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Column(name = "id_card_validity", nullable = false)
  private LocalDate idCardValidity;

  @Column(name = "id_card_issue_date", nullable = false)
  private LocalDate idCardIssueDate;

  @Column(name = "id_card_issue_authority", nullable = false)
  private String idCardIssueAuthority;

  @Column(name = "id_card_number", nullable = false, length = 20)
  private String idCardNumber;

}
