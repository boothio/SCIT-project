package net.scit.sec.spring7.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.sec.spring7.dto.UserDTO;
import net.scit.sec.spring7.service.UserService;



@Controller
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	/**
	 * 会員登録画面のリクエスト
	 * @return
	 */
	@GetMapping("/join")
	public String join() {
		return "user/join";
	}
	
	/**
	 * ID重複確認チェック
	 * @param userId
	 * @return
	 */
	@PostMapping("/idCheck")
	@ResponseBody
	public boolean idCheck(@RequestParam(name="userId") String userId) {
		boolean result = userService.existId(userId);
		
		return result;
	}
	
	@PostMapping("/joinProc")
	public String joinProc(@ModelAttribute UserDTO userDTO) {
		log.info("회원정보 : {}", userDTO.toString());
		boolean result = userService.joinProc(userDTO);
		
		return "redirect:/";
	}
	
	
	/**
	 * 1) ログイン画面のリクエスト
	 *2) エラー発生時はこちらへリダイレクト
	 * @param error
	 * @param model
	 * @return
	 */
	@GetMapping("/login")
	public String login(@RequestParam(value="error", required = false) String error, Model model) {
		System.out.println("error ==>" + error);
		if (error != null) {
			model.addAttribute("error", error);
			model.addAttribute("errMessage", "IDやパスワードが間違っています。");
		}
		
		return "user/login";
	}
	
	
}
