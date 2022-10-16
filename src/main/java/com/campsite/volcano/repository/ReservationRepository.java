package com.campsite.volcano.repository;

import com.campsite.volcano.domain.Reservation;
import com.campsite.volcano.domain.User;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    List<Integer> findAvailableDatesWithinRange(ZonedDateTime startDate, ZonedDateTime endDate);
    boolean checkDateAvailable(ZonedDateTime day);
    boolean checkDateAvailable(int day);
    Optional<Reservation> findByReservationId(long reservationId); // this to be atomic long

    long storeReservation(User user, ZonedDateTime startDate, ZonedDateTime endDate);

    boolean deleteReservation(long reservationId);
    boolean isCorrectUserUnderReservationId(long reservationId, String userEmail);
}
