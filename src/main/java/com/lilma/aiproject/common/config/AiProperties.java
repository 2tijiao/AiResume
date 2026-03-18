package com.lilma.aiproject.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.ai")
@Data
public class AiProperties {
    private String baseUrl;
    private String apiKey;
    private String model;
}
