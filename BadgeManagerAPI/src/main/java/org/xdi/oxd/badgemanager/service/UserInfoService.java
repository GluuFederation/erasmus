package org.xdi.oxd.badgemanager.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.xdi.oxd.badgemanager.Settings;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;
import org.xdi.oxd.badgemanager.model.UserInfo;
import org.xdi.oxd.common.CommandResponse;
import org.xdi.oxd.common.response.GetUserInfoResponse;

import javax.inject.Inject;

/**
 * Created by Arvind Tomar on 25/4/17.
 */
@Component
public class UserInfoService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoService.class);

    @Autowired
    public RedisTemplate<Object, Object> redisTemplate;

    @Inject
    private Settings settings;

    @Inject
    private OxdService oxdService;

    public UserInfo getUserInfo(String accessToken) {
        UserInfo userInfo = new UserInfo();

        try {
            CommandResponse respUserInfo = oxdService.getUserInfo(settings.getOxdId(redisTemplate), accessToken);
            logger.error("User info response status:" + respUserInfo.getStatus());
            if (respUserInfo.getStatus().equals(org.xdi.oxd.common.ResponseStatus.ERROR)) {
                logger.error("Error in retrieving user info in oxd:" + CommandResponse.INTERNAL_ERROR_RESPONSE_AS_STRING);
                return null;
            } else {
                GetUserInfoResponse objUserInfo = respUserInfo.dataAsResponse(GetUserInfoResponse.class);
                logger.info("response userinfo claims:" + objUserInfo.getClaims());
                String claims = GsonService.getGson().toJson(objUserInfo.getClaims());
                JsonObject jObjClaims = new JsonParser().parse(claims).getAsJsonObject();
                userInfo.setSub(GsonService.getValueFromJson("sub", jObjClaims));
                userInfo.setIssuer(GsonService.getValueFromJson("issuer", jObjClaims));
                userInfo.setEmail(GsonService.getValueFromJson("email", jObjClaims));
                userInfo.setName(GsonService.getValueFromJson("name", jObjClaims));

                return userInfo;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception in retrieving user info:" + ex.getMessage());
            return null;
        }
    }
}
