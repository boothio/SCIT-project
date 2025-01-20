package net.scit.sec.spring7.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.scit.sec.spring7.entity.BoardEntity;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

//	検索+ページング機能のクエリメソッド宣言
	Page<BoardEntity> findByBoardTitleContains(String searchWord, PageRequest of);

	Page<BoardEntity> findByBoardWriterContains(String searchWord, PageRequest of);

	Page<BoardEntity> findByBoardContentContains(String searchWord, PageRequest of);

	
	//	検索機能のクエリメソッド宣言
//	List<BoardEntity> findByBoardTitleContains(String searchWord, Sort by);
//
//	List<BoardEntity> findByBoardWriterContains(String searchWord, Sort by);
//
//	List<BoardEntity> findByBoardContentContains(String searchWord, Sort by);


}
