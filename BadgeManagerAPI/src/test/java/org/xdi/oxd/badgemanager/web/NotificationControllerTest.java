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
import org.xdi.oxd.badgemanager.model.NotificationRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Arvind Tomar on 6/7/17.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {NotificationController.class}, secure = false)
@TestPropertySource({"classpath:test.properties"})
@ComponentScan(basePackages = "org.xdi.oxd.badgemanager.web")
public class NotificationControllerTest {

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
    public void sendNotification() throws Exception {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setBadge("badge");
        notificationRequest.setOpHost(Global.Test_OpHost);
        notificationRequest.setParticipant("participant");
        mockMvc.perform(post("/notification/send").contentType(MediaType.APPLICATION_JSON)
                .header("AccessToken", Global.Test_AccessToken)
                .content(objectMapper.writeValueAsBytes(notificationRequest)))
                .andExpect(status().isOk());
    }

}
