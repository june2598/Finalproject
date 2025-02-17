
const authBtn = document.getElementById('emailValid');
const email = document.getElementById('email').value;

const modifyFormBtn = document.getElementById('show-modify-form');

// 인증 요청 버튼
document.getElementById('request-modify').addEventListener('click', async () => {
  

  try {
    const response = await ajax.post('/api/auth/send-verification-email', { email });
    alert(response.message); // 서버에서 전송된 메시지 표시

    authBtn.removeAttribute('disabled');  // 인증하기 버튼 비활성화 하제
    authBtn.style.display = 'block';       // 인증하기 버튼 숨김 해제
  } catch (error) {
    console.error('이메일 전송 오류', error);
    alert('이메일 전송 중 오류가 발생했습니다.');
  }
});

authBtn.addEventListener('click', async () => {

  const code = document.getElementById('code').value;
  try {
    const response = await ajax.post('/api/auth/verify-code', {
      email: email,
      code: code
    });

    if (!response || !response.success) {
      alert('인증 실패: ' + (response?.message || "잘못된 응답입니다."));
      return;
    }

    alert('인증이 완료되었습니다. 회원정보 수정화면으로 넘어가실수 있습니다.');

    modifyFormBtn.removeAttribute('disabled');
    modifyFormBtn.style.display = 'block';

  } catch (error) {
    console.error('인증 코드 확인 오류', error);
    alert('인증 코드 확인 중 오류가 발생했습니다.');
  }
});

