package com.stc.service;

import com.stc.config.AppConfig;
import com.stc.dto.EmailValidationResponse;
import com.stc.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailValidationService {

    private static final String API_URL = "https://api.zerobounce.net/v2/validate?api_key=%s&email=%s";
    private final AppConfig appConfig;
    @Value("${email.validation.api.key}")
    private String apiKey;
    private final RestTemplate restTemplate;

    public boolean validateEmail(String email) {
        int attempts = 3;
        while (attempts-- > 0) {
            try {
                String url = String.format(API_URL, apiKey, email);
                EmailValidationResponse response = restTemplate.getForObject(url, EmailValidationResponse.class);
                assert response != null;
                return "valid".equalsIgnoreCase(response.status());
            } catch (Exception e) {
                if (attempts == 0) {
                    throw new ExternalServiceException("Email validation failed after retries", e);
                }
                try {
                    TimeUnit.SECONDS.sleep((long) Math.pow(2, 3 - attempts)); // Exponential backoff
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false;
    }
}
