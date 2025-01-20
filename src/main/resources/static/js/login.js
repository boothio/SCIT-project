/*
	ログイン時に必要な検証タスク
*/


$(function () {

	$('#submitBtn').on('click', login);
});

// 1) ID検証(長さ+重複確認)

function login() {
	let userId = $('#userId').val();

	// 長さチェック

	if (userId.trim().length < 3 || userId.trim().length > 5) {
		$('#confirmId').css('color', 'red');
		$('#confirmId').html('IDは3〜5文字以内');


		return false;
	}

	let userPwd = $('#userPwd').val();
	// パスワード長チェック

	if (userPwd.trim().length < 3 || userPwd.trim().length > 5) {
		$('#confirmPwd').css('color', 'red');
		$('#confirmPwd').html('パスワードは3〜5文字以内');

		return false;
	}

	return true;
}

