package com.tinqinacademy.hotel.core.processors.checkavailablerooms;

import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.api.enums.BedType;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.input.AvailableRoomsInput;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.operation.CheckAvailableRoomsOperation;
import com.tinqinacademy.hotel.api.operations.checkavailablerooms.output.AvailableRoomsOutput;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckAvailableRoomsOperationProcessor implements CheckAvailableRoomsOperation {

  public static final int DEFAULT_BED_COUNT = 0;
  public static final int DEFAULT_WEEKS = 1;

  private final RoomRepository roomRepository;
  private final ConversionService conversionService;

  @Override
  public AvailableRoomsOutput process(AvailableRoomsInput input) {
    log.info("Start checkAvailableRooms input: {}", input);

    LocalDate startDate = getStartDateOrNow(input.getStartDate());
    LocalDate endDate = getEndDateOrOneWeekAhead(startDate, input.getEndDate());
    Integer bedCount = getBedCountOrDefault(input.getBedCount());
    List<String> bedSizes = getBedSizesAsStringList(input.getBedSizes());
    String bathroomTypeName = getBathroomTypeName(input.getBathroomType());

    List<UUID> result = roomRepository.findAllAvailableRoomIds(startDate, endDate, bathroomTypeName,
        bedSizes, bedCount);

    AvailableRoomsOutput output = convertUUIDListToOutput(result);
    log.info("End checkAvailableRooms output: {}", output);
    return output;
  }

  private LocalDate getStartDateOrNow(LocalDate startDate) {
    if (startDate == null) {
      return LocalDate.now();
    }
    return startDate;
  }

  private LocalDate getEndDateOrOneWeekAhead(LocalDate startDate, LocalDate endDate) {
    if (endDate == null) {
      return startDate.plusWeeks(DEFAULT_WEEKS);
    }
    return endDate;
  }

  private Integer getBedCountOrDefault(Integer bedCount) {
    if (bedCount == null || bedCount < 0) {
      return DEFAULT_BED_COUNT;
    }
    return bedCount;
  }

  private List<String> getBedSizesAsStringList(List<BedType> bedSizes) {
    return bedSizes.stream()
        .map(b -> BedSize.getByCode(b.getCode()).name())
        .toList();
  }

  private String getBathroomTypeName(com.tinqinacademy.hotel.api.enums.BathroomType bathroomType) {
    return conversionService.convert(bathroomType, BathroomType.class).name();
  }

  private AvailableRoomsOutput convertUUIDListToOutput(List<UUID> uuids) {
    return AvailableRoomsOutput.builder()
        .roomIds(uuids)
        .build();
  }
}
