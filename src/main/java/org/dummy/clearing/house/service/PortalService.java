package org.dummy.clearing.house.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.dummy.clearing.house.dto.PortalDto;
import org.dummy.clearing.house.model.VerifiableCredentialDto;
import org.dummy.clearing.house.proxy.PortalProxy;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class PortalService {

    @Autowired
    private PortalProxy portalProxy;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private ComplianceJsonLoader complianceJsonLoader;


    @Async
    public void sendDataToPortal(VerifiableCredentialDto verifiableCredentialDto,
                                 String externalId) {
        // Send response back to Portal using feign
        String token = "Bearer "+tokenManager.getAccessTokenString();

        PortalDto portalDto = createPortalRequestObejct(externalId,
                verifiableCredentialDto,
                complianceJsonLoader.getComplianceServiceJson());

        ResponseEntity<String> res = null;
        if(verifiableCredentialDto.getType().contains("LegalPerson")) {
            res = portalProxy.postPortalLegalPersonResponse(portalDto, token);
            log.info("Response from portal: "+ res.getStatusCode());
        } else if(verifiableCredentialDto.getType().contains("ServiceOffering")) {
            res = portalProxy.postPortalServiceOfferingResponse(portalDto, token);
            log.info("Response from portal: "+ res.getStatusCode());
        }
    }

    private PortalDto createPortalRequestObejct(String externalId,
                                                VerifiableCredentialDto verifiableCredentialDto,
                                                JsonNode complianceJsonNode) {
        ObjectMapper mapper = new ObjectMapper();
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
