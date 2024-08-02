package com.tinqinacademy.hotel.persistence.entities.room;

import com.tinqinacademy.hotel.persistence.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.entities.base.BaseEntity;
import com.tinqinacademy.hotel.persistence.entities.bed.Bed;
import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity(name = "rooms")
public class Room extends BaseEntity {

  @Column(name = "number", unique = true, nullable = false, length = 20)
  private String number;

  @Column(name = "floor", nullable = false)
  private Integer floor;

  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(name = "bathroom_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private BathroomType bathroomType;

  @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
  @JoinTable(
      name = "room_beds",
      joinColumns = {@JoinColumn(name = "room_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "bed_id", referencedColumnName = "id")}
  )
  private List<Bed> beds = new ArrayList<>();

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "room")
  private List<Booking> bookings = new ArrayList<>();
}
