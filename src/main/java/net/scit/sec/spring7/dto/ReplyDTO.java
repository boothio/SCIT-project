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
import net.scit.sec.spring7.entity.ReplyEntity;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class ReplyDTO {
	private Long replySeq;
	private Long boardSeq;
	private String replyWriter;
	private String replyContent;
	private LocalDateTime createDate;

	// Entity --> DTO
	public static ReplyDTO toDTO(ReplyEntity replyEntity, Long bSeq) {
		return ReplyDTO.builder()
				.replySeq(replyEntity.getReplySeq())
				.boardSeq(bSeq)
				.replyWriter(replyEntity.getReplyWriter())
				.replyContent(replyEntity.getReplyContent())
				.createDate(replyEntity.getCreateDate())
				.build();
	}

}
