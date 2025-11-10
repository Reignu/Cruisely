package com.cruisely.services;

import com.cruisely.exceptions.EmailServiceException;
import com.cruisely.utils.PropertiesReader;
import com.cruisely.utils.interceptors.TrackingInterceptor;

import javax.interceptor.Interceptors;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static com.cruisely.common.I18n.EMAIL_SERVICE_INACCESSIBLE;
import static com.cruisely.common.I18n.EMAIL_SERVICE_INCORRECT_EMAIL;

/**
 * The type Email service.
 */
@Interceptors(TrackingInterceptor.class)
public class EmailService {

    private static final Properties emailProperties = PropertiesReader.getSecurityProperties();
    private static String EMAIL_USER = getAccount();
    private static String PASSWD = getPasswd();


    /**
     * Sends email for the specified user
     *
     * @param recipientEmail recipient's email
     * @param subject        subject
     * @param contentHtml    HTML content
     * @throws EmailServiceException exception thrown when email is invalid or when connection problem to SMTP service occurs
     */
    public static void sendEmailWithContent(String recipientEmail, String subject, String contentHtml) throws EmailServiceException {

        Properties properties = System.getProperties();

        String host = "smtp.gmail.com";
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", host);
        properties.put("mail.smtp.user", EMAIL_USER);
        properties.put("mail.smtp.password", PASSWD);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setFrom(new InternetAddress(EMAIL_USER));

            message.setSubject(subject, "UTF-8");
            message.setContent(contentHtml, "text/html; charset=UTF-8");

            Transport transport = session.getTransport("smtp");

            transport.connect(host, EMAIL_USER, PASSWD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (AddressException ae) {
            throw new EmailServiceException(EMAIL_SERVICE_INCORRECT_EMAIL);
        } catch (MessagingException me) {
            throw new EmailServiceException(EMAIL_SERVICE_INACCESSIBLE);
        }
    }

    /**
     * Sends mail for the specified recipients
     *
     * @param recipients list of recipient emails
     * @param subject    email subject
     * @param body       email content
     */
    public static void sendFromGMail(String[] recipients, String subject, String body) {
        Properties properties = System.getProperties();

        String host = "smtp.gmail.com";
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", host);
        properties.put("mail.smtp.user", EMAIL_USER);
        properties.put("mail.smtp.password", PASSWD);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(EMAIL_USER));
            InternetAddress[] toAddress = new InternetAddress[recipients.length];

            // To get the array of addresses
            for (int i = 0; i < recipients.length; i++) {
                toAddress[i] = new InternetAddress(recipients[i]);
            }

            for (int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            message.setSubject(subject);
            message.setContent(body, "text/html; charset=UTF-8");

            Transport transport = session.getTransport("smtp");

            transport.connect(host, EMAIL_USER, PASSWD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (AddressException ae) {
            ae.printStackTrace();
        } catch (MessagingException me) {
            me.printStackTrace();
        }
    }

    private static String getAccount() {
        return emailProperties.getProperty("email.user");
    }

    private static String getPasswd() {
        return emailProperties.getProperty("email.passwd");
    }
}