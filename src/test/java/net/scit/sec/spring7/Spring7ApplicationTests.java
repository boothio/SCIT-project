package net.scit.sec.spring7;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import net.scit.sec.spring7.entity.BoardEntity;
import net.scit.sec.spring7.repository.BoardRepository;

@SpringBootTest
class Spring7ApplicationTests {

	@Test
	void contextLoads() {
	}

//	메서드 주입방식
	@Autowired
	BoardRepository repo;

	@Test
	void testInsertBoard() {
		String[] w = { "김콩쥐", "김팥쥐", "박장화", "박홍련", "정햇님", "정달님", "박호랭이" };
		String[] c = { "나리나리 게나리 입에따다 물고요", "송아지 송아지 얼룩송아지 엄마소도 얼룩소", "강아지 망아지 얼룩 고양이 엄마돼지는 얼룩 돼지",
				"엄마가 섬그늘에 굴따러 가면 아기는 혼자 남아", "동구밖 과수월길 아카시아 꽃이 활짝 폈네", "바람이 머물다간 들판에 모락모락 피어나는 저녁 연기",
				"파란하는 파란하늘 드리운 푸른 언덕에...", "동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세",
				"이 기상과 이 맘으로 충성을 다하여 괴로우나 즐거우나 나라 사랑하세", "가을 하늘 공활한데 높고 구름없이 밝은 달은 우리 기상일세" };
		for (int i = 0; i < 30; i++) {
			int idxW = (int) (Math.random() * w.length);
			int idxC = (int) (Math.random() * c.length);
			int idxT = (int) (Math.random() * c.length);

			String writer = w[idxW];
			String content = c[idxC];
			String title = "제목: " + c[idxT].substring(idxT, idxT+4) + "...";

			BoardEntity entity = new BoardEntity();
			entity.setBoardWriter(writer);
			entity.setBoardContent(content);
			entity.setBoardTitle(title);

			repo.save(entity);
		}
		System.out.println("저장완료!!!");
	}
}
