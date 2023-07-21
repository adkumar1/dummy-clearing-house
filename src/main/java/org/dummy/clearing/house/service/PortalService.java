package org.dummy.clearing.house.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.dummy.clearing.house.dto.PortalDto;
import org.dummy.clearing.house.proxy.PortalProxy;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PortalService {

    @Autowired
    private PortalProxy portalProxy;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private ComplianceJsonLoader complianceJsonLoader;

    @Autowired
    private ObjectMapper mapper;


    public void sendDataToPortal(Map<String, Object> verifiableCredentialDto,
                                 String externalId) {
        // Send response back to Portal using feign
        String token = "Bearer "+tokenManager.getAccessTokenString();

        PortalDto portalDto = createPortalRequestObejct(externalId,
                verifiableCredentialDto,
                complianceJsonLoader.getComplianceServiceJson());
        ResponseEntity<String> res;
        List<Object> typeList = (List<Object>) verifiableCredentialDto.get("type");
        if  (typeList.contains("LegalParticipant")) {
            res = portalProxy.postPortalLegalPersonResponse(portalDto, token);
            log.info("Response from portal: " + res.getStatusCode());
        } else if (typeList.contains("ServiceOffering")) {
            res = portalProxy.postPortalServiceOfferingResponse(portalDto, token);
            log.info("Response from portal: " + res.getStatusCode());
        }

    }

    private PortalDto createPortalRequestObejct(String externalId,
                                                Map<String, Object> verifiableCredentialDto,
                                                JsonNode complianceJsonNode) {
        String sdDocumentDtoString = "";
        try {

            JsonNode vcNode = mapper.readTree(mapper.writeValueAsString(verifiableCredentialDto));
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.set("selfDescriptionCredential",vcNode);
            rootNode.set("complianceCredential", complianceJsonNode);
            sdDocumentDtoString = rootNode.toString();
        }
        catch (IOException e) {
            log.info("Exception occured while generating json:"+ e);
            e.printStackTrace();
        }
        PortalDto res = PortalDto.builder()
                .externalId(externalId)
                .message("Message")
                .status("Confirm")
                .selfDescriptionDocument(sdDocumentDtoString)
                .build();

        log.info("Request data: "+res);
        return res;
    }
}
