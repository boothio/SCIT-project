package net.scit.sec.spring7.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.sec.spring7.entity.BoardEntity;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class BoardDTO {
	private Long boardSeq;
	private String boardWriter;
	private String boardTitle;
	private String boardContent;
	private int hitCount;
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	// コメント数（BoardEntityで@formulaを使用して検索）

	private int replyCount;

	// 添付ファイルを保存するメンバー

	private MultipartFile uploadFile;

	// アップロードファイルがある場合はViewで使用する

	private String originalFileName;
	private String savedFileName;

	// Entity --> DTO

	public static BoardDTO toDTO(BoardEntity boardEntity) {
		return BoardDTO.builder()
				.boardSeq(boardEntity.getBoardSeq())
				.boardWriter(boardEntity.getBoardWriter())
				.boardTitle(boardEntity.getBoardTitle())
				.boardContent(boardEntity.getBoardContent())
				.hitCount(boardEntity.getHitCount())
				.createDate(boardEntity.getCreateDate())
				.updateDate(boardEntity.getUpdateDate())
				.originalFileName(boardEntity.getOriginalFileName())
				.savedFileName(boardEntity.getSavedFileName())
				.replyCount(boardEntity.getReplyCount())
				.build();
	}
}
