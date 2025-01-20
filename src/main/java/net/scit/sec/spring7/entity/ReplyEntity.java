package net.scit.sec.spring7.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.sec.spring7.dto.ReplyDTO;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Table(name = "reply")
public class ReplyEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reply_seq")
	private Long replySeq;

//	関係設定があるので親: board 子: reply			
//	reply 立場での多対一関係（子）
//	フェッチはLAZY、EAGERの2つです
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_Seq")
	private BoardEntity boardEntity;

	@Column(name = "reply_writer")
	private String replyWriter;

	@Column(name = "reply_content")
	private String replyContent;

	@Column(name = "create_date")
	@CreationTimestamp
	private LocalDateTime createDate;

	public static ReplyEntity toEntity(ReplyDTO replyDTO, BoardEntity entity) {
		return ReplyEntity.builder()
				.replySeq(replyDTO.getReplySeq())
				.boardEntity(entity)
				.replyWriter(replyDTO.getReplyWriter())
				.replyContent(replyDTO.getReplyContent())				
				.build();
	}

	// DTO --> Entity

}
