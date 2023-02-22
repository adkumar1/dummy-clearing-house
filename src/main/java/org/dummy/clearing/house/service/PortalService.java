package org.dummy.clearing.house.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dummy.clearing.house.dto.PortalDto;
import org.dummy.clearing.house.proxy.PortalProxy;
import org.keycloak.admin.client.token.TokenManager;
import org.dummy.clearing.house.model.VerifiableCredentialDto;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Async
    public void sendDataToPortal(VerifiableCredentialDto verifiableCredentialDto,
                                 String externalId) {
        // Send response back to Portal using feign
        String token = "Bearer "+tokenManager.getAccessTokenString();

        PortalDto portalDto = createPortalRequestObejct(externalId, verifiableCredentialDto);

        if(verifiableCredentialDto.getType().contains("LegalPerson")) {
            portalProxy.postPortalLegalPersonResponse(portalDto, token);
        } else if(verifiableCredentialDto.getType().contains("ServiceOffering")) {
            portalProxy.postPortalServiceOfferingResponse(portalDto, token);
        }
    }

    private PortalDto createPortalRequestObejct(String externalId,
            VerifiableCredentialDto verifiableCredentialDto) {
        ObjectMapper Obj = new ObjectMapper();
        String sdDocumentDtoString = "";
        try {
            sdDocumentDtoString = Obj.writeValueAsString(verifiableCredentialDto);
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
