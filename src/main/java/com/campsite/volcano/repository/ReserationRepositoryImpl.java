package com.campsite.volcano.repository;

import com.campsite.volcano.domain.Reservation;
import com.campsite.volcano.domain.User;
import com.campsite.volcano.util.ReservationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ReserationRepositoryImpl implements ReservationRepository{

    Logger logger = LoggerFactory.getLogger(ReserationRepositoryImpl.class);


    //Instant instant = Instant.now();
    public static final String TIME_ZONE_STRING = "Pacific/Honolulu";
    public static final ZoneId ZONE_ID = ZoneId.of( TIME_ZONE_STRING );
    public static ZonedDateTime today;// = ZonedDateTime.ofInstant( Instant.now() , ZONE_ID );

    //store current reservations
    //private static ConcurrentSkipListMap<ZonedDateTime, Reservation> reservations = new ConcurrentSkipListMap<>();
    // store available dates for faster fetch
    private static ConcurrentSkipListSet<Integer> availiabilities = new ConcurrentSkipListSet();
    // store reservation id to reservations
    private static ConcurrentHashMap<Long, Reservation> reservations = new ConcurrentHashMap<>();
    //reservation id
    private static AtomicLong al = new AtomicLong(0L);


    private final ReservationUtils reservationUtils;

    @Autowired
    public ReserationRepositoryImpl(ReservationUtils reservationUtils) {
        this.reservationUtils = reservationUtils;

    }

    public static ConcurrentHashMap<Long, Reservation> getReservations() {
        return reservations;
    }

    //everyday at 12am add one more availability
    @Scheduled(cron = "0 0 0 * * *",zone = TIME_ZONE_STRING)
    public void addNewAvailability() {
        today = today.plusDays(1);
        availiabilities.add(reservationUtils.calcDate(today));
        availiabilities.pollFirst();

    }


    //add 30 days only at the beginning of application
    @EventListener
    public void onApplicationEvent(ApplicationStartedEvent event) {
        today = ZonedDateTime.ofInstant( Instant.now() , ZONE_ID );

        ZonedDateTime firstDay = today.plusDays(1);
        int fisrtDayVal = reservationUtils.calcDate(firstDay);
        ZonedDateTime lastDay = firstDay.plusDays(29);
        int lastDayVal = reservationUtils.calcDate(lastDay);

        availiabilities.add(fisrtDayVal);
        while(availiabilities.last() < lastDayVal) {
            availiabilities.add(reservationUtils.calcDate(firstDay));
            firstDay =  firstDay.plusDays(1);
        }

        logger.info("today is " + today.toString() );
        logger.info("firstDay is " + firstDay );
        logger.info("lastDay is " + lastDay );
        logger.info("availiabilities are " + availiabilities + " with len of " + availiabilities.size() );
    }

    @Override
    public List<Integer> findAvailableDatesWithinRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        return new ArrayList(availiabilities.subSet(reservationUtils.calcDate(startDate), reservationUtils.calcDate(endDate.plusDays(1))));
    }

    @Override
    public boolean checkDateAvailable(ZonedDateTime day) {
        return availiabilities.contains(reservationUtils.calcDate(day));
    }

    @Override
    public boolean checkDateAvailable(int day) {
        return availiabilities.contains(day);
    }

    @Override
    public Optional<Reservation> findByReservationId(long reservationId) {
        return Optional.ofNullable(reservations.get(reservationId));
    }

    @Override
    public synchronized long storeReservation(User user, ZonedDateTime startDate, ZonedDateTime endDate) throws IllegalArgumentException{
        if(checkOverlap(startDate, endDate)) {
            long reservationId = al.incrementAndGet();
            reservations.put(reservationId, new Reservation(
                    reservationId, startDate, endDate, user
            ));
            logger.info("enddate" + reservations.get(reservationId).getEndDate().toString());
            availiabilities.removeAll(availiabilities.subSet(reservationUtils.calcDate(startDate), reservationUtils.calcDate(endDate.plusDays(1))));
            return reservationId;
        } else {
            throw new IllegalArgumentException("Overlapping reservation exist");
        }

    }

    private boolean checkOverlap(ZonedDateTime startDate, ZonedDateTime endDate) {
        int endVal = reservationUtils.calcDate(endDate);

        ZonedDateTime currDay = startDate;
        int currVal = reservationUtils.calcDate(currDay);

        while(currVal <= endVal) {
            // when not available
            if(!availiabilities.contains(currVal)) {
                return false;
            }
            currDay = currDay.plusDays(1);
            currVal = reservationUtils.calcDate(currDay);
        }
        return true;
    }

    @Override
    public synchronized boolean deleteReservation(long reservationId) {
        if(!reservations.containsKey(reservationId)) {
            return false;
        }
        Reservation reservation = reservations.get((Long)reservationId);
        addDays(reservation.getStartDate(), reservation.getEndDate());
        reservations.remove((Long)reservationId);
        return true;
    }

    private void addDays(ZonedDateTime startDate, ZonedDateTime endDate) {
        int endVal = reservationUtils.calcDate(endDate);

        ZonedDateTime currDay = startDate;
        int currVal = reservationUtils.calcDate(currDay);

        while(currVal <= endVal) {
            // when not available
            if(reservationUtils.inAvailableRange(currVal)) {
                availiabilities.add(currVal);
            }
            currDay = currDay.plusDays(1);
            currVal = reservationUtils.calcDate(currDay);
        }
    }


    public boolean isCorrectUserUnderReservationId(long reservationId, String userEmail) {
        return reservations.get(reservationId).getUser().getEmail().equals(userEmail);
    }
}
