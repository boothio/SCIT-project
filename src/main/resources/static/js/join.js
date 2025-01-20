/*
	会員登録時に必要な検証作業
*/

let idCheck = false;
let pwdCheck = false;

$(function () {
	$('#userId').on('keyup', confirmId);
	$('#userPwd').on('focus', function () {
		$('#userPwdCheck').val('');
	});
	$('#submitBtn').on('click', join);
});

// 1) ID検証(長さ+重複確認)

function confirmId() {
	let userId = $('#userId').val();

	// 長さチェック

	if (userId.trim().length < 3 || userId.trim().length > 5) {
		$('#confirmId').css('color', 'red');
		$('#confirmId').html('아이디는 3~5자 이내');


		return;
	}

	$('#confirmId').html('');
	$.ajax({
		url: '/user/idCheck'
		, method: 'POST'
		, data: { "userId": userId }
		, success: function (resp) { // respがtrueの場合、会員登録可能

			if (resp) {
				$('#confirmId').css('color', 'blue');
				$('#confirmId').html('使用できるIDです。');
				idCheck = true;

			} else {
				$('#confirmId').css('color', 'red');
				$('#confirmId').html('使用できないIDです。');
				idCheck = false;

			}
		}
	});
}

// 2) 会員加入のための残りの検証作業

function join() {
	let userPwd = $('#userPwd').val();
	// パスワード長チェック

	if (userPwd.trim().length < 3 || userPwd.trim().length > 5) {
		$('#confirmPwd').css('color', 'red');
		$('#confirmPwd').html('비밀번호는 3~5자 이내');

		pwdCheck = false;
		return false;
	}
	$('#confirmPwd').html('');

	let userPwdCheck = $('#userPwdCheck').val();
	// パスワード同じチェック

	if (userPwd.trim() != userPwdCheck.trim()) {
		$('#confirmPwd').css('color', 'red');
		$('#confirmPwd').html('비밀번호가 다르게 입력되었습니다.');

		pwdCheck = false;
		return false;
	}

	// 実名長チェック

	let userName = $('#userName').val();
	if (userName.trim().length < 3 || userName.trim().length > 5) {
		$('#confirmUserName').css('color', 'red');
		$('#confirmUserName').html('이름은 3~5자 이내');

		return false;
	}


	// メール長チェック

	let email = $('#email').val();
	if (email.trim().length < 1) {
		$('#confirmEmail').css('color', 'red');
		$('#confirmEmail').html('이메일을 입력하세요');

		return false;
	}

	return true;
}