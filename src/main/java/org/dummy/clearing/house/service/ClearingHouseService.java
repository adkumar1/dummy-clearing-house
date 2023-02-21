package org.dummy.clearing.house.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Headers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dummy.clearing.house.api.ApiApiDelegate;
import org.dummy.clearing.house.dto.PortalDto;
import org.dummy.clearing.house.dto.SDDocumentDto;
import org.dummy.clearing.house.model.VerifiableCredentialDto;
import org.dummy.clearing.house.proxy.PortalProxy;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
@Slf4j
public class ClearingHouseService implements ApiApiDelegate {

    @Autowired
    private PortalProxy portalProxy;

    @Autowired
    private TokenManager tokenManager;

    @SuppressWarnings("unchecked")
    @Async
    public ResponseEntity<Void> apiCredentialsPost(
            VerifiableCredentialDto verifiableCredentialDto,
            String externalId) {

        sendDataToPortal(verifiableCredentialDto, externalId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Async
    private void sendDataToPortal(VerifiableCredentialDto verifiableCredentialDto,
                                  String externalId) {
        // Send response back to Portal using feign
        String token = "Bearer "+tokenManager.getAccessTokenString();
        log.info("The token fetched for Portal keycloak: \n"+token+"\n\n");

        PortalDto portalDto = createPortalRequestObejct(verifiableCredentialDto);

        if(verifiableCredentialDto.getType().contains("LegalPerson")) {
            portalProxy.postPortalLegalPersonResponse(portalDto, token);
        } else if(verifiableCredentialDto.getType().contains("ServiceOffering")) {
            portalProxy.postPortalServiceOfferingResponse(portalDto, token);
        }
    }

    private PortalDto createPortalRequestObejct(VerifiableCredentialDto verifiableCredentialDto) {
        SDDocumentDto sdDocumentDto = SDDocumentDto.builder()
                .bpn("BPN")
                .build();
        ObjectMapper Obj = new ObjectMapper();
        String sdDocumentDtoString = "";
        try {
            sdDocumentDtoString = Obj.writeValueAsString(sdDocumentDto);
        }
        catch (IOException e) {
            log.info("Exception occured while generating json:"+ e);
            e.printStackTrace();
        }
        PortalDto res = PortalDto.builder()
                .externalId("ID01234-123-4321")
                .message("Message")
                .status("Confirm")
                .selfDescriptionDocument(sdDocumentDtoString)
                .build();

        log.info("Request data: "+res);
        return res;
    }

}
