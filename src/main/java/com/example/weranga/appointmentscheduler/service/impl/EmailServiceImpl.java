package com.example.weranga.appointmentscheduler.service.impl;

import com.example.weranga.appointmentscheduler.entity.Appointment;
import com.example.weranga.appointmentscheduler.entity.ChatMessage;
import com.example.weranga.appointmentscheduler.entity.ExchangeRequest;
import com.example.weranga.appointmentscheduler.entity.Invoice;
import com.example.weranga.appointmentscheduler.entity.user.User;
import com.example.weranga.appointmentscheduler.service.EmailService;
import com.example.weranga.appointmentscheduler.util.PdfGeneratorUtil;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.*;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final JwtTokenServiceImpl jwtTokenService;
    private final PdfGeneratorUtil pdfGenaratorUtil;
    private final String baseUrl;

    public EmailServiceImpl(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine, JwtTokenServiceImpl jwtTokenService, PdfGeneratorUtil pdfGenaratorUtil, @Value("${base.url}") String baseUrl) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.jwtTokenService = jwtTokenService;
        this.pdfGenaratorUtil = pdfGenaratorUtil;
        this.baseUrl = baseUrl;
    }

    @Async
    @Override
    public void sendEmail(String to, String subject, String templateName, Context templateContext, File attachment) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            String html = templateEngine.process("email/" + templateName, templateContext);

            helper.setTo(to);
            helper.setFrom("ABC Laboratory<abclaboratory86@gmail.com>");
            helper.setSubject(subject);
            helper.setText(html, true);

            if (attachment != null) {
                helper.addAttachment("invoice", attachment);
            }

            javaMailSender.send(message);

        } catch (MessagingException e) {
            log.error("Error while adding attachment to email, error is {}", e.getLocalizedMessage());
        }



    }

    @Async
    @Override
    public void sendAppointmentFinishedNotification(Appointment appointment) {
        Context context = new Context();
        context.setVariable("appointment", appointment);
        context.setVariable("url", baseUrl + "/appointments/reject?token=" + jwtTokenService.generateAppointmentRejectionToken(appointment));
        sendEmail(appointment.getCustomer().getEmail(), "Finished appointment summary", "appointmentFinished", context, null);
    }

    @Async
    @Override
    public void sendAppointmentRejectionRequestedNotification(Appointment appointment) {
        Context context = new Context();
        context.setVariable("appointment", appointment);
        context.setVariable("url", baseUrl + "/appointments/acceptRejection?token=" + jwtTokenService.generateAcceptRejectionToken(appointment));
        sendEmail(appointment.getProvider().getEmail(), "Rejection requested", "appointmentRejectionRequested", context, null);
        SendMsg("Appoinment Rejection Request Sent By Customer");
    }

    @Async
    @Override
    public void sendNewAppointmentScheduledNotification(Appointment appointment) {
        Context context = new Context();
        context.setVariable("appointment", appointment);
        sendEmail(appointment.getProvider().getEmail(), "New appointment booked", "newAppointmentScheduled", context, null);
        SendMsg("New Appoinment Sheduled By Customer");

    }

    @Async
    @Override
    public void sendAppointmentCanceledByCustomerNotification(Appointment appointment) {
        Context context = new Context();
        context.setVariable("appointment", appointment);
        context.setVariable("canceler", "customer");
        sendEmail(appointment.getProvider().getEmail(), "Appointment canceled by Customer", "appointmentCanceled", context, null);
        SendMsg("An Appoinment Canceled By A Customer");
    }

    @Async
    @Override
    public void sendAppointmentCanceledByProviderNotification(Appointment appointment) {
        Context context = new Context();
        context.setVariable("appointment", appointment);
        context.setVariable("canceler", "provider");
        sendEmail(appointment.getCustomer().getEmail(), "Appointment canceled by Provider", "appointmentCanceled", context, null);
    }

    @Async
    @Override
    public void sendInvoice(Invoice invoice) {
        Context context = new Context();
        context.setVariable("customer", invoice.getAppointments().get(0).getCustomer().getFirstName() + " " + invoice.getAppointments().get(0).getCustomer().getLastName());
        try {
            File invoicePdf = pdfGenaratorUtil.generatePdfFromInvoice(invoice);
            sendEmail(invoice.getAppointments().get(0).getCustomer().getEmail(), "Appointment invoice", "appointmentInvoice", context, invoicePdf);
        } catch (Exception e) {
            log.error("Error while generating pdf, error is {}", e.getLocalizedMessage());
        }

    }

    @Async
    @Override
    public void sendAppointmentRejectionAcceptedNotification(Appointment appointment) {
        Context context = new Context();
        context.setVariable("appointment", appointment);
        sendEmail(appointment.getCustomer().getEmail(), "Rejection request accepted", "appointmentRejectionAccepted", context, null);
    }

    @Async
    @Override
    public void sendNewChatMessageNotification(ChatMessage chatMessage) {
        Context context = new Context();
        User recipent = chatMessage.getAuthor() == chatMessage.getAppointment().getProvider() ? chatMessage.getAppointment().getCustomer() : chatMessage.getAppointment().getProvider();
        context.setVariable("recipent", recipent);
        context.setVariable("appointment", chatMessage.getAppointment());
        context.setVariable("url", baseUrl + "/appointments/" + chatMessage.getAppointment().getId());
        sendEmail(recipent.getEmail(), "New chat message", "newChatMessage", context, null);
    }

    @Async
    @Override
    public void sendNewExchangeRequestedNotification(Appointment oldAppointment, Appointment newAppointment) {
        Context context = new Context();
        context.setVariable("oldAppointment", oldAppointment);
        context.setVariable("newAppointment", newAppointment);
        context.setVariable("url", baseUrl + "/appointments/" + newAppointment.getId());
        sendEmail(newAppointment.getCustomer().getEmail(), "New Appointment Exchange Request", "newExchangeRequest", context, null);
    }

    @Override
    public void sendExchangeRequestAcceptedNotification(ExchangeRequest exchangeRequest) {
        Context context = new Context();
        context.setVariable("exchangeRequest", exchangeRequest);
        context.setVariable("url", baseUrl + "/appointments/" + exchangeRequest.getRequested().getId());
        sendEmail(exchangeRequest.getRequested().getCustomer().getEmail(), "Exchange request accepted", "exchangeRequestAccepted", context, null);
    }

    @Override
    public void sendExchangeRequestRejectedNotification(ExchangeRequest exchangeRequest) {
        Context context = new Context();
        context.setVariable("exchangeRequest", exchangeRequest);
        context.setVariable("url", baseUrl + "/appointments/" + exchangeRequest.getRequestor().getId());
        sendEmail(exchangeRequest.getRequestor().getCustomer().getEmail(), "Exchange request rejected", "exchangeRequestRejected", context, null);
    }

    public static void SendMsg(String messageText) {

        String UserName = "" ;
        String Password = "" ;

        try {



            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create a connection to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/appointmentscheduler?useSSL=false&serverTimezone=Asia/Colombo", "root", "1234");

            // Prepare and execute the stored procedure
            try (CallableStatement statement = connection.prepareCall("{call GetTwilioCredentials()}")) {
                // Execute the stored procedure
                statement.execute();

                // Retrieve the results
                ResultSet resultSet = statement.getResultSet();
                if (resultSet.next()) {
                     UserName = resultSet.getString("UserName");
                     Password = resultSet.getString("Password");

                }
            }

            // Close the database connection
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }

        Twilio.init(UserName, Password);
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("+94764065868"),
                        new com.twilio.type.PhoneNumber("+15169906673"),
                        messageText)
                .create();


    }
}
