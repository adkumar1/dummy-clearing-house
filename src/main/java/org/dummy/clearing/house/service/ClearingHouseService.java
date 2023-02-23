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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
@Slf4j
public class ClearingHouseService implements ApiApiDelegate {

    @Autowired
    private PortalService portalService;

    @Autowired
    private TaskScheduler taskScheduler;

    @SuppressWarnings("unchecked")
    public ResponseEntity<Void> apiCredentialsPost(
            VerifiableCredentialDto verifiableCredentialDto,
            String externalId) {

        taskScheduler.schedule(() -> portalService.sendDataToPortal(verifiableCredentialDto, externalId), Instant.now().plus(Duration.ofSeconds(60)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }




}
