package com.example.project_spring.security.jwt;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class JwtProperties {
    private String secretKey = "DemoApiCoreSecretKeyThatIsLongEnoughToPassValidation";

    //validity in milliseconds
    private long validityInMs = 3600000 * 12; // 12h

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getValidityInMs() {
        return validityInMs;
    }

    public void setValidityInMs(long validityInMs) {
        this.validityInMs = validityInMs;
    }
}
