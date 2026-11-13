package com.thomascook.hotel.service;

import com.thomascook.hotel.entity.Booking;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    /**
     * Send professional HTML booking confirmation email in background without blocking transaction.
     */
    public void sendBookingConfirmation(Booking booking) {
        if (mailSender == null) {
            log.warn("JavaMailSender is not configured. Skipping confirmation email dispatch.");
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(senderEmail, "ThomasCook Hotel Reservations");
                helper.setTo(booking.getGuestEmail());
                helper.setSubject("Booking Confirmed: " + booking.getReference() + " — " + booking.getHotel().getName());

                String htmlContent = buildHtmlContent(booking);
                helper.setText(htmlContent, true);

                mailSender.send(message);
                log.info("Successfully sent booking confirmation email for ref [{}] to [{}]",
                        booking.getReference(), booking.getGuestEmail());
            } catch (Exception e) {
                log.error("Failed to send booking confirmation email for ref [{}]: {}",
                        booking.getReference(), e.getMessage(), e);
            }
        });
    }

    private String buildHtmlContent(Booking booking) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset='UTF-8'></head>"
                + "<body style='font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif; background-color: #F6F1E9; margin: 0; padding: 24px; color: #16130F;'>"
                + "<div style='max-width: 600px; margin: 0 auto; background: #FFFFFF; border-radius: 8px; border: 1px solid #DCD4C5; overflow: hidden; box-shadow: 0 4px 16px rgba(0,0,0,0.06);'>"
                + "<div style='background: #16130F; padding: 24px; text-align: center; color: #FAF7F2;'>"
                + "<h1 style='margin: 0; font-size: 22px; font-weight: 700; letter-spacing: -0.02em;'>Grand Tour Hotels</h1>"
                + "<p style='margin: 4px 0 0 0; font-size: 12px; color: #E8633B; font-weight: 600; letter-spacing: 0.1em;'>OFFICIAL DIGITAL BOARDING PASS</p>"
                + "</div>"
                + "<div style='padding: 32px 28px;'>"
                + "<h2 style='margin-top: 0; font-size: 20px; color: #16130F;'>Reservation Confirmed!</h2>"
                + "<p style='color: #5C5750; font-size: 14px; line-height: 1.5;'>Dear <strong>" + booking.getGuestName() + "</strong>,<br>Your room has been locked and confirmed. Below are your stay details:</p>"
                + "<div style='background: #FAF7F2; border: 1px solid #E8E2D7; border-radius: 6px; padding: 18px; margin: 20px 0;'>"
                + "<table style='width: 100%; border-collapse: collapse; font-size: 14px;'>"
                + "<tr><td style='padding: 6px 0; color: #8E8880;'>Booking Reference:</td><td style='padding: 6px 0; font-weight: 700; font-family: monospace; font-size: 16px; color: #E4572E; text-align: right;'>" + booking.getReference() + "</td></tr>"
                + "<tr><td style='padding: 6px 0; color: #8E8880;'>Hotel:</td><td style='padding: 6px 0; font-weight: 600; text-align: right;'>" + booking.getHotel().getName() + "</td></tr>"
                + "<tr><td style='padding: 6px 0; color: #8E8880;'>Location:</td><td style='padding: 6px 0; font-weight: 600; text-align: right;'>" + booking.getHotel().getLocation() + ", India</td></tr>"
                + "<tr><td style='padding: 6px 0; color: #8E8880;'>Check-In:</td><td style='padding: 6px 0; font-weight: 600; text-align: right;'>" + booking.getCheckIn() + " (After 14:00)</td></tr>"
                + "<tr><td style='padding: 6px 0; color: #8E8880;'>Check-Out:</td><td style='padding: 6px 0; font-weight: 600; text-align: right;'>" + booking.getCheckOut() + " (Before 11:00)</td></tr>"
                + "<tr><td style='padding: 6px 0; color: #8E8880;'>Duration:</td><td style='padding: 6px 0; font-weight: 600; text-align: right;'>" + booking.getNights() + " Nights</td></tr>"
                + "<tr style='border-top: 1px solid #DCD4C5;'><td style='padding: 10px 0 0 0; font-weight: 700; color: #16130F;'>Total Amount:</td><td style='padding: 10px 0 0 0; font-weight: 700; font-size: 18px; color: #16130F; text-align: right;'>₹" + booking.getTotalAmount() + "</td></tr>"
                + "</table>"
                + "</div>"
                + "<p style='font-size: 13px; color: #716B64; line-height: 1.5;'>Please present this reference number or print your digital boarding pass upon arrival at the concierge.</p>"
                + "</div>"
                + "<div style='background: #FAF7F2; padding: 16px; text-align: center; font-size: 11px; color: #8E8880; border-top: 1px solid #E8E2D7;'>"
                + "ThomasCook Hotel Reservations · Automated Booking Dispatch"
                + "</div>"
                + "</div>"
                + "</body></html>";
    }
}

