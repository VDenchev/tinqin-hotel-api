package com.tinqinacademy.hotel.persistence.models.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor
@Setter
public class VisitorSearchResult {

  private LocalDate startDate;
  private LocalDate endDate;
  private String firstName;
  private String lastName;
  private String userId;
  private LocalDate birthDate;
  private String idCardNumber;
  private LocalDate idCardValidity;
  private String idCardIssueAuthority;
  private LocalDate idCardIssueDate;
}
