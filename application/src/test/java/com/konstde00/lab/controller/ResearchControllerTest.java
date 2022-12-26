package com.konstde00.lab.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider(dataSourceBeanName = "tenantDataSource")
public class ResearchControllerTest extends AbstractApiTest {

    @Test
    @DataSet(value = {"datasets/get_all_researches/setup.yaml"})
    @ExpectedDataSet(value = {"datasets/get_all_researches/expected.yaml"})
    public void getAllResearchesTest() throws Exception {

        String token = tokenService.generate(USER).getToken();

        mockMvc.perform(
                get("/api/researches/v1")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(jsonReader.read("getAllResearchesResponse.json")))
                .andDo(print());
    }

    @Test
    @DataSet(value = {"datasets/create_research/setup.yaml"})
    @ExpectedDataSet(
            value = {"datasets/create_research/expected.yaml"},
            ignoreCols = {"id"}
    )
    public void createResearchTest() throws Exception {

        String token = tokenService.generate(ADMIN).getToken();

        mockMvc.perform(
                        post("/api/researches/v1")
                                .contentType(APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .content(jsonReader.read("createResearchRequest.json")))
                .andExpect(status().isCreated())
                .andDo(print());
    }
}



