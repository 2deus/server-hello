package com.dongseo.server_hello;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// for the whole app (security, controllers, ApiExceptionHandler, mysql, etc.) through MockMvc
// @Transactional rolls every test back, so the database ends up unchanged
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiFailureTest {

    @Autowired
    private MockMvc mvc;

    // class passwords: name + 1234
    private static String basic(String username) {
        String pair = username + ":" + username + "1234";
        return "Basic " + Base64.getEncoder()
                .encodeToString(pair.getBytes(StandardCharsets.UTF_8));
    }

    // ---------- step 8 (noLoginIs401 and userCreatingRoomIs403 are also R7 #5 and #4) ----------

    @Test
    void noLoginIs401() throws Exception {                      // R7 #5: no token
        mvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("login required"));
    }

    @Test
    void userCreatingRoomIs403() throws Exception {             // R7 #4: rule D
        mvc.perform(post("/api/rooms")
                        .header("Authorization", basic("mina"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Test Room", "capacity": 5 }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("admin role required"));
    }

    @Test
    void adminCreatingRoomIs201() throws Exception {            // the same request, as admin
        mvc.perform(post("/api/rooms")
                        .header("Authorization", basic("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Test Room", "capacity": 5 }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("Test Room"));
    }

    // ---------- lab H2: R7 fails ----------

    @Test
    void overlapIs409() throws Exception {                      // R7 #1: rule A
        mvc.perform(post("/api/rooms/1/reservations")
                        .header("Authorization", basic("omar"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "startTime": "2026-10-06T10:30:00", "endTime": "2026-10-06T11:30:00" }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("room 1 is already booked for that time"));
    }

    @Test
    void endBeforeStartIs400() throws Exception {               // R7 #2: rule B
        mvc.perform(post("/api/rooms/1/reservations")
                        .header("Authorization", basic("mina"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "startTime": "2026-12-01T11:00:00", "endTime": "2026-12-01T10:00:00" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("request has invalid fields"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("startTime must be before endTime"));
    }

    @Test
    void anotherUsersReservationIs403() throws Exception {      // R7 #3: rule C
        mvc.perform(get("/api/reservations/1")
                        .header("Authorization", basic("omar")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("reservation 1 belongs to another user"));
    }

    @Test
    void deleteRoomWithReservationsIs409() throws Exception {   // R7 #6: P5, room has children
        mvc.perform(delete("/api/rooms/1")
                        .header("Authorization", basic("admin")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("room 1 has reservations"));
    }

    @Test
    void missingFieldIs400() throws Exception {                 // R7 #7: validation
        mvc.perform(post("/api/rooms/1/reservations")
                        .header("Authorization", basic("mina"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "startTime": "2026-12-01T10:00:00" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("request has invalid fields"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("endTime"));
    }

    // ---------- done early ----------

    @Test
    void wrongPasswordIs401() throws Exception {
        String wrong = "Basic " + Base64.getEncoder()
                .encodeToString("mina:wrongpass".getBytes(StandardCharsets.UTF_8));

        mvc.perform(get("/api/reservations/mine")
                        .header("Authorization", wrong))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("wrong username or password"));
    }

    @Test
    void missingReservationIs404() throws Exception {
        mvc.perform(get("/api/reservations/99")
                        .header("Authorization", basic("omar")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("reservation 99 not found"));
    }
}
