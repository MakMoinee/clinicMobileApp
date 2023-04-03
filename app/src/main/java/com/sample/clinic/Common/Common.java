package com.sample.clinic.Common;

import com.sample.clinic.Models.Appointment;
import com.sample.clinic.Models.Bookings;
import com.sample.clinic.Models.Buildings;
import com.sample.clinic.Models.Doctor;
import com.sample.clinic.Models.Message;
import com.sample.clinic.Models.Users;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Common {

    public static Map<String, Object> toLoginMaps(Users users) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", users.getEmail());
        params.put("password", users.getPassword());
        params.put("firstName", users.getFirstName());
        params.put("middleName", users.getMiddleName());
        params.put("lastName", users.getLastName());
        params.put("secret", users.getSecret());
        params.put("userType", users.getUserType());
        return params;
    }

    public static Map<String, Object> toBuildingMaps(Buildings buildings) {
        Map<String, Object> params = new HashMap<>();
        params.put("buildingName", buildings.getBuildingName());
        params.put("description", buildings.getDescription());
        params.put("picturePath", buildings.getPicturePath());
        params.put("videoPath", buildings.getVideoPath());
        return params;
    }

    public static Map<String, Object> getBookMap(Bookings bookings) {
        Map<String, Object> map = new HashMap<>();
        map.put("bookDate", bookings.getBookDate());
        map.put("bookTime", bookings.getBookTime());
        map.put("clientName", bookings.getClientName());
        map.put("address", bookings.getAddress());
        map.put("userID", bookings.getUserID());
        map.put("status", bookings.getStatus());
        map.put("notifID", bookings.getNotifID());
        map.put("hospitalDataRaw", bookings.getHospitalDataRaw());
        return map;
    }

    public static Map<String, Object> getDoctorMap(Doctor doctor) {
        Map<String, Object> map = new HashMap<>();
        map.put("doctorName", doctor.getDoctorName());
        map.put("specialize", doctor.getSpecialize());
        map.put("address", doctor.getAddress());
        map.put("contactNumber", doctor.getContactNumber());

        return map;
    }

    public static Map<String, Object> getAppointmentMap(Appointment appointment) {
        Map<String, Object> map = new HashMap<>();
        map.put("doctorName", appointment.getDoctorName());
        map.put("bookDateTime", appointment.getBookDateTime());
        map.put("userID", appointment.getUserID());
        map.put("doctorID", appointment.getDoctorID());
        map.put("notifID", appointment.getNotifID());
        return map;
    }

    public static Map<String, Object> getMessageMap(Message message, String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("messageID", message.getMessageID());
        map.put("userID", message.getUserID());
        map.put("doctorName", name);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        map.put("chatDateTime", timestamp);
        return map;
    }

    public static Map<String, Object> getMsgMap(Message message) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("messageID", message.getMessageID());
        map.put("userID", message.getUserID());
        map.put("recipientID", message.getRecipientUserID());
        map.put("messages", message.getMessages());

        return map;
    }
}
