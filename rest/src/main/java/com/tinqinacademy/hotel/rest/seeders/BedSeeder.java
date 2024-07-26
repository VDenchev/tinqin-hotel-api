package com.tinqinacademy.hotel.rest.seeders;

import com.tinqinacademy.hotel.api.enums.BedType;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repositories.BedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BedSeeder implements CommandLineRunner {

  private final BedRepository bedRepository;

  @Override
  public void run(String... args) {
    List<Bed> savedBeds = bedRepository.findAll();

    Arrays.stream(BedType.values())
        .filter(bed -> savedBeds.stream()
            .noneMatch(sb -> sb.getBedSize().getCode().equals(bed.getCode()))
        ).forEach(b -> {
              Bed bed = Bed.builder()
                  .bedSize(BedSize.getByCode(b.getCode()))
                  .capacity(b.getCapacity())
                  .build();
              bedRepository.save(bed);
              log.info("BedSeeder - saved bed {}: ", bed);
            }
        );
  }
}
