package com.sample.clinic.Models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Appointment {
    String docID;
    String doctorName;
    String doctorID;
    String userID;
    String bookDateTime;
    int notifID;

    public Appointment(AppointmentBuilder builder) {
        this.docID = builder.docID;
        this.doctorName = builder.doctorName;
        this.doctorID = builder.doctorID;
        this.userID = builder.userID;
        this.bookDateTime = builder.bookDateTime;
        this.notifID = builder.notifID;
    }

    public static class AppointmentBuilder {
        String docID;
        String doctorName;
        String doctorID;
        String userID;
        String bookDateTime;
        int notifID;


        public AppointmentBuilder() {

        }

        public AppointmentBuilder setDocID(String docID) {
            this.docID = docID;
            return this;
        }

        public AppointmentBuilder setDoctorName(String doctorName) {
            this.doctorName = doctorName;
            return this;
        }

        public AppointmentBuilder setDoctorID(String doctorID) {
            this.doctorID = doctorID;
            return this;
        }

        public AppointmentBuilder setUserID(String userID) {
            this.userID = userID;
            return this;
        }

        public AppointmentBuilder setBookDateTime(String bookDateTime) {
            this.bookDateTime = bookDateTime;
            return this;
        }

        public AppointmentBuilder setNotifID(int notifID) {
            this.notifID = notifID;
            return this;
        }

        public Appointment build(){
            return new Appointment(this);
        }
    }
}
