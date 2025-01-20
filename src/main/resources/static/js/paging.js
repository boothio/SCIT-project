/**
 *  ページング呼び出しコード
 */

function pageFormSubmit(page) {
	$('#requestPage').val(page);
	$('#searchForm').submit();
	// location.href = "/board/boardList?page=" + page;
	// location.href = "/board/boardList?page=${page}";
	// 検索後にページングすると、検索が解除されます
}
