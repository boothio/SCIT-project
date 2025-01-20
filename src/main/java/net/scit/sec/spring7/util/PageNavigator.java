package net.scit.sec.spring7.util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PageNavigator {
	// グループあたりのページ数

	private final int pagePerGroup = 10;
	// 1ページあたりの投稿数

	private int pageLimit;
	// ユーザーがリクエストしたページ

	private int page;
	// 総ページ数

	private int totalPages;
	// 総グループ数

	private int totalGroupCount;
	// ユーザーが要求したページが属するグループ

	private int currentGroup;
	// 現在のグループの最初のページ

	private int startPageGroup;
	// 現在のグループの最後のページ

	private int endPageGroup;

	public PageNavigator(int pageLimit, int page, int totalPages) {

		// メンバーの初期化

		this.pageLimit = pageLimit;
		this.page = page;
		this.totalPages = totalPages;

		// 総グループ数予算
		// 総文数152個なら16ページ2グループ

		totalGroupCount = totalPages / pagePerGroup; // 16/10 ==> 1

		totalGroupCount += (totalPages % pagePerGroup == 0) ? 0 : 1; // 16/10 ==> 1

		System.out.println("総グループ数" + totalGroupCount);

		// ユーザーがリクエストしたページの最初、最後
		// page : 15 , pageLimit : 10 ==> 1
		// 15.0/10 ==> 1.5
		// Math.ceil((double)page /pageLimit) ==> 2 -1
		// 1〜10の間のページをリクエストするとspg ==> 1
		// 11〜20の間のページをリクエストするとspg ==> 1

		startPageGroup = (int) ((Math.ceil((double) page / pageLimit)) - 1) * pageLimit + 1;
		System.out.println("リクエストしたページグループの最初のページ" + startPageGroup);

		// 1〜10の間のページをリクエストすると、epg ==> 10
		// 11〜20の間のページをリクエストすると、epg ==> 16
		// 3pをリクエストしたらspg = 1、epg 10
		// 14pをリクエストした場合、spg = 11、epg 16

		endPageGroup = (startPageGroup + pagePerGroup - 1) >= totalPages ? totalPages
				: (startPageGroup + pagePerGroup - 1);
		if (endPageGroup == 0)
			endPageGroup = 1;
		System.out.println("リクエストしたページグループの最後のページ" + endPageGroup);
		// 16pをリクエストしたら、現在のグループ？ 2
		// （166 -1）/10 ==？ 1+1==>2

		currentGroup = (page - 1) / pagePerGroup + 1;
		System.out.println("リクエストしたページグループの現在のページ" + currentGroup);

	}
}
