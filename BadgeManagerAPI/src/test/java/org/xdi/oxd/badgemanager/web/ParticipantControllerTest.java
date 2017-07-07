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
public class ParticipantControllerTest {
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = standaloneSetup(new ParticipantController()).build();
    }

    @Test
    public void getParticipants() throws Exception {
        mockMvc.perform(get("/participants/{state}/{city}", "state", "city"))
                .andExpect(status().isOk());
    }
}
