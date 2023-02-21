package org.dummy.clearing.house.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dummy.clearing.house.api.ApiApiDelegate;
import org.dummy.clearing.house.model.VerifiableCredentialDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
@Slf4j
public class ClearingHouseService implements ApiApiDelegate {

    @Autowired
    private PortalService portalService;

    @SuppressWarnings("unchecked")
    public ResponseEntity<Void> apiCredentialsPost(
            VerifiableCredentialDto verifiableCredentialDto,
            String externalId) {

        portalService.sendDataToPortal(verifiableCredentialDto, externalId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }




}
