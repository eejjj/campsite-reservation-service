package com.campsite.volcano.domain;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class Reservation {
    long reservationId;
    ZonedDateTime startDate;
    ZonedDateTime endDate;
    int duration; // how many days
    User user;

    public Reservation(long reservationId, ZonedDateTime fromDate, ZonedDateTime toDate, User user) {
        this.reservationId = reservationId;
        this.startDate = fromDate;
        this.endDate = toDate;
        this.duration = toDate.compareTo(fromDate) +1;
        this.user = user;
    }

    public long getReservationId() {
        return reservationId;
    }

    public void setReservationId(long reservationId) {
        this.reservationId = reservationId;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
