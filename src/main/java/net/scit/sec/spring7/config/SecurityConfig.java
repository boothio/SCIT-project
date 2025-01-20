package net.scit.sec.spring7.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests((auth) -> auth
						.requestMatchers(
								"/", "/board/boardList", "/board/boardDetail", "/user/join", "/board/download",
								"/reply/replyAll", "/user/idCheck", "/user/joinProc", "/user/login" // 通常のルートで来たときにめくるリンク
								, "/images/**", "/css/**", "/js/**", "/job/**")
						.permitAll()
						.requestMatchers("/admin").hasRole("ADMIN")
						.requestMatchers("/myPage/**").hasAnyRole("ADMIN", "USER")
						.anyRequest().authenticated());

		// カスタムログイン設定

		http
				.formLogin((auth) -> auth
						.loginPage("/user/login") // 異常なルートで来たときにめくるリンク

						.loginProcessingUrl("/user/loginProc")
						.usernameParameter("userId")
						.passwordParameter("userPwd")
						.failureUrl("/user/login?error=true")
						.permitAll());

		// logout 設定

		http
				.logout((auth) -> auth
						.logoutUrl("/user/logout")
						.logoutSuccessHandler(customLogoutSuccessHandler())
						.invalidateHttpSession(true)
						.clearAuthentication(true));

		// POST要求時にCSRFトークンを要求するため（Cross-Site Request Forgery）無効化（開発環境）

		http
				.csrf((auth) -> auth.disable());

		return http.build();
	}

	// 一方向パスワード暗号化

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// カスタム LogoutSuccessHandler

	@Bean
	LogoutSuccessHandler customLogoutSuccessHandler() {
		return new LogoutSuccessHandler() {
			@Override
			public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
					org.springframework.security.core.Authentication authentication)
					throws IOException {
				// 現在のページにリダイレクト

				String refererUrl = request.getHeader("Referer");
				response.sendRedirect(refererUrl != null ? refererUrl : "/");
			}
		};
	}
}
