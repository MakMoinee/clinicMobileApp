package com.sample.clinic.Models;


import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message2 {
    String docID;
    String doctorName;
    String doctorID;
    String messageID;
    String userID;
    Date chatDateTime;
}
