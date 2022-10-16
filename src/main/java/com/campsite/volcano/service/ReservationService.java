package com.campsite.volcano.service;

import java.util.Date;
import java.util.List;

public interface ReservationService {
    List<Integer> getAvailabilities();

    List<Integer> getAvailabilities(int startDate) throws Exception;

    List<Integer> getAvailabilities(int startDate, int endDate);

    long addReservation(String userEmail, int startDate, int endDate);

    long changeReservation(long reservationId, String userEmail, int startDate, int endDate);

    boolean cancelReservation(long reservationId, String userEmail);
}
