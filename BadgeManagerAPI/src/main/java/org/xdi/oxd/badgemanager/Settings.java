package org.xdi.oxd.badgemanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.xdi.oxd.badgemanager.service.OxdService;
import org.xdi.oxd.common.CommandResponse;
import org.xdi.oxd.common.ResponseStatus;
import org.xdi.oxd.common.response.RegisterSiteResponse;

import javax.inject.Inject;
import java.io.IOException;

@Component
public class Settings {

    @Value("${oxd.server.op-host}")
    private String opHost;

    @Value("${oxd.client.redirect-uri}")
    private String redirectUrl;

    @Value("${oxd.client.logout-uri}")
    private String logoutUrl;

    @Value("${oxd.client.post-logout-uri}")
    private String postLogoutUrl;

    @Inject
    private OxdService oxdService;

    private String oxdId;

    private void setRedisData(RedisTemplate<Object, Object> redisTemplate, String key, String value) throws IOException {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getOxdId(RedisTemplate<Object, Object> redisTemplate) {
        if (redisTemplate.opsForValue().get(opHost) != null) {
            this.oxdId = redisTemplate.opsForValue().get(opHost).toString();
            return this.oxdId;
        }

        CommandResponse commandResponse = oxdService.registerSite(redirectUrl, logoutUrl, postLogoutUrl);
        if (commandResponse.getStatus().equals(ResponseStatus.ERROR)) {
            System.out.println("Error in register site in oxd:" + CommandResponse.INTERNAL_ERROR_RESPONSE_AS_STRING);
            throw new RuntimeException("Can not register site: {redirectUrl: '" + redirectUrl + "', logoutUrl: '" + logoutUrl + "', postLogoutUrl: '" + postLogoutUrl + "'}. Plese see the oxd-server.log");
        }

        RegisterSiteResponse response = commandResponse.dataAsResponse(RegisterSiteResponse.class);
        this.oxdId = response.getOxdId();

        try {
            setRedisData(redisTemplate, this.opHost, this.oxdId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.oxdId;
    }
}
