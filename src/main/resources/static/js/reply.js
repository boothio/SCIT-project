$(function () {

	// 添付ファイルがある場合は削除

	$('.deleteFile').on('click', function () {
		let boardSeq = $(this).attr('data-seq');
		let searchItem = $(this).attr('data-searchI');
		let searchWord = $(this).attr('data-searchW');
		let answer = confirm('本当に削除しますか？');
		if (answer) {
			location.href = "/board/deleteFile?boardSeq=" + boardSeq + "&searchItem=" + searchItem + "&searchWord=" + searchWord;
		} else {
			alert('削除操作をキャンセルします。');
		}

	});


	init(); // フルコメントを読んで画面に出力


	// コメントを入力

	$('#replyBtn').on('click', replyInsert);
	$('#replyUpdateProc').on('click', replyUpdateProc);
	$('#replyCancel').on('click', replyCancel);

});


// コメントの初期化

function init() {
	let boardSeq = $('#boardSeq').val()

	$.ajax({
		url: '/reply/replyAll'
		, method: 'GET'
		, data: { 'boardSeq': boardSeq }
		, success: output
		// シリアル番号（seqではない）/コメント内容/書き込み/日付/ボタン


	});
}

// コメントを入力

function replyInsert() {
	let replyContent = $('#replyContent').val();
	let boardSeq = $('#boardSeq').val();
	if (replyContent.trim().length == 0) {
		alert(boardSeq);
		return;
	}

	let sendData = { "boardSeq": boardSeq, "replyContent": replyContent };

	// ajaxでコメントを送信

	$.ajax({
		url: '/reply/replyInsert'
		, method: 'POST'
		, data: sendData
		, success: function (resp) {
			alert('転送');
			console.log(resp);
			init();
			$('#replyContent').val('');
		}
	});

}

// コメント出力
// シリアル番号（seqではない）/コメント内容/書き込み/日付/ボタン

function output(resp) {
	let loginId = $('#loginId').val();
	let tag = ``;
	tag += `<table>
            <tr>
                <th>シリアル番号</th>
                <th>内容</th>
                <th>執筆者</th>
                <th>日付</th>
                <th></th>
            </tr>`;
	$.each(resp, function (index, item) {	// 応答 = [{},{},{},{}]

		tag += `<tr>
                <td class='no'>${index + 1}</td>
                <td class='content'>${item['replyContent']}</td>
                <td class='writer'>${item['replyWriter']}</td>
                <td class='date'>${item['createDate'].substr(0, 10)}</td>
                <td class='btns'>
                    <input type="button" value="삭제" class="btn btn-danger deleteBtn"
					data-seq="${item['replySeq']}"
					${item['replyWriter'] == loginId ? '' : 'disabled'}>
                    <input type="button" value="수정" class="btn btn-secondary updateBtn"
					data-seq="${item['replySeq']}"
					${item['replyWriter'] == loginId ? '' : 'disabled'}>
                </td>
            </tr>`;
	});

	tag += `</table>`;
	$('#reply_list').html(tag);

	$('.deleteBtn').on('click', replyDelete);
	$('.updateBtn').on('click', replyUpdate);
}

function replyDelete() {
	let replySeq = $(this).attr('data-seq');

	let answer = confirm('削除しますか？');

	if (!answer) return;

	$.ajax({
		url: '/reply/replyDelete'
		, method: 'GET'
		, data: { "replySeq": replySeq }
		, success: init
	});
}
/* 修正のための検索関数*/
function replyUpdate() {
	let replySeq = $(this).attr('data-seq');

	$.ajax({
		url: '/reply/replyUpdate'
		, method: 'GET'
		, data: { "replySeq": replySeq }
		, success: function (resp) {
			alert("検索成功");
			let content = resp['replyContent'];
			// 挿してから

			$('#replyContent').val(content);
			let seq = resp['replySeq'];
			$('#replySeq').val(seq);
			// ボタンを反転する：入力ウィンドウが見えず、修正ウィンドウが見える

			$('#replyBtn').css('display', 'none');
			$('#replyUpdateProc').css('display', 'inline-block');
			$('#replyCancel').css('display', 'inline-block');
		}

	})
}

/* コメント修正処理関数*/
function replyUpdateProc() {
	let replySeq = $(this).attr('data-seq');
	replySeq = $('#replySeq').val();
	let replyContent = $('#replyContent').val();
	let boardSeq = $('#boardSeq').val();




	if (replyContent.trim().length == 0) {
		alert(boardSeq);
		return;
	}

	let sendData = { "boardSeq": boardSeq, "replyContent": replyContent, "replySeq": replySeq };

	// ajaxでコメントを送信

	$.ajax({
		url: '/reply/replyUpdateProc'
		, method: 'POST'
		, data: sendData
		, success: function (resp) {
			alert('転送');
			console.log(resp);
			init();
			$('#replyContent').val('');
		}
	});
}

/* コメント編集キャンセル関数*/
function replyCancel() {
	let replySeq = $(this).attr('data-seq');
	console.log(replySeq);

	$('#replyBtn').css('display', 'inline-block');
	$('#replyUpdateProc').css('display', 'none');
	$('#replyCancel').css('display', 'none');

	$('#replyContent').val('');
}
