package com.tinqinacademy.hotel.api.models.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VisitorDetailsOutput {

  private LocalDate startDate;
  private LocalDate endDate;
  private String firstName;
  private String lastName;
  private String userId;
  private String idCardNo;
  private LocalDate birthDate;
  private LocalDate idCardValidity;
  private String idCardIssueAuthority;
  private LocalDate idCardIssueDate;
}
