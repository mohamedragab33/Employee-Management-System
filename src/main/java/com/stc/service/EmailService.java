package com.stc.service;

import com.stc.entity.Employee;
import com.stc.exception.EmailSendingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final ISpringTemplateEngine templateEngine;

    @Async
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
}
