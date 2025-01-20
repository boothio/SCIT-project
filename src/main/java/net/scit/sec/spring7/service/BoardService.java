package net.scit.sec.spring7.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.sec.spring7.dto.BoardDTO;
import net.scit.sec.spring7.entity.BoardEntity;
import net.scit.sec.spring7.repository.BoardRepository;
import net.scit.sec.spring7.util.FileService;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
	private final BoardRepository boardRepository;

	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;

	@Value("${user.board.pageLimit}")
	private int pageLimit;

	/**
	 * 1) 簡易照会 : 投稿全リスト照会 2) 検索照会 : 照会メソッドの使用 3) ページング追加
	 * 
	 * @param searchItem:   boardTitle findByboardTitle
	 * @param searchWord：ナリ
	 * @return
	 */
	public Page<BoardDTO> selectAll(Pageable pageable, String searchItem, String searchWord) {
		// -1をした理由：DBのPageは0から始まる。
		// ユーザーが要求したページ番号

		int pageNumber = pageable.getPageNumber() - 1;

		Page<BoardEntity> temp = null;

		switch (searchItem) {
			case "boardTitle":
				temp = boardRepository.findByBoardTitleContains(
						searchWord, PageRequest.of(pageNumber, pageLimit, Sort.by(Direction.DESC, "createDate")));
				break;
			case "boardWriter":
				temp = boardRepository.findByBoardWriterContains(
						searchWord, PageRequest.of(pageNumber, pageLimit, Sort.by(Direction.DESC, "createDate")));
				break;
			case "boardContent":
				temp = boardRepository.findByBoardContentContains(
						searchWord, PageRequest.of(pageNumber, pageLimit, Sort.by(Direction.DESC, "createDate")));
				break;
		}

		// 1) 単純なルックアップ
		// List<BoardEntity> temp = boardRepository.findAll(Sort.by(Sort.Direction.DESC,
		// "createDate"));

		Page<BoardDTO> list = null;

		list = temp.map((entity) -> BoardDTO.toDTO(entity));

		/*
		 * // 1ページに投稿がいくつあるか
		 * log.info("個数 : {}", list.getSize());
		 * // 総ページ数
		 * log.info("totalPage : {}", list.getTotalPages());
		 * // 全文数
		 * log.info("totalPage : {}", list.getTotalElements());
		 * // ページ番号-1
		 * log.info("number : {}", list.getNumber());
		 * // 現在のページに投稿がいくつあるか
		 * log.info("getNumberOfElements : {}", list.getNumberOfElements());
		 * // 最初のページですか？
		 * log.info("isFirst : {}", list.isFirst());
		 * // 最後のページですか？
		 * log.info("isLast : {}", list.isLast());
		 * // 最新の投稿を表示
		 * log.info("getContent : {}", list.getContent().get(0).toString());
		 * // 2) Lambda オブジェクト, Stream : List, Set, Map : Iterable 客彩 /中間演算, 最終演算
		 * // Iterableは、全体を巡回するために出てきたオブジェクト>> +中間演算機能を含むStream
		 */

		return list;
	}

	//
	// /**
	// *1) 簡易照会 : 投稿全リスト照会 2) 検索照会 : 照会メソッドの使用
	// *
	// *@param searchItem: boardTitle findByboardTitle
	// *@param searchWord：ナリ
	// *@return
	// */
	// public List<BoardDTO> selectAll(String searchItem, String searchWord) {
	//// 2) 検索照会
	// List<BoardEntity> temp = null;
	//
	// switch(searchItem){
	// case "boardTitle":
	// temp = boardRepository.findByBoardTitleContains(searchWord,
	// Sort.by(Direction.DESC, "createDate"));
	// break;
	// case "boardWriter":
	// temp = boardRepository.findByBoardWriterContains(searchWord,
	// Sort.by(Direction.DESC, "createDate"));
	// 壊す;
	// ケース「ボードコンテンツ」:
	// temp = boardRepository.findByBoardContentContains(searchWord,
	// Sort.by(Direction.DESC, "createDate"));
	// 壊す;
	// }
	//
	//// 단순 조회
	//// List<BoardEntity> temp =
	// boardRepository.findAll(Sort.by(Sort.Direction.DESC, "createDate"));
	// List<BoardDTO> list = new ArrayList<>();
	//
	// log.info("======= 총 글 개수 : {}", temp.size());
	// temp.forEach((エンティティ) -> list.add(BoardDTO.toDTO(エンティティ)));
	//
	// リストを返す;
	// }

	/**
	 * 転送されたBoardDTOをDBに保存添付ファイルかどうかに応じてDBの2つの列値を修正
	 * 
	 * @param boardDTO
	 */
	public void insertBoard(BoardDTO boardDTO) {
		MultipartFile uploadFile = boardDTO.getUploadFile();
		String savedFileName = null;
		String originalFileName = null;

		if (!uploadFile.isEmpty()) {
			savedFileName = FileService.saveFile(boardDTO.getUploadFile(), uploadPath);
			originalFileName = uploadFile.getOriginalFilename();

		}

		boardDTO.setSavedFileName(savedFileName);
		boardDTO.setOriginalFileName(originalFileName);

		BoardEntity entity = BoardEntity.toEntity(boardDTO);
		log.info("파일 저장 경로 : {}", uploadPath);

		boardRepository.save(entity);

	}

	public BoardDTO selectOne(Long boardSeq) {
		Optional<BoardEntity> temp = boardRepository.findById(boardSeq);

		if (!temp.isPresent()) {
			return null;
		}
		// BoardEntity エンティティ = temp.get();
		// BoardDTO.toDTO(エンティティ)を返します。

		return BoardDTO.toDTO(temp.get());
	}

	/**
	 * ヒット数増加 1) ヒット: findById(boardSEQ)
	 * 
	 * save() : 新しくデータを insert : 既存データの場合 update PK で照会した後 set をしながら値を変えると
	 * update update from board set hit_count = hit_count+1 where board_seq = ?;
	 * 
	 * @param boardSeq
	 */
	@Transactional
	public void incrementHitCount(Long boardSeq) {
		Optional<BoardEntity> temp = boardRepository.findById(boardSeq);

		if (!temp.isPresent()) {
			return;
		}
		BoardEntity entity = temp.get();
		entity.setHitCount(entity.getHitCount() + 1);

	}

	/**
	 * 
	 * 投稿を削除+添付ファイルを削除
	 * 
	 * @param boardSeq
	 */
	@Transactional
	public void deleteOne(Long boardSeq) {
		Optional<BoardEntity> temp = boardRepository.findById(boardSeq);

		if (!temp.isPresent()) {
			return;
		}

		// tempの後にsavedfilenameが存在する場合は、物理的に削除

		BoardEntity entity = temp.get();
		String savedFileName = entity.getSavedFileName();
		if (savedFileName != null) {
			String fullPath = uploadPath + "/" + savedFileName;
			FileService.deleteFile(fullPath);
		}

		boardRepository.deleteById(boardSeq);
	}

	/**
	 * DBへの修正処理
	 * 
	 * @param boardDTO
	 */
	@Transactional
	public void updateBoard(BoardDTO boardDTO) {
		/*
		 * ファイルがある場合文のみ修正、添付ファイルはそのまま！テキストを修正、ファイルを追加 -既存のファイルは？物理的に削除、文修正、ファイル名も修正
		 * 
		 * ファイルがない場合 文のみ修正 (以前作業) 文も修正、ファイル追加 -文修正、ファイル名も追加
		 * 
		 */
		// 1) 添付ファイルがあるか確認

		MultipartFile file = boardDTO.getUploadFile();

		String newFile = !file.isEmpty() ? file.getOriginalFilename() : null;

		// 1) 修正したいデータがあるか確認

		Optional<BoardEntity> temp = boardRepository.findById(boardDTO.getBoardSeq());

		if (!temp.isPresent()) {
			return;
		}
		// 2）場合はdto ->エンティティに変換

		BoardEntity entity = temp.get();
		entity.setUpdateDate(LocalDateTime.now());

		// 既存のファイルがDBに保存されていることを確認する

		String oldFile = entity.getSavedFileName();

		// （1）既存のファイルがあり、アップロードしたファイルもある場合
		// -ハードディスクでは既存のファイルを削除、アップロードしたファイルを保存
		// -DBにアップロードしたファイルで2つの列を更新する
		// (2) 既存ファイルがなくアップロードしたファイルもある場合
		// -ハードディスクにアップロードしたファイルを保存する
		// -DBにアップロードしたファイルで2つの列を更新する

		if (newFile != null) { // アップロードファイルがある場合

			String savedFileName = null;
			savedFileName = FileService.saveFile(file, uploadPath);
			entity.setOriginalFileName(newFile);
			entity.setSavedFileName(savedFileName);

			if (oldFile != null) { // 既存のファイルがある場合

				String fullPath = uploadPath + "/" + oldFile;
				FileService.deleteFile(fullPath);
			}
		}

		entity.setBoardTitle(boardDTO.getBoardTitle());
		entity.setBoardContent(boardDTO.getBoardContent());

		// if (newFile != null) { //업로드 일본있으면
		// if (oldFile != null) { //기존 일본있으면
		// 文字列フルパス = アップロードパス + "/" + oldFile;
		// FileService.deleteFile(フルパス);
		// }
		// 文字列保存ファイル名 =FileService.saveFile(ファイル, アップロードパス);
		// エンティティ.setOriginalFileName(newFile);
		// エンティティ.setSavedFileName(savedFileName);
		// }
		//
		// エンティティ.setBoardTitle(boardDTO.getBoardTitle());
		// エンティティ.setBoardContent(boardDTO.getBoardContent());

	}

	/**
	 * file名が入っている2つの列の値をnullに！
	 * 
	 * Update構文またはSQLを2回実行すると、@Transactional
	 * 
	 * @param boardSeq
	 */

	@Transactional
	public void deleteFile(Long boardSeq) {
		Optional<BoardEntity> temp = boardRepository.findById(boardSeq);

		if (temp.isPresent()) {
			BoardEntity entity = temp.get();
			entity.setOriginalFileName(null);
			entity.setSavedFileName(null);
		}
	}
}
