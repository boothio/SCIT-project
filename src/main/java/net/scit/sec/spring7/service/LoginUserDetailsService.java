package net.scit.sec.spring7.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.scit.sec.spring7.dto.LoginUserDetails;
import net.scit.sec.spring7.entity.UserEntity;
import net.scit.sec.spring7.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class LoginUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	// UserId 検証ロジックの追加。 DBテーブルからデータをインポートする
	// ユーザーが login を行うと、SecurityConfig を傍受した後、こちらにデータを渡す

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		// Optional 返すメソッド orElseThrow() 連結して使用可能 -> UserEntity で受け取ることができる。

		UserEntity userEntity = userRepository.findById(userId)
				.orElseThrow(() -> {
					throw new UsernameNotFoundException("아이디나 비밀번호가 틀렸습니다.");
				});

		// 戻り値をUserDetailsとして返す必要があるため、UserDTOをUserDetailsに置き換えます

		return LoginUserDetails.toDTO(userEntity);
	}
}
