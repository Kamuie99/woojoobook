package com.e207.woojoobook.api.extension;

import com.e207.woojoobook.api.extension.request.ExtensionRespondRequest;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.global.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = ExtensionController.class)
class ExtensionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ExtensionService extensionService;

    @WithMockUser
    @DisplayName("회원이 대여한 도서에 대해 연장신청을 한다")
    @Test
    void extension() throws Exception {
        // given
        Long rentalId = 1L;

        // when
        ResultActions resultActions =
                this.mockMvc.perform(post("/rentals/{rentalId}/extensions", rentalId));

        // then
        resultActions.andExpect(status().isOk());
    }

    @WithMockUser
    @DisplayName("도서 소유자가 연장신청에 응답한다")
    @Test
    void extensionRespond() throws Exception {
        // given
        Long extensionId = 1L;
        ExtensionRespondRequest request = new ExtensionRespondRequest(true);

        // when
        ResultActions resultActions =
            this.mockMvc.perform(put("/extensions/{extensionId}", extensionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isOk());
    }

    @WithMockUser
    @DisplayName("연장 신청자가 연장 신청을 취소한다")
    @Test
    void extensionDelete() throws Exception {
        // given
        Long extensionId = 1L;

        // when
        ResultActions resultActions =
            this.mockMvc.perform(delete("/extensions/{extensionId}", extensionId));

        // then
        resultActions.andExpect(status().isOk());
    }


}