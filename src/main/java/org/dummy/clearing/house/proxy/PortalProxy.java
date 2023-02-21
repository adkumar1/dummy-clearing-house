package org.dummy.clearing.house.proxy;

import feign.Headers;
import org.dummy.clearing.house.dto.PortalDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.Map;

@FeignClient(name = "portalProxy", url = "${portal.url}")
public interface PortalProxy {


    @PostMapping(value = "/api/administration/registration/clearinghouse/selfDescription")
    public Void postPortalLegalPersonResponse(
            @RequestBody PortalDto body,
            @RequestHeader("Authorization") String token
    );

    @PostMapping(value = "/api/administration/Connectors/clearinghouse/selfDescription")
    public Void postPortalServiceOfferingResponse(
            @RequestBody PortalDto body,
            @RequestHeader("Authorization") String token
    );
}
