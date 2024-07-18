package com.e207.woojoobook.api.user;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.e207.woojoobook.api.user.request.EmailCodeCreateRequest;
import com.e207.woojoobook.api.user.request.LoginRequest;
import com.e207.woojoobook.api.user.request.UserCreateRequest;
import com.e207.woojoobook.api.user.request.UserUpdateRequest;
import com.e207.woojoobook.api.verification.request.VerificationMail;
import com.e207.woojoobook.domain.user.UserSlaveRepository;
import com.e207.woojoobook.global.security.SecurityConfig;
import com.e207.woojoobook.global.security.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

@Import({SecurityConfig.class, UserValidator.class})
@WebMvcTest(UserController.class)
class UserControllerTest {

	@MockBean
	private JwtProvider jwtProvider;
	@MockBean
	private UserService userService;
	@MockBean
	private UserSlaveRepository userSlaveRepository;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@DisplayName("인증이 완료된 회원이 회원가입을 한다")
	@Test
	void createMember_success() throws Exception {
		// given
		String email = "test@test.com";
		String password = "password";
		String passwordConfirm = "password";
		String nickname = "nickname";
		UserCreateRequest userCreateRequest = createUserCreateRequest(email, password, passwordConfirm, nickname);

		doNothing().when(this.userService).join(any(UserCreateRequest.class));

		// when
		ResultActions resultActions = this.mockMvc.perform(post("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.objectMapper.writeValueAsString(userCreateRequest)));

		// then
		resultActions.andExpect(status().isCreated())
			.andDo(print());
	}

	@DisplayName("비밀번호가 일치하지 않다면 회원가입은 실패한다")
	@Test
	void createMember_fail_password() throws Exception {
		// given
		String email = "test@test.com";
		String password = "notSamePassword";
		String passwordConfirm = "password";
		String nickname = "nickname";
		UserCreateRequest userCreateRequest = createUserCreateRequest(email, password, passwordConfirm, nickname);

		// when
		ResultActions resultActions = this.mockMvc.perform(post("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.objectMapper.writeValueAsString(userCreateRequest)));

		// then
		resultActions.andExpect(status().is4xxClientError())
			.andDo(print());
	}

	@DisplayName("이메일 인증코드를 발송한다")
	@Test
	void sendVerificationCode_success() throws Exception {
		// given
		String email = "test@test.com";
		EmailCodeCreateRequest request = new EmailCodeCreateRequest();
		request.setEmail(email);

		// when
		ResultActions resultActions = this.mockMvc.perform(post("/users/emails")
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.objectMapper.writeValueAsString(request)));

		// then
		resultActions.andExpect(status().isCreated());
	}

	@DisplayName("정확한 인증 코드라면 이메일 인증을 완료한다")
	@Test
	void verifyEmail_success() throws Exception {
		// given
		VerificationMail verificationMail = VerificationMail.builder()
			.email("test@test.com")
			.verificationCode("validToken")
			.build();

		given(this.userService.verifyEmail(any(VerificationMail.class))).willReturn(true);

		// when
		ResultActions resultActions = this.mockMvc.perform(put("/users/emails")
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.objectMapper.writeValueAsString(verificationMail)));

		// then
		resultActions.andExpect(status().isOk());
	}

	@DisplayName("이메일 중복 여부를 체크한다")
	@Test
	void checkDuplicateEmail_success() throws Exception {
		// given
		String email = "test@test.com";
		given(this.userService.checkDuplicateEmail(any())).willReturn(true);

		// when
		ResultActions resultActions = this.mockMvc.perform(get("/users/emails/{email}", email));

		// then
		resultActions.andExpect(status().isOk());
	}

	@DisplayName("닉네임 중복 여부를 체크한다")
	@Test
	void checkDuplicateNickname_success() throws Exception {
		// given
		String nickname = "nickname";
		given(this.userService.checkDuplicateNickname(any())).willReturn(true);

		// when
		ResultActions resultActions = this.mockMvc.perform(get("/users/nicknames/{nickname}", nickname));

		// then
		resultActions.andExpect(status().isOk());
	}

	@DisplayName("사용자가 로그인을 성공한다")
	@Test
	void login_success() throws Exception {
		// given
		String token = "validToken";
		LoginRequest loginRequest = new LoginRequest("test@test.com", "password");
		given(this.jwtProvider.createToken(any())).willReturn(token);

		// when
		ResultActions resultActions = this.mockMvc.perform(post("/auth")
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.objectMapper.writeValueAsString(loginRequest)));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(header().exists("Authorization"));
	}

	@WithMockUser
	@DisplayName("회원이 회원 정보를 수정한다")
	@Test
	void updateUser_success() throws Exception {
		// given
		UserUpdateRequest userUpdateRequest = new UserUpdateRequest("nickname", "areaCode");

		// when
		ResultActions resultActions = this.mockMvc.perform(put("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.objectMapper.writeValueAsString(userUpdateRequest)));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(header().exists("Authorization"));
	}

	@WithMockUser
	@DisplayName("회원이 비밀번호를 변경한다")
	@Test
	void updatePassword_success() throws Exception {
		// given
		var request = new PasswordUpdateRequest("cur", "change", "change");

		// when
		ResultActions resultActions = this.mockMvc.perform(put("/users/password")
			.contentType(MediaType.APPLICATION_JSON)
			.content(this.objectMapper.writeValueAsString(request)));

		// then
		resultActions.andExpect(status().isOk());
	}

	private static UserCreateRequest createUserCreateRequest(String email, String password, String passwordConfirm,
		String nickname) {
		return UserCreateRequest.builder()
			.email(email)
			.password(password)
			.passwordConfirm(passwordConfirm)
			.nickname(nickname)
			.areaCode("areaCode")
			.build();
	}
}