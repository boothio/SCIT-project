package net.scit.sec.spring7.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.sec.spring7.entity.UserEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserDTO {

	private String userId;
	private String userPwd;
	
	
	private String userName;

	private String email;

	private String roles;
	
	private Boolean enabled;
	
	public static UserDTO toDTO(UserEntity userEntity) {
		return UserDTO.builder()
				.userId(userEntity.getUserId())
				.userPwd(userEntity.getUserPwd())
				.userName(userEntity.getUserName())
				.email(userEntity.getEmail())
				.roles(userEntity.getRoles())
				.enabled(userEntity.getEnabled())
				.build();
	}
}
