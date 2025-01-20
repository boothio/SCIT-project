package net.scit.sec.spring7.controller;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.sec.spring7.dto.BoardDTO;
import net.scit.sec.spring7.dto.LoginUserDetails;
import net.scit.sec.spring7.service.BoardService;
import net.scit.sec.spring7.util.FileService;
import net.scit.sec.spring7.util.PageNavigator;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {
	private final BoardService service;
	// private final LoginUserDetails loginUserDetails;

	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;

	@Value("${user.board.pageLimit}")
	private int pageLimit;

	@GetMapping("/boardList")
	public String boardList(
			@AuthenticationPrincipal LoginUserDetails loginUser, @PageableDefault(page = 1) Pageable pageable,
			@RequestParam(name = "searchItem", defaultValue = "boardTitle") String searchItem,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord, Model model) {
		/*
		 * 25-01-14
		 * 2) 検索機能+ページング機能
		 */
		Page<BoardDTO> list = service.selectAll(pageable, searchItem, searchWord);
		int totalPages = list.getTotalPages();
		int page = pageable.getPageNumber();
		PageNavigator navi = new PageNavigator(pageLimit, page, totalPages);

		/*
		 * 1) 検索機能を追加
		 * searchItemとsearchWordはnullの状態でserviceに渡すべきではありません
		 * selectAllを修正
		 * List<BoardDTO> list = service.selectAll(searchItem, searchWord);
		 */
		model.addAttribute("list", list);
		model.addAttribute("searchItem", searchItem);
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("navi", navi);
		if (loginUser != null) {
			model.addAttribute("userName", loginUser.getUserName());
		}

		return "/board/boardList";
	}

	@GetMapping("/boardWrite")
	public String boardWrite() {
		return "/board/boardWrite";
	}

	@PostMapping("/boardWrite")
	public String boardWrite(@ModelAttribute BoardDTO boardDTO) {

		// MultipartFile file = boardDTO.getUploadFile();
		// log.info("파일 : {}",file);
		// log.info("ContentType : {}",file.getContentType());
		// log.info("OriginalFilename : {}",file.getOriginalFilename());
		// log.info("getSize : {}",file.getSize());
		// log.info("isEmpty : {}",file.isEmpty());

		// DB 등록
		service.insertBoard(boardDTO);

		return "redirect:/board/boardList";
	}

	/**
	 * ヒット数の増加メソッド
	 * 
	 * @param boardSeq
	 * @param model
	 * @return
	 */
	@GetMapping("/boardDetail")
	public String boardDetail(@RequestParam(name = "boardSeq") Long boardSeq,
			@RequestParam(name = "searchItem", defaultValue = "boardTitle") String searchItem,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord, Model model) {

		// DBでboardSeqに対応する1つの投稿を検索する
		BoardDTO boardDTO = service.selectOne(boardSeq);
		service.incrementHitCount(boardSeq);

		// 以前の検索状態を維持するため
		model.addAttribute("board", boardDTO);
		model.addAttribute("searchItem", searchItem);
		model.addAttribute("searchWord", searchWord);

		return "/board/boardDetail";

	}

	/**
	 * 投稿を削除+添付ファイルを削除
	 * 
	 * @param boardSeq
	 * @param searchItem
	 * @param searchWord
	 * @param rttr
	 * @return
	 */
	@GetMapping("/boardDelete")
	public String boardDelete(@RequestParam(name = "boardSeq") Long boardSeq,
			@RequestParam(name = "searchItem", defaultValue = "boardTitle") String searchItem,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord, RedirectAttributes rttr) {
		rttr.addAttribute("searchItem", searchItem);
		rttr.addAttribute("searchWord", searchWord);
		service.deleteOne(boardSeq);

		return "redirect:/board/boardList";
	}

	/*
	 * 修正
	 * 1) リストから修正要求時 --> boardSeq を request param に送って照会
	 * 2) 照会されたボード値をモデルに保存
	 * 3) 修正画面で 2) のデータを出力 input 内… 3) ユーザが値を修正し、その値に修正要求を送る
	 */
	/**
	 * 修正のための画面要求
	 * 
	 * @param boardSeq
	 * @return
	 */
	@GetMapping("/boardUpdate")
	public String boardUpdate(@RequestParam(name = "boardSeq") Long boardSeq,
			@RequestParam(name = "searchItem", defaultValue = "boardTitle") String searchItem,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord, Model model) {
		BoardDTO boardDTO = service.selectOne(boardSeq);
		model.addAttribute("searchItem", searchItem);
		model.addAttribute("searchWord", searchWord);

		model.addAttribute("board", boardDTO);
		return "/board/boardUpdate";
	}

	/**
	 * 投稿修正処理リクエスト update board set board_content = ??? board_title=???
	 * board_writer ？ /コントローラからセッションを呼び出して取得する必要があるboard_seq = ???
	 * 
	 * @param boardDTO
	 * @return
	 */
	@PostMapping("/boardUpdate")
	public String boardUpdate(@ModelAttribute BoardDTO boardDTO,
			@RequestParam(name = "searchItem", defaultValue = "boardTitle") String searchItem,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord, RedirectAttributes rttr) {

		rttr.addAttribute("searchItem", searchItem);
		rttr.addAttribute("searchWord", searchWord);
		// DB登録
		log.info(boardDTO.toString());
		service.updateBoard(boardDTO);
		return "redirect:/board/boardList";
	}

	/**
	 * ゴミ箱アイコンをクリックしてファイルのみを削除する
	 * 
	 * @param boardSeq
	 * @return
	 */
	@GetMapping("/deleteFile")
	public String deleteFile(@RequestParam(name = "boardSeq") long boardSeq,
			@RequestParam(name = "searchItem", defaultValue = "boardTitle") String searchItem,
			@RequestParam(name = "searchWord", defaultValue = "") String searchWord, RedirectAttributes rttr) {

		BoardDTO boardDTO = service.selectOne(boardSeq);

		String savedFileName = boardDTO.getSavedFileName();
		String fullPath = uploadPath + "/" + savedFileName;
		// 1）物理的に存在するファイルを削除する
		boolean result = FileService.deleteFile(fullPath);
		log.info("삭제결과 : {}", result);
		// 2) DBも修正 -> file列2つの値をnullにする
		service.deleteFile(boardSeq);

		rttr.addAttribute("boardSeq", boardSeq);
		// サーチアイテム、サーチワードも送らなければならない！
		//クライアントから送信する必要があります
		rttr.addAttribute("searchItem", searchItem);
		rttr.addAttribute("searchWord", searchWord);

		return "redirect:/board/boardDetail";

	}

	@GetMapping("/download")
	public String download(@RequestParam(name = "boardSeq") Long boardSeq, HttpServletResponse response) {
		BoardDTO boardDTO = service.selectOne(boardSeq);
		String savedFileName = boardDTO.getSavedFileName();
		String originalFileName = boardDTO.getOriginalFileName();

		try {
			String tempName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8.toString());
			// ファイルを開かずにダウンロードできるようにする =
			response.setHeader("Content-Disposition", "attachment;filename=" + tempName);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String fullPath = uploadPath + "/" + savedFileName;

		FileInputStream fin = null; // ローカルで input
		ServletOutputStream fout = null; // ネットワークへ output

		try {
			fin = new FileInputStream(fullPath);
			fout = response.getOutputStream();

			FileCopyUtils.copy(fin, fout);

			fout.close();
			fin.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
