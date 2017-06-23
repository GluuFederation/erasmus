package org.xdi.oxd.badgemanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.xdi.oxd.badgemanager.service.OxdService;
import org.xdi.oxd.badgemanager.service.RedisService;
import org.xdi.oxd.common.CommandResponse;
import org.xdi.oxd.common.ResponseStatus;
import org.xdi.oxd.common.response.RegisterSiteResponse;

import javax.inject.Inject;
import java.io.IOException;

@Component

@ContextConfiguration(classes = RedisService.class, loader = AnnotationConfigContextLoader.class)
@ComponentScan({"org.xdi.oxd.badgemanager.service"})
public class Settings {

    private static final Logger logger = LoggerFactory.getLogger(Settings.class);

//    @Value("${oxd.server.op-host}")
//    private String opHost;

    @Value("${oxd.client.redirect-uri}")
    private String redirectUrl;

    @Value("${oxd.client.logout-uri}")
    private String logoutUrl;

    @Value("${oxd.client.post-logout-uri}")
    private String postLogoutUrl;

    @Autowired
    private OxdService oxdService;

    private String oxdId;

    private void setRedisData(RedisTemplate<Object, Object> redisTemplate, String key, String value) throws IOException {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getOxdId(RedisTemplate<Object, Object> redisTemplate, String opHost) {
        logger.info("opHost in getOxdId():" + opHost);

        if (redisTemplate.opsForValue().get(opHost) != null) {
            this.oxdId = redisTemplate.opsForValue().get(opHost).toString();
            logger.info("Site Oxd Id:" + this.oxdId);
            return this.oxdId;
        }

        CommandResponse commandResponse = oxdService.registerSite(opHost, redirectUrl, logoutUrl, postLogoutUrl);
        if (commandResponse.getStatus().equals(ResponseStatus.ERROR)) {
            logger.error("Error in register site in oxd:" + CommandResponse.INTERNAL_ERROR_RESPONSE_AS_STRING);
            throw new RuntimeException("Can not register site: {redirectUrl: '" + redirectUrl + "', logoutUrl: '" + logoutUrl + "', postLogoutUrl: '" + postLogoutUrl + "'}. Plese see the oxd-server.log");
        }

        RegisterSiteResponse response = commandResponse.dataAsResponse(RegisterSiteResponse.class);
        this.oxdId = response.getOxdId();

        try {
            setRedisData(redisTemplate, opHost, this.oxdId);
        } catch (IOException e) {
            logger.error("Exception in storing oxd id to redis:" + e.getMessage());
            e.printStackTrace();
        }
        logger.info("Site Oxd Id:" + this.oxdId);
        return this.oxdId;
    }
}
