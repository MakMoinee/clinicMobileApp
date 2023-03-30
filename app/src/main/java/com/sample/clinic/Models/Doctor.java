package com.sample.clinic.Models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Doctor {
    String docID;
    String doctorName;
    String specialize;
    String address;

    String contactNumber;

    public Doctor(DoctorBuilder builder) {
        this.docID = builder.docID;
        this.doctorName = builder.doctorName;
        this.specialize = builder.specialize;
        this.address = builder.address;
        this.contactNumber = builder.contactNumber;
    }


    public static class DoctorBuilder {
        String docID;
        String doctorName;
        String specialize;
        String address;

        String contactNumber;

        public DoctorBuilder setDocID(String docID) {
            this.docID = docID;
            return this;
        }

        public DoctorBuilder setDoctorName(String doctorName) {
            this.doctorName = doctorName;
            return this;
        }

        public DoctorBuilder setSpecialize(String specialize) {
            this.specialize = specialize;
            return this;
        }

        public DoctorBuilder setAddress(String address) {
            this.address = address;
            return this;
        }

        public DoctorBuilder setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
            return this;
        }

        public Doctor build() {
            return new Doctor(this);
        }
    }
}
