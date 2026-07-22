package com.angelkml.libraryapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listWithoutAuthenticationIsRejected() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void createListUpdateAndDeleteMovie() throws Exception {
        mockMvc.perform(post("/api/movies").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Dune","barcode":"1234567890123","watched":false,\
                                "type":"FILM","genre":"SCIENCE_FICTION","year":2021,"actors":["Timothée Chalamet"]}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.watched").value(false));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Dune"));

        Long id = 1L;

        mockMvc.perform(put("/api/movies/" + id).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Dune","barcode":"1234567890123","watched":true,\
                                "type":"FILM","genre":"SCIENCE_FICTION","year":2021,"actors":[]}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.watched").value(true));

        mockMvc.perform(delete("/api/movies/" + id).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void creatingDuplicateBarcodeIsRejected() throws Exception {
        mockMvc.perform(post("/api/movies").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Arrival","barcode":"9999999999999","watched":false,\
                                "type":"FILM","genre":"SCIENCE_FICTION","year":2016,"actors":[]}"""))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/movies").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Arrival (copie)","barcode":"9999999999999","watched":false,\
                                "type":"FILM","genre":"SCIENCE_FICTION","year":2016,"actors":[]}"""))
                .andExpect(status().isConflict());
    }
}
