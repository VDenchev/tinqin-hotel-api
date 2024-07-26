package com.tinqinacademy.hotel.persistence.entities.bed;

import com.tinqinacademy.hotel.persistence.enums.BedSize;
import com.tinqinacademy.hotel.persistence.entities.base.BaseEntity;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity(name = "beds")
public class Bed extends BaseEntity {

  @Column(name = "bed_size", nullable = false, unique = true)
  @Enumerated(EnumType.STRING)
  private BedSize bedSize;

  @Column(name = "capacity", nullable = false)
  private Integer capacity;

//  @ManyToMany(mappedBy = "beds")
//  private List<Room> rooms;
}
