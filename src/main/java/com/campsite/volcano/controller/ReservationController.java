package com.campsite.volcano.controller;

import com.campsite.volcano.repository.ReserationRepositoryImpl;
import com.campsite.volcano.service.ReservationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class ReservationController {

    Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationServiceImpl reservationService;

    @Autowired
    public ReservationController(ReservationServiceImpl reservationService) {
        this.reservationService = reservationService;
    }

    // availabilities from today
    @GetMapping("availabilities")
    @ResponseBody
    public List<Integer> availabilities() {
        try {
            logger.info("availabilities : " + reservationService.getAvailabilities());
            return reservationService.getAvailabilities();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
        }
    }

    // availabilities from startDate
    @GetMapping("availabilities-with-startdate")
    @ResponseBody
    public List<Integer> availabilities(@RequestParam(value = "startDate", required = true) int startDate) {
        try {
            logger.info("availabilities : " +  reservationService.getAvailabilities(startDate));
            return reservationService.getAvailabilities(startDate);
        } catch (IllegalArgumentException ex) {
            logger.error(ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }


    // availabilities from startDate to endDate
    @GetMapping("availabilities-with-range")
    @ResponseBody
    public List<Integer> availabilities(@RequestParam(value = "startDate", required = true) int startDate
            , @RequestParam(value = "endDate", required = true) int endDate) {
        try {
            logger.info("availabilities : " + reservationService.getAvailabilities(startDate, endDate));
            return reservationService.getAvailabilities(startDate, endDate);
        } catch (IllegalArgumentException ex) {
            logger.error(ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }




    @PostMapping("make-reservation")
    @ResponseBody
    public String makeReservation(@RequestParam(value = "userEmail", required = true) String userEmail
            , @RequestParam(value = "startDate", required = true) int startDate
            , @RequestParam(value = "endDate", required = true) int endDate) {
        try {
            long reservationId = reservationService.addReservation(userEmail, startDate, endDate);
            logger.info("Reservation ID: " + String.valueOf(reservationId));
            return "Reservation ID: " + reservationId;
        } catch (IllegalArgumentException ex) {
            logger.error(ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("change-reservation")
    @ResponseBody
    public String changeReservation(long reservationId, String userEmail, int startDate, int endDate) {
        try {
            long newReservationId = reservationService.changeReservation(reservationId, userEmail, startDate, endDate);
            return "New Reservation ID: " + reservationId;
        } catch (IllegalArgumentException ex) {
            logger.error(ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("cancel-reservation")
    @ResponseBody
    public String cancelReservation(@RequestParam(value = "reservationId", required = true) long reservationId
            ,@RequestParam(value = "userEmail", required = true) String userEmail) {
        try {
            boolean cancelResult = reservationService.cancelReservation(reservationId, userEmail);
            return cancelResult? "Cancelled":"Not Cancelled";
        } catch (IllegalArgumentException ex) {
            logger.error(ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}
