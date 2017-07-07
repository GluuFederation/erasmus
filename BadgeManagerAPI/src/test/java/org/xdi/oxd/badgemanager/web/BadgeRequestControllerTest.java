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
import org.xdi.oxd.badgemanager.model.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Arvind Tomar on 6/7/17.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {BadgeRequestController.class}, secure = false)
@TestPropertySource({"classpath:test.properties"})
@ComponentScan(basePackages = "org.xdi.oxd.badgemanager.web")
public class BadgeRequestControllerTest {

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
    public void createBadgeRequest() throws Exception {
        CreateBadgeRequest createBadgeRequest = new CreateBadgeRequest();
        createBadgeRequest.setParticipant("participant");
        createBadgeRequest.setTemplateBadgeId("templateBadgeId");
        createBadgeRequest.setTemplateBadgeTitle("templateBadgeTitle");
        createBadgeRequest.setOpHost(Global.Test_OpHost);
        mockMvc.perform(post("/badges/request").contentType(MediaType.APPLICATION_JSON)
                .header("AccessToken", Global.Test_AccessToken)
                .content(objectMapper.writeValueAsBytes(createBadgeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void getBadgeRequestsByParticipant() throws Exception {
        mockMvc.perform(get("/badges/request/list/{participant:.+}/{status:.+}", "participant", "status")
                .header("Authorization", Global.AccessToken))
                .andExpect(status().isOk());
    }

    @Test
    public void approveBadgeRequest() throws Exception {
        ApproveBadge approveBadge = new ApproveBadge();
        approveBadge.setInum("inum");
        approveBadge.setPrivacy("privacy");
        approveBadge.setValidity(2);
        mockMvc.perform(post("/badges/request/approve").contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", Global.Test_AccessToken)
                .content(objectMapper.writeValueAsBytes(approveBadge)))
                .andExpect(status().isOk());
    }

    @Test
    public void getBadgeRequestsByStatus() throws Exception {
        BadgeRequest badgeRequest = new BadgeRequest();
        badgeRequest.setOpHost(Global.Test_OpHost);
        badgeRequest.setStatus("all");
        mockMvc.perform(post("/badges/request/list").contentType(MediaType.APPLICATION_JSON)
                .header("AccessToken", Global.Test_AccessToken)
                .content(objectMapper.writeValueAsBytes(badgeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void removeBadgeRequest() throws Exception {
        BadgeRequestDetail badgeRequestDetail = new BadgeRequestDetail();
        badgeRequestDetail.setOpHost(Global.Test_OpHost);
        badgeRequestDetail.setBadgeRequestInum("badgeRequestInum");
        mockMvc.perform(delete("/badges/request/delete").contentType(MediaType.APPLICATION_JSON)
                .header("AccessToken", Global.Test_AccessToken)
                .content(objectMapper.writeValueAsBytes(badgeRequestDetail)))
                .andExpect(status().isOk());
    }

}
