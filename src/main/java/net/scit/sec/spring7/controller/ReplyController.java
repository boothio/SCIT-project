package net.scit.sec.spring7.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.sec.spring7.dto.LoginUserDetails;
import net.scit.sec.spring7.dto.ReplyDTO;
import net.scit.sec.spring7.service.ReplyService;

@RestController
@Slf4j
@RequestMapping("/reply")
@RequiredArgsConstructor
public class ReplyController {
	private final ReplyService replyService;

	@PostMapping("/replyInsert")
	public String replyInsert(@ModelAttribute ReplyDTO replyDTO, @AuthenticationPrincipal LoginUserDetails loginUser) {

		String loginId = loginUser.getUserId();
		replyDTO.setReplyWriter(loginId);

		log.info("댓글 정보 : {}", replyDTO.toString());
		replyService.replyInsert(replyDTO);
		return "success";
	}

	/**
	 * boardSeqに対応する完全なコメントを見る
	 * 
	 * @param boardSeq
	 * @return
	 */
	@GetMapping("/replyAll")
	public List<ReplyDTO> replyAll(@RequestParam(name = "boardSeq") Long boardSeq) {
		List<ReplyDTO> list = replyService.replyAll(boardSeq);
		return list;
	}

	@GetMapping("/replyDelete")
	public String replyDelete(@RequestParam(name = "replySeq") Long replySeq) {
		replyService.replyDelete(replySeq);
		return "success";
	}

	/**
	 * 修正のための照会
	 * 
	 * @return
	 */
	@GetMapping("/replyUpdate")
	public ReplyDTO replyUpdate(@RequestParam(name = "replySeq") Long replySeq) {
		ReplyDTO replyDTO = replyService.replySelectOne(replySeq);

		return replyDTO;
	}

	@PostMapping("/replyUpdateProc")
	public String replyUpdateProc(@ModelAttribute ReplyDTO replyDTO) {
		replyService.replyUpdateProc(replyDTO);
		return "success";
	}

}
