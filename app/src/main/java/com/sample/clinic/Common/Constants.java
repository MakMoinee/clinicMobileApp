package com.sample.clinic.Common;

import com.sample.clinic.Models.NearPlacesResponse;
import com.sample.clinic.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
    public static final String storageBucket = "gs://saankapuntaapp.appspot.com";
    public static final String firebaseStore = "https://firebasestorage.googleapis.com/v0/b/saankapuntaapp.appspot.com/o/";
    public static final String buildingFileFolder = "Dr. Josefina Estolas Building,Dr. Lydia M. Profeta Building,Main Academic Building,Old Building,Research And Development Building,Sen. Nepatali A. Gonzales Academic Hall,Wellness And Health Building";
    public static final String buildingPath = "Buildings/";
    public static final String postersPath = "Posters/";
    public static final String virtualGuidePath = "Virtual Guides/";

    public static final String photoURL = "https://maps.googleapis.com/maps/api/place/photo";

    public static List<NearPlacesResponse> nearbyHospitals = new ArrayList<>();
    public static final String getHospitalNearMeURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    public static final String gmapKey = "AIzaSyB5aCq16HGA-S2uhBvYitXPuwyKNAng7Vo";

    public static Map<String, String> getBuildingMaps() {
        Map<String, String> buildingMaps = new HashMap<>();

        buildingMaps.put("Faculty Office of the Department of Electrical Engineering", "Dr. Josefina Estolas Building");
        buildingMaps.put("Faculty Office of the Department of Engineering", "Dr. Josefina Estolas Building");
        buildingMaps.put("Dean’s Office – College of Engineering, Architecture and Technology", "Dr. Josefina Estolas Building");
        buildingMaps.put("Alumni Office", "Dr. Josefina Estolas Building");
        buildingMaps.put("ECE Department Office", "Dr. Josefina Estolas Building");

        buildingMaps.put("Management Information Center", "Dr. Lydia M. Profeta Building");
        buildingMaps.put("Office of the Vice-President of Academic Affairs", "Dr. Lydia M. Profeta Building");
        buildingMaps.put("Office of the Director of the Finance Services", "Dr. Lydia M. Profeta Building");
        buildingMaps.put("Cashier Window", "Dr. Lydia M. Profeta Building");
        buildingMaps.put("Cashier Staff’s Office", "Dr. Lydia M. Profeta Building");
        buildingMaps.put("Office of the VPSS", "Dr. Lydia M. Profeta Building");
        buildingMaps.put("Office of the VPDA", "Dr. Lydia M. Profeta Building");
        buildingMaps.put("Office the President", "Dr. Lydia M. Profeta Building");
        buildingMaps.put("COA", "Dr. Lydia M. Profeta Building");
        buildingMaps.put("Accounting Office", "Dr. Lydia M. Profeta Building");
        buildingMaps.put("Office of the Department for Administration Services", "Dr. Lydia M. Profeta Building");
        buildingMaps.put("Department of Instrumentation and Control Engineering", "Dr. Lydia M. Profeta Building");

        buildingMaps.put("Scholarship &amp; Grant Office", "Main Academic Building");
        buildingMaps.put("Center for Students Affairs", "Main Academic Building");
        buildingMaps.put("Registrar", "Main Academic Building");
        buildingMaps.put("DOST Office", "Main Academic Building");
        buildingMaps.put("Records Management Office", "Main Academic Building");
        buildingMaps.put("College of Arts and Sciences Faculty Office", "Main Academic Building");
        buildingMaps.put("College of Arts and Sciences Reading Center", "Main Academic Building");


        buildingMaps.put("RTU Co-Op", "Old Building");
        buildingMaps.put("Food Kiosk", "Old Building");
        buildingMaps.put("ROTC Office", "Old Building");
        buildingMaps.put("Office of the Director Department of Extension Expenses", "Old Building");
        buildingMaps.put("Clinic", "Old Building");

        buildingMaps.put("President Emeritus Office", "Research And Development Building");
        buildingMaps.put("Bids and Awards Committee Office", "Research And Development Building");
        buildingMaps.put("Astronomy Hall", "Research And Development Building");
        buildingMaps.put("Astronomy Office", "Research And Development Building");
        buildingMaps.put("Astronomy Library", "Research And Development Building");
        buildingMaps.put("Conference Blue Room", "Research And Development Building");
        buildingMaps.put("RND Conference Room", "Research And Development Building");
        buildingMaps.put("VP Research Office", "Research And Development Building");
        buildingMaps.put("VP Research Extension Services Office", "Research And Development Building");
        buildingMaps.put("GSSAI Office", "Research And Development Building");
        buildingMaps.put("Gender and Development Office", "Research And Development Building");
        buildingMaps.put("International Relations and Grants Office", "Research And Development Building");
        buildingMaps.put("Accreditation Room", "Research And Development Building");
        buildingMaps.put("Graduate School Library", "Research And Development Building");
        buildingMaps.put("GS Research &amp; Development Extension Services", "Research And Development Building");
        buildingMaps.put("Institute of Human Kinetics Research Office", "Research And Development Building");
        buildingMaps.put("Institute of Human Kinetics Faculty Office", "Research And Development Building");
        buildingMaps.put("Graduate School – Information Technology Center", "Research And Development Building");
        buildingMaps.put("Office of the Director of Astronomy Research", "Research And Development Building");

        buildingMaps.put("Main Library", "Sen. Nepatali A. Gonzales Academic Hall");
        buildingMaps.put("Information Technology Library", "Sen. Nepatali A. Gonzales Academic Hall");
        buildingMaps.put("Information Technology Department", "Sen. Nepatali A. Gonzales Academic Hall");
        buildingMaps.put("Laboratory High School Faculty", "Sen. Nepatali A. Gonzales Academic Hall");
        buildingMaps.put("College of Education Department Head Office", "Sen. Nepatali A. Gonzales Academic Hall");
        buildingMaps.put("College of Education Faculty Office", "Sen. Nepatali A. Gonzales Academic Hall");
        buildingMaps.put("Senior High School Faculty Office", "Sen. Nepatali A. Gonzales Academic Hall");

        buildingMaps.put("Guidance Office", "Wellness And Health Building");
        buildingMaps.put("DRRMO Office", "Wellness And Health Building");
        buildingMaps.put("Counseling Office", "Wellness And Health Building");
        buildingMaps.put("Office of Student Affairs", "Wellness And Health Building");
        buildingMaps.put("Medical and Dental SVCS Center", "Wellness And Health Building");
        buildingMaps.put("Sports Development Wellness Center", "Wellness And Health Building");
        buildingMaps.put("Internal Linkages and External Affairs Center", "Wellness And Health Building");
        buildingMaps.put("CBET Dean’s Office", "Wellness And Health Building");
        buildingMaps.put("CBET Department", "Wellness And Health Building");
        buildingMaps.put("Guidance and Counselling Services Center", "Wellness And Health Building");
        buildingMaps.put("Cultural Affairs", "Wellness And Health Building");
        buildingMaps.put("RTU Himig Rizalia", "Wellness And Health Building");
        buildingMaps.put("Tunog Rizalia Rondalla", "Wellness And Health Building");
        buildingMaps.put("Badminton Court", "Wellness And Health Building");
        buildingMaps.put("Table Tennis Court", "Wellness And Health Building");
        buildingMaps.put("Board Games Court", "Wellness And Health Building");
        buildingMaps.put("Swimming Pool", "Wellness And Health Building");
        buildingMaps.put("Basketball Court", "Wellness And Health Building");

        return buildingMaps;
    }

    public static Map<String, Integer> getLocationsMap() {
        Map<String, Integer> buildingMaps = new HashMap<>();
        buildingMaps.put("Office of the Vice-President of Academic Affairs", R.string.locations_office_of_the_vice_president_of_academic_affairs);
        buildingMaps.put("College of Education Faculty Office", R.string.locations_college_of_education_faculty_office);


        buildingMaps.put("Cashier Window", R.string.locations_cashier_window);
        buildingMaps.put("Cashier Staff’s Office", R.string.locations_cashier_window);

        buildingMaps.put("Registrar", R.string.locations_registrar);
        buildingMaps.put("College of Arts and Sciences Faculty Office", R.string.locations_cas_faculty);

        buildingMaps.put("Dean’s Office – College of Engineering, Architecture and Technology", R.string.locations_college_of_engineering_arch_and_tech);
        return buildingMaps;
    }

    public static Map<String, Integer> getDirectionsMap() {
        Map<String, Integer> buildingMaps = new HashMap<>();
        buildingMaps.put("Office of the Vice-President of Academic Affairs", R.string.directions_office_of_the_vice_president_of_academic_affairs);
        buildingMaps.put("College of Education Faculty Office", R.string.directions_college_of_education_faculty_office);

        buildingMaps.put("Cashier Window", R.string.directions_cashier_window);
        buildingMaps.put("Cashier Staff’s Office", R.string.directions_cashier_window);

        buildingMaps.put("Registrar", R.string.directions_registrar);
        buildingMaps.put("College of Arts and Sciences Faculty Office", R.string.directions_cas_faculty);

        buildingMaps.put("Dean’s Office – College of Engineering, Architecture and Technology", R.string.directions_college_of_engineering_arch_and_tech);


        return buildingMaps;
    }

    public static Map<String, String> getFloorMap() {
        Map<String, String> floorMaps = new HashMap<>();
        floorMaps.put("Office of the Vice-President of Academic Affairs", "https://github.com/MakMoinee/saanKaPuntaApp/blob/main/buildings/Dr.%20Lydia%20M.%20Profeta%20Building/1st%20floor%20-%20Profeta.jpg?raw=true");
        floorMaps.put("College of Education Faculty Office", "https://github.com/MakMoinee/saanKaPuntaApp/blob/main/buildings/Sen.%20Nepatali%20A.%20Gonzales%20Academic%20Hall/8th%20Floor%20-%20SNAGAH.jpg?raw=true");
        floorMaps.put("Cashier Window", "https://github.com/MakMoinee/saanKaPuntaApp/blob/main/buildings/Dr.%20Josefina%20Estolas%20Building/1st%20Floor.PNG?raw=true");
        floorMaps.put("Cashier Staff’s Office", "https://github.com/MakMoinee/saanKaPuntaApp/blob/main/buildings/Dr.%20Josefina%20Estolas%20Building/1st%20Floor.PNG?raw=true");
        floorMaps.put("Registrar", "https://github.com/MakMoinee/saanKaPuntaApp/blob/main/buildings/Main%20Academic%20Building/Ground%20Floor.PNG?raw=true");
        floorMaps.put("College of Arts and Sciences Faculty Office", "https://github.com/MakMoinee/saanKaPuntaApp/blob/main/buildings/Main%20Academic%20Building/2nd%20Floor.PNG?raw=true");
        floorMaps.put("Dean’s Office – College of Engineering, Architecture and Technology", "https://github.com/MakMoinee/saanKaPuntaApp/blob/main/buildings/Dr.%20Josefina%20Estolas%20Building/2nd%20Floor.PNG?raw=true");
        return floorMaps;
    }

    public static Map<String, String> getVirtualGuideMap() {
        Map<String, String> virtualGuideMaps = new HashMap<>();
        virtualGuideMaps.put("Office of the Vice-President of Academic Affairs", "https://elako.net/videos/Office%20of%20the%20Vice%20President%20of%20Academic%20Affairs.mp4");
        virtualGuideMaps.put("College of Education Faculty Office", "https://elako.net/videos/College%20of%20Education%20Faculty%20Office.mp4");//
        virtualGuideMaps.put("Cashier Staff’s Office", "https://firebasestorage.googleapis.com/v0/b/saankapuntaapp.appspot.com/o/Virtual%20Guides%2FDr.%20Lydia%20M.%20Profeta%20Building%2FCashier.mp4?alt=media&token=3a1a5260-6613-4714-8c0b-0f0f4a479b95");//
        virtualGuideMaps.put("Cashier Window", "https://firebasestorage.googleapis.com/v0/b/saankapuntaapp.appspot.com/o/Virtual%20Guides%2FDr.%20Lydia%20M.%20Profeta%20Building%2FCashier.mp4?alt=media&token=3a1a5260-6613-4714-8c0b-0f0f4a479b95");//
        virtualGuideMaps.put("Registrar", "https://firebasestorage.googleapis.com/v0/b/saankapuntaapp.appspot.com/o/Virtual%20Guides%2FMain%20Academic%20Building%2FRegistrar.mp4?alt=media&token=6151b00b-d080-47eb-bf92-9a5d9a18bce7");//
        virtualGuideMaps.put("College of Arts and Sciences Faculty Office", "https://firebasestorage.googleapis.com/v0/b/saankapuntaapp.appspot.com/o/Virtual%20Guides%2FMain%20Academic%20Building%2FCAS%20Faculty.mp4?alt=media&token=a01f3dcb-b272-462a-ad1e-595746e2f7ad");//
        virtualGuideMaps.put("Dean’s Office – College of Engineering, Architecture and Technology", "https://firebasestorage.googleapis.com/v0/b/saankapuntaapp.appspot.com/o/Virtual%20Guides%2FDr.%20Josefina%20Estolas%20Building%2FCEAT%20Dean%20Office.mp4?alt=media&token=d03b3734-307e-4f59-a952-e800d863aa1d");//

        return virtualGuideMaps;
    }

    public static Map<String, String> getDirectoryMap() {
        Map<String, String> directoryMap = new HashMap<>();
        directoryMap.put("Main Academic Building", "https://github.com/MakMoinee/saanKaPuntaApp/blob/main/buildings/Main%20Academic%20Building/directory.png?raw=true");
        directoryMap.put("Wellness And Health Building", "https://github.com/MakMoinee/saanKaPuntaApp/blob/main/directories/Dr.%20Lydia%20M.%20Profeta%20Bldg%20Directory.png?raw=true");
        directoryMap.put("Dr. Lydia M. Profeta Building", "https://github.com/MakMoinee/saanKaPuntaApp/blob/main/directories/Dr.%20Lydia%20M.%20Profeta%20Bldg%20Directory.png?raw=true");
        directoryMap.put("Sen. Nepatali A. Gonzales Academic Hall", "https://github.com/MakMoinee/saanKaPuntaApp/blob/main/directories/SNAGAH%20directory.png?raw=true");
        directoryMap.put("Dr. Josefina Estolas Building", "https://github.com/MakMoinee/saanKaPuntaApp/blob/main/buildings/Dr.%20Josefina%20Estolas%20Building/directory%20estolas.png?raw=true");
        return directoryMap;
    }

}
