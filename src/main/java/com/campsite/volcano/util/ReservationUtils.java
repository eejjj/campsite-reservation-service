package com.campsite.volcano.util;

import com.campsite.volcano.controller.ReservationController;
import com.campsite.volcano.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

import static com.campsite.volcano.repository.ReserationRepositoryImpl.ZONE_ID;
import static com.campsite.volcano.repository.ReserationRepositoryImpl.today;

    @Component
    public class ReservationUtils {
        Logger logger = LoggerFactory.getLogger(ReservationUtils.class);

        public int calcDate(ZonedDateTime date) {
            //logger.info(String.valueOf(date.getDayOfMonth()));
            return date.getMonthValue() * 1000000 + date.getDayOfMonth() * 10000+ date.getYear();
        }


        public boolean inAvailableRange(int currVal) {
            if(currVal < calcDate(today.plusDays(1))) return false;
            if(currVal > calcDate(today.plusDays(30))) return false;
            return true;
        }


        public ZonedDateTime calcIntToZonedTime(int startDate)  throws IllegalStateException {

            int startYear = startDate %10000;
            int startDay = (startDate/ 10000) % 100 ;
            int startMonth = startDate/ 1000000 ;
            //logger.info("start year:"  + startYear  +  " month:" + startMonth +  " date:" +  startDay );

            validateDateValues(startYear, startMonth, startDay);

            ZonedDateTime time = ZonedDateTime.of(startYear, startMonth, startDay, 0, 0, 0, 0, ZONE_ID);
            return time;
        }

        private void validateDateValues(int startYear, int startMonth, int startDay)  throws IllegalStateException {
            if(startYear < 2022) {
                throw new IllegalArgumentException("Year value not valid");
            }

            if(startMonth > 12) {
                throw new IllegalArgumentException("Month value not valid");
            }

            if(startDay > 31) {
                throw new IllegalArgumentException("Day value not valid");
            }
        }

        public boolean isStartDateBeforeEndDate(ZonedDateTime startZonedTime, ZonedDateTime endZonedTime) {
            if (startZonedTime.isBefore(endZonedTime) ||
                    (startZonedTime.getYear() == endZonedTime.getYear() && startZonedTime.getMonth() == endZonedTime.getMonth() ||
                            startZonedTime.getDayOfMonth() == endZonedTime.getDayOfMonth())) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isInThreeDays(ZonedDateTime startZonedTime, ZonedDateTime endZonedTime) {
            //logger.info(String.valueOf(startZonedTime.compareTo(endZonedTime)));
            if (startZonedTime.plusDays(3).isAfter(endZonedTime)) {
                return true;
            } else {
                return false;
            }
        }

        public void validateDateRange(int startDate, int endDate) throws IllegalStateException {
            //validation logic
            if(!inAvailableRange(startDate)) {
                throw new IllegalArgumentException("start date is invalid");
            }

            if(!inAvailableRange(endDate)) {
                throw new IllegalArgumentException("end date is invalid");
            }

            if(calcIntToZonedTime(startDate).compareTo(calcIntToZonedTime(endDate)) > 0) {
                throw new IllegalArgumentException("range order is invalid");
            }
        }


    }
