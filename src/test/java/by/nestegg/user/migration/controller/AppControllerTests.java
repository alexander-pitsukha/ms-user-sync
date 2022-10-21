package by.nestegg.user.migration.controller;

import by.nestegg.user.migration.service.UserMigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppController.class)
class AppControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserMigrationService userMigrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartMigration() throws Exception {
        willDoNothing().given(userMigrationService).usersMigration();

        mockMvc.perform(get("/migration/start")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userMigrationService).usersMigration();
    }

    @Test
    void testUserMigration() throws Exception {
        willDoNothing().given(userMigrationService).userMigration(any(UUID.class));

        mockMvc.perform(post("/migration/migrate/{externalId}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userMigrationService).userMigration(any(UUID.class));
    }

    @Test
    void testCheckService() throws Exception {
        mockMvc.perform(get("/migration/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
