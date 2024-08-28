package com.tinqinacademy.hotel.persistence.repositories;

import com.tinqinacademy.hotel.persistence.entities.booking.Booking;
import com.tinqinacademy.hotel.persistence.entities.guest.Guest;
import com.tinqinacademy.hotel.persistence.entities.room.Room;
import com.tinqinacademy.hotel.persistence.models.output.VisitorSearchResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomGuestRepository {

  private EntityManager em;

  @Autowired
  public CustomGuestRepository(JpaContext context) {
    this.em = context.getEntityManagerByManagedType(Guest.class);
  }

  public List<VisitorSearchResult> searchVisitors(
      LocalDate date1,
      LocalDate date2,
      String firstName,
      String lastName,
      LocalDate birthDate,
      List<String> userIds,
      String idCardNumber,
      String issueAuthority,
      String roomNumber
  ) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<VisitorSearchResult> query = cb.createQuery(VisitorSearchResult.class);
    Root<Booking> booking = query.from(Booking.class);
    Join<Booking, Guest> guest = booking.join("guests", JoinType.INNER);
    Join<Booking, Room> room = booking.join("room", JoinType.INNER);
    List<Predicate> predicates = new ArrayList<>();

    if (date1 != null && date2 != null) {
      LocalDate startDate = date1;
      LocalDate endDate = date2;
      if (date1.isAfter(date2)) {
        startDate = date2;
        endDate = date1;
      }
      Predicate datePredicate = cb.and(
          cb.greaterThanOrEqualTo(booking.get("startDate"), startDate),
          cb.lessThanOrEqualTo(booking.get("endDate"), endDate)
      );
      predicates.add(datePredicate);
    } else if (date1 != null) {
      Predicate endDatePredicate = cb.and(cb.greaterThanOrEqualTo(booking.get("startDate"), date1));
      predicates.add(endDatePredicate);
    } else if (date2 != null) {
      Predicate endDatePredicate = cb.and(cb.lessThanOrEqualTo(booking.get("endDate"), date2));
      predicates.add(endDatePredicate);
    }

    if (firstName != null && !firstName.isBlank()) {
      Predicate firstNamePredicate = cb.like(cb.lower(guest.get("firstName")), "%" + firstName.toLowerCase() + "%");
      predicates.add(firstNamePredicate);
    }
    if (lastName != null && !lastName.isBlank()) {
      Predicate lastNamePredicate = cb.like(cb.lower(guest.get("lastName")), "%" + lastName.toLowerCase() + "%");
      predicates.add(lastNamePredicate);
    }
    if (userIds != null && !userIds.isEmpty()) {
      Expression<String> expression = booking.get("userId").as(String.class);
      Predicate userIdsPredicate = expression.in(userIds);
      predicates.add(userIdsPredicate);
    }
    if (birthDate != null) {
      Predicate birthDatePredicate = cb.equal(guest.get("birthDate"), birthDate);
      predicates.add(birthDatePredicate);
    }
    if (idCardNumber != null && !idCardNumber.isBlank()) {
      Predicate idCardNumberPredicate = cb.like(guest.get("idCardNumber"), "%" + idCardNumber + "%");
      predicates.add(idCardNumberPredicate);
    }
    if (issueAuthority != null && !issueAuthority.isBlank()) {
      Predicate issueAuthorityPredicate = cb.like(cb.lower(guest.get("idCardIssueAuthority")), "%" + issueAuthority.toLowerCase() + "%");
      predicates.add(issueAuthorityPredicate);
    }
    if (roomNumber != null && !roomNumber.isBlank()) {
      Predicate roomNumberPredicate = cb.equal(cb.lower(room.get("number")), roomNumber.toLowerCase());
      predicates.add(roomNumberPredicate);
    }

    if (!predicates.isEmpty()) {
      query.where(predicates.toArray(new Predicate[0]));
    }

    query.select(cb.construct(
        VisitorSearchResult.class,
        booking.get("startDate"),
        booking.get("endDate"),
        guest.get("firstName"),
        guest.get("lastName"),
        booking.get("userId").as(String.class),
        guest.get("birthDate"),
        guest.get("idCardNumber"),
        guest.get("idCardValidity"),
        guest.get("idCardIssueAuthority"),
        guest.get("idCardIssueDate")
    )).distinct(true);

    return em.createQuery(query).getResultList();
  }
}
