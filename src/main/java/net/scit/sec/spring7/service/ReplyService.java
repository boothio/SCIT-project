package net.scit.sec.spring7.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.sec.spring7.dto.ReplyDTO;
import net.scit.sec.spring7.entity.BoardEntity;
import net.scit.sec.spring7.entity.ReplyEntity;
import net.scit.sec.spring7.repository.BoardRepository;
import net.scit.sec.spring7.repository.ReplyRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyService {
	private final BoardRepository boardRepository;
	private final ReplyRepository replyRepository;

	/**
	 * 受け取った値をエンティティに変更した後、DBに保存
	 * 
	 * @param replyDTO
	 */
	public void replyInsert(ReplyDTO replyDTO) {
		// 1) 親があるかどうかを調べる

		Optional<BoardEntity> temp = boardRepository.findById(replyDTO.getBoardSeq());

		if (!temp.isPresent())
			return;

		// 2) 両親を取り出す

		BoardEntity boardEntity = temp.get();

		// 3）2つを渡してentityを返す

		ReplyEntity replyEntity = ReplyEntity.toEntity(replyDTO, boardEntity);

		// 4) DBに保存(save)

		replyRepository.save(replyEntity);
	}

	/**
	 * boardSeqに対応するコメント全体を見る
	 * 
	 * @param boardSeq
	 * @return
	 */
	public List<ReplyDTO> replyAll(Long boardSeq) {
		// 親があるかどうかを調べる

		Optional<BoardEntity> temp = boardRepository.findById(boardSeq);

		// 2) コメント照会のための Query Method

		List<ReplyEntity> entityList = replyRepository.findAllByBoardEntity(temp,
				Sort.by(Sort.Direction.DESC, "replySeq"));

		log.info("コメント数 : {}", entityList.size());

		// 3) List<ReplyDTO>を宣言

		List<ReplyDTO> list = new ArrayList<>();

		// DTOに変換

		entityList.forEach((entity) -> list.add(ReplyDTO.toDTO(entity, boardSeq)));
		return list;
	}

	/**
	 * コメント削除処理
	 * 
	 * @param replySeq
	 */
	public void replyDelete(Long replySeq) {
		Optional<ReplyEntity> temp = replyRepository.findById(replySeq);

		if (!temp.isPresent())
			return;

		replyRepository.deleteById(replySeq);

	}

	/**
	 * コメントを見る
	 * 
	 * @param replySeq
	 * @return
	 */
	public ReplyDTO replySelectOne(Long replySeq) {
		Optional<ReplyEntity> temp = replyRepository.findById(replySeq);

		if (!temp.isPresent())
			return null;

		ReplyEntity entity = temp.get();

		ReplyDTO replyDTO = ReplyDTO.toDTO(entity, entity.getBoardEntity().getBoardSeq());

		return replyDTO;
	}

	@Transactional
	public void replyUpdateProc(ReplyDTO replyDTO) {
		// 1) 親があるかどうかを調べる

		Optional<BoardEntity> temp = boardRepository.findById(replyDTO.getBoardSeq());

		if (!temp.isPresent())
			return;

		// 2) 両親を取り出す

		BoardEntity boardEntity = temp.get();

		Optional<ReplyEntity> replyEntityTemp = replyRepository.findById(replyDTO.getReplySeq());
		if (!replyEntityTemp.isPresent())
			return;

		ReplyEntity replyEntity = replyEntityTemp.get();
		replyEntity.setReplyContent(replyDTO.getReplyContent());
	}

}
