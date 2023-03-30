package com.sample.clinic.Models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Bookings {

    String docID;
    String bookDate;

    String bookTime;
    String clientName;
    String medicalHistory;
    String address;
    String userID;
    String status;
    String hospitalDataRaw;
    int notifID;

    public Bookings(BookingsBuilder builder) {
        this.bookDate = builder.bookDate;
        this.bookTime = builder.bookTime;
        this.clientName  = builder.clientName;
        this.medicalHistory  = builder.medicalHistory;
        this.address  = builder.address;
        this.userID  = builder.userID;
        this.status  = builder.status;
        this.notifID = builder.notifID;
        this.hospitalDataRaw  = builder.hospitalDataRaw;
    }

    public static class BookingsBuilder {
        String clientName;
        String bookDate;
        String bookTime;
        String medicalHistory;
        String address;
        String userID;
        String status;

        String hospitalDataRaw;

        int notifID;


        public BookingsBuilder(String clientName) {
            this.clientName = clientName;
        }


        public BookingsBuilder setBookDate(String bookDate) {
            this.bookDate = bookDate;
            return this;
        }

        public BookingsBuilder setBookTime(String bookTime) {
            this.bookTime = bookTime;
            return this;
        }

        public BookingsBuilder setMedicalHistory(String medicalHistory) {
            this.medicalHistory = medicalHistory;
            return this;
        }

        public BookingsBuilder setAddress(String address) {
            this.address = address;
            return this;
        }

        public BookingsBuilder setUserID(String userID) {
            this.userID = userID;
            return this;
        }

        public BookingsBuilder setStatus(String status) {
            this.status = status;
            return this;
        }

        public BookingsBuilder setHospitalDataRaw(String hospitalDataRaw) {
            this.hospitalDataRaw = hospitalDataRaw;
            return this;
        }

        public BookingsBuilder setNotifID(int notifID) {
            this.notifID = notifID;
            return this;
        }

        public Bookings build() {
            return new Bookings(this);
        }
    }
}
