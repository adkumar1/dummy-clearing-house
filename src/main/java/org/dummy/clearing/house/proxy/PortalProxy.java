package org.dummy.clearing.house.proxy;

import org.dummy.clearing.house.dto.PortalDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "portalProxy", url = "${portal.url}")
public interface PortalProxy {


    @PostMapping(value = "/api/administration/registration/clearinghouse/selfDescription")
    public ResponseEntity<String> postPortalLegalPersonResponse(
            @RequestBody PortalDto body,
            @RequestHeader("Authorization") String token
    );

    @PostMapping(value = "/api/administration/Connectors/clearinghouse/selfDescription")
    public ResponseEntity<String> postPortalServiceOfferingResponse(
            @RequestBody PortalDto body,
            @RequestHeader("Authorization") String token
    );
}
