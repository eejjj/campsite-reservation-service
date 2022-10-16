package com.campsite.volcano.service;

import com.campsite.volcano.domain.User;
import com.campsite.volcano.repository.ReserationRepositoryImpl;
import com.campsite.volcano.repository.ReservationRepository;
import com.campsite.volcano.util.ReservationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.campsite.volcano.repository.ReserationRepositoryImpl.ZONE_ID;
import static com.campsite.volcano.repository.ReserationRepositoryImpl.today;

@Service
public class ReservationServiceImpl implements ReservationService{
    Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);
    private final ReservationRepository reservationRepository;
    private final ReservationUtils reservationUtils;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, ReservationUtils reservationUtils) {
        this.reservationRepository = reservationRepository;
        this.reservationUtils = reservationUtils;

    }

    @Override
    public List<Integer> getAvailabilities() {
        return reservationRepository.findAvailableDatesWithinRange(today.plusDays(1), today.plusDays(30));
    }

    @Override
    public List<Integer> getAvailabilities(int startDate) throws IllegalArgumentException {
        //validation logic
        if(!reservationUtils.inAvailableRange(startDate)) {
            throw new IllegalArgumentException("start date is invalid");
        }

        ZonedDateTime startZonedTime = reservationUtils.calcIntToZonedTime(startDate);
        return reservationRepository.findAvailableDatesWithinRange(startZonedTime, startZonedTime.plusDays(29));
    }


    @Override
    public List<Integer> getAvailabilities(int startDate, int endDate) throws IllegalArgumentException{
        reservationUtils.validateDateRange(startDate, endDate);
        ZonedDateTime startZonedTime = reservationUtils.calcIntToZonedTime(startDate);
        ZonedDateTime endZonedTime = reservationUtils.calcIntToZonedTime(endDate);
        return reservationRepository.findAvailableDatesWithinRange(startZonedTime, endZonedTime);
    }

    @Override
    public long addReservation(String userEmail, int startDate, int endDate) throws IllegalArgumentException {
        //validation
        reservationUtils.validateDateRange(startDate, endDate);

        ZonedDateTime startZonedTime = reservationUtils.calcIntToZonedTime(startDate);
        ZonedDateTime endZonedTime = reservationUtils.calcIntToZonedTime(endDate);
        if(!reservationUtils.isInThreeDays(startZonedTime, endZonedTime)) {
            throw new IllegalArgumentException("reservation length cannot be longer than three days");
        }

        long reservationId = reservationRepository.storeReservation(new User(userEmail), startZonedTime, endZonedTime);
        return reservationId;
    }


    @Override
    public long changeReservation(long reservationId, String userEmail, int startDate, int endDate) throws IllegalArgumentException {
        cancelReservation(reservationId, userEmail);
        long changedReservationId = addReservation(userEmail, startDate, endDate);
        return changedReservationId;
    }

    @Override
    public boolean cancelReservation(long reservationId, String userEmail) throws IllegalArgumentException {
        if(!reservationRepository.findByReservationId(reservationId).isPresent()) {
            throw new IllegalArgumentException("Reservation Id does not exist");
        }

        if(!reservationRepository.isCorrectUserUnderReservationId(reservationId, userEmail)) {
            throw new IllegalArgumentException("User email is not matching with reservation information");
        }
        return reservationRepository.deleteReservation(reservationId);
    }
}
