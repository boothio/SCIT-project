package net.scit.sec.spring7.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.sec.spring7.dto.UserDTO;
import net.scit.sec.spring7.entity.UserEntity;
import net.scit.sec.spring7.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * 受け取った userId が DB に存在するかどうかを確認する
	 * 
	 * @param userId
	 * @return
	 */
	public boolean existId(String userId) {

		boolean result = userRepository.existsById(userId);
		log.info("아이디 존재 여부 : {}", result); // サインアップするにはfalse

		return !result;
	}

	/**
	 * 会員登録処理
	 * 
	 * @param userDTO
	 * @return
	 */
	public boolean joinProc(UserDTO userDTO) {
		// パスワード暗号化

		userDTO.setUserPwd(bCryptPasswordEncoder.encode(userDTO.getUserPwd()));

		UserEntity entity = UserEntity.toEntity(userDTO);
		userRepository.save(entity); // 会員登録完了

		boolean result = userRepository.existsById(userDTO.getUserId());
		return result;
	}
}
