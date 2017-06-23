package org.xdi.oxd.badgemanager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.xdi.oxd.badgemanager.web.TempURLController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Arvind Tomar on 4/5/17.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {TempURLController.class}, secure = false)
@TestPropertySource({"classpath:test.properties"})
@ComponentScan(basePackages = "org.xdi.oxd.badgemanager.web")
public class TempURLControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Test
    public void redirect() throws Exception {
        mockMvc.perform(get("/tmp/{id}", "id"))
                .andExpect(status().isOk());
    }

    @Configuration
    static class ContextConfiguration {

        @Bean
        public static PropertySourcesPlaceholderConfigurer properties() throws Exception {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }
}


