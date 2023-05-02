package com.sample.clinic.Services;

import android.util.Log;

import com.sample.clinic.Common.Constants;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class LocalMail {

    public LocalMail() {
    }

    public void sendEmail(String emailTo, String subject, String msg) {
        try {
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", Constants.emailHost);
            properties.put("mail.smtp.socketFactory.port", Constants.emailPort);
            properties.put("mail.smtp.socketFactory.class", Constants.emailClass);
            properties.put("mail.smtp.auth", Constants.emailAuth);


            Session mailSession = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Constants.emailFrom, Constants.emailPass);
                }
            });

            MimeMessage message = new MimeMessage(mailSession);

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
            message.setSubject(subject);
            message.setText(msg);

            Thread thread = new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (MessagingException e) {
                    Log.e("MessagingException >>", e.getLocalizedMessage());
                }
            });
//
            thread.start();


        } catch (AddressException e) {
            Log.e("AddressException >>", e.getLocalizedMessage());
        } catch (MessagingException e) {
            Log.e("MessagingException >>", e.getLocalizedMessage());
        }
    }
}
