package org.dummy.clearing.house.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Component
public class ComplianceJsonLoader {

    @Value("${compliance-json.file-path}")
    private String filePath;

    private JsonNode complianceServiceJson;


    @Bean
    @PostConstruct
    public void init() throws IOException {
        File jsonFile = ResourceUtils.getFile(filePath);
        ObjectMapper jsonMapper = new ObjectMapper();
        complianceServiceJson = jsonMapper.readValue(jsonFile, JsonNode.class);
    }

    public JsonNode getComplianceServiceJson() {
        return complianceServiceJson;
    }
}
