package com.stc.service;

import com.stc.entity.Employee;
import com.stc.exception.EmailSendingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final ISpringTemplateEngine templateEngine;
    @Value("${admin.email}")
    private String adminEmail;

    @Async
    @Retryable(
            value = EmailSendingException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void sendEmployeeCreationNotification(Employee employee) {
        try {
            Context context = new Context();
            context.setVariable("firstName", employee.getFirstName());
            context.setVariable("lastName", employee.getLastName());
            context.setVariable("email", employee.getEmail());
            context.setVariable("department", employee.getDepartment());
            context.setVariable("salary", employee.getSalary());

            String subject = "Welcome to the Company, " + employee.getFirstName() + " " + employee.getLastName();
            String body = templateEngine.process("email-template", context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("your-email@example.com");
            helper.setTo(employee.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send email: " + e.getMessage());
        }
    }

    @Recover
    public void fallbackEmailSending(EmailSendingException e, Employee employee) {
        log.error("Email sending failed after retries for employee: {}. Notifying admin.", employee.getEmail(), e);
        notifyAdmin(employee, e.getMessage());
    }

    private void notifyAdmin(Employee employee, String errorMessage) {
        try {
            SimpleMailMessage adminMessage = new SimpleMailMessage();
            adminMessage.setFrom("your-email@example.com");
            adminMessage.setTo(adminEmail);
            adminMessage.setSubject("Email Sending Failure Notification");
            adminMessage.setText(String.format("""
                Failed to send email to employee:
                Name: %s %s
                Email: %s
                Department: %s
                Salary: %.2f

                Error: %s
                """,
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getEmail(),
                    employee.getDepartment(),
                    employee.getSalary(),
                    errorMessage
            ));
            javaMailSender.send(adminMessage);
        } catch (Exception ex) {
            log.error("Failed to notify admin about email sending failure.", ex);
        }
    }
}
