package org.xdi.oxd.badgemanager.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Created by Arvind Tomar on 4/5/17.
 */
public class BadgeClassControllerTest {
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = standaloneSetup(new BadgeClassController()).build();
    }

    @Test
    public void getBadgeClass() throws Exception {
        mockMvc.perform(get("/badgeClass/{id}","id").param("key", "key"))
                .andExpect(status().isOk());
    }
}
