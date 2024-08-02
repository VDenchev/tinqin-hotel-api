package com.tinqinacademy.hotel.api.models.input;

import com.tinqinacademy.hotel.api.enums.BathroomType;
import com.tinqinacademy.hotel.api.enums.BedType;
import com.tinqinacademy.hotel.api.validation.groups.NonMandatoryFieldsGroup;
import com.tinqinacademy.hotel.api.validators.annotations.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RoomInput {

  @NotNull(message = "Bed size cannot be empty")
  @Schema(example = "[\"single\", \"double\"]")
  private List<
      @ValidEnum(enumClass = BedType.class, groups = {NonMandatoryFieldsGroup.class, Default.class}, message = "Invalid bed size")
      @NotBlank(message = "Bed size cannot be blank") String> bedSizes;

  @NotBlank(message = "Room number must not be blank")
  @Size(
      groups = {NonMandatoryFieldsGroup.class, Default.class},
      message = "Room number should be at most 10 characters long",
      min = 1, max = 10
  )
  @Schema(example = "101A")
  private String roomNo;

  @NotNull(message = "Floor cannot be blank")
  @Schema(example = "1")
  private Integer floor;

  @NotNull(message = "Price cannot be blank")
  @Positive(
      groups = {NonMandatoryFieldsGroup.class, Default.class},
      message = "Price has to be a positive number"
  )
  @Schema(example = "150")
  private BigDecimal price;

  @NotBlank(message = "Bathroom type cannot be blank")
  @Schema(example = "private")
  @ValidEnum(enumClass = BathroomType.class, groups = {NonMandatoryFieldsGroup.class, Default.class}, message = "Invalid bathroom type")
  private String bathroomType;
}
