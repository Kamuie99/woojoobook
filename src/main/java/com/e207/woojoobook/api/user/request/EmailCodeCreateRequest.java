package com.e207.woojoobook.api.user.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailCodeCreateRequest {
	@Email
	private String email;
}
