package com.chegg.release.checklist.config;

import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@NonNull
@Component
@ConfigurationProperties("fields")
@PropertySource(value = "file:/apps/release-checklist/bin/cmc-template.yml", factory = YamlPropertySourceFactory.class)
public class CMCProperties {
    private String summary;
    private String description;
    private String label;
    private String customfield_12503;
    private String customfield_12507;
    private String customfield_14700;
    private String customfield_14701;
}
