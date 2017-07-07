package org.xdi.oxd.badgemanager.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.xdi.oxd.badgemanager.global.Global;
import org.xdi.oxd.badgemanager.model.AccessRequest;
import org.xdi.oxd.badgemanager.model.BadgeRequestDetail;
import org.xdi.oxd.badgemanager.model.PrivacyRequest;
import org.xdi.oxd.badgemanager.model.TemplateBadgeRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Arvind Tomar on 23/6/17.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {BadgeController.class}, secure = false)
@TestPropertySource({"classpath:test.properties"})
@ComponentScan(basePackages = "org.xdi.oxd.badgemanager.web")
public class BadgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Configuration
    static class ContextConfiguration {

        @Bean
        public static PropertySourcesPlaceholderConfigurer properties() throws Exception {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    @Test
    public void getTemplateBadgesByParticipant() throws Exception {
        TemplateBadgeRequest templateBadgeRequest = new TemplateBadgeRequest();
        templateBadgeRequest.setOpHost(Global.Test_OpHost);
        templateBadgeRequest.setType("type");
        mockMvc.perform(post("/badges/templates").contentType(MediaType.APPLICATION_JSON)
                .header("AccessToken", "accessToken")
                .content(objectMapper.writeValueAsBytes(templateBadgeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void verifyBadge() throws Exception {
        mockMvc.perform(get("/badges/verify/{id}", "id"))
                .andExpect(status().isOk());
    }

    @Test
    public void verifyPrivateBadge() throws Exception {
        mockMvc.perform(post("/badges/verify").contentType(MediaType.APPLICATION_JSON)
                .param("id", "id")
                .param("key", "key"))
                .andExpect(status().isOk());
    }

    @Test
    public void getBadge() throws Exception {
        mockMvc.perform(get("/badges/{badgeRequestInum}", "badgeRequestInum")
                .header("Authorization", "Authorization"))
                .andExpect(status().isOk());
    }

    @Test
    public void getBadgeByBadgeRequestInum() throws Exception {
        BadgeRequestDetail badgeRequestDetail = new BadgeRequestDetail();
        badgeRequestDetail.setBadgeRequestInum("@!4301.2A50.9A09.7688!1002!0B93.AA10");
        badgeRequestDetail.setOpHost(Global.Test_OpHost);
        mockMvc.perform(post("/badges/details").contentType(MediaType.APPLICATION_JSON)
                .header("AccessToken", Global.Test_AccessToken)
                .content(objectMapper.writeValueAsBytes(badgeRequestDetail)))
                .andExpect(status().isOk());
    }

    @Test
    public void setBadgePrivacy() throws Exception {
        PrivacyRequest privacyRequest = new PrivacyRequest();
        privacyRequest.setBadgeRequestInum("@!4301.2A50.9A09.7688!1002!148A.90CE");
        privacyRequest.setOpHost(Global.Test_OpHost);
        privacyRequest.setPrivacy("Public");
        mockMvc.perform(post("/badges/setPrivacy").contentType(MediaType.APPLICATION_JSON)
                .header("AccessToken", Global.Test_AccessToken)
                .content(objectMapper.writeValueAsBytes(privacyRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void setBadgePermission() throws Exception {
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setBadge("@!4301.2A50.9A09.7688!1002!148A.90CE");
        accessRequest.setOpHost(Global.Test_OpHost);
        accessRequest.setAccess("true");
        mockMvc.perform(post("/badges/setPermission").contentType(MediaType.APPLICATION_JSON)
                .header("AccessToken", Global.Test_AccessToken)
                .content(objectMapper.writeValueAsBytes(accessRequest)))
                .andExpect(status().isOk());
    }
}
