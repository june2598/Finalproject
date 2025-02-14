// 아이디 중복 확인 기능
document.getElementById('idValid').addEventListener('click', async function() {
    const memberId = document.getElementById('memberId').value;

    if (!memberId) {
            document.getElementById('message').innerText = '아이디를 입력해 주세요.';
            return;
        }
    console.log('입력한 아이디:', memberId);
     try {
            const response = await ajax.post('/api/auth/check-member-id', {
                memberId: memberId
            });
            console.log('서버 응답:', response);
            document.getElementById('message').innerText = response.message; // 서버에서 전송된 메시지를 HTML 요소에 표시
        } catch (error) {
            console.error('서버 응답 처리 중 오류:', error);
            document.getElementById('message').innerText = '서버 응답을 처리하는 중 오류가 발생했습니다.';
        }
});

// 비밀번호 확인 기능
document.getElementById('pwConfirm').addEventListener('input', function() {
    const pw = document.getElementById('pw').value;
    const pwConfirm = document.getElementById('pwConfirm').value;

    if (pw !== pwConfirm) {
        document.getElementById('pwConfirm').setCustomValidity('비밀번호가 일치하지 않습니다.');
    } else {
        document.getElementById('pwConfirm').setCustomValidity(''); // 유효성 초기화
    }
});

let isVerified = false; // 인증 여부를 저장하는 변수


// 이메일 인증 코드 전송 기능
document.getElementById('sendVerificationEmail').addEventListener('click', async function() {
    const email = document.getElementById('email').value;

    try {
            const response = await ajax.post('/api/auth/send-verification-email', { email });
              alert(response.message); // 서버에서 전송된 메시지 표시
                } catch (error) {
                    console.error('Error parsing JSON response:', error);
                    alert('이메일 전송 중 오류가 발생했습니다.');
                }
});

// 인증하기 버튼 기능
document.getElementById('emailValid').addEventListener('click', async function() {
    const email = document.getElementById('email').value;
    const code = document.getElementById('code').value;

      try {
             const response = await ajax.post('/api/auth/verify-code', {
                 email: email,
                 code: code
             });

             if (response && response.success) {
                 isVerified = true; // 인증 성공
                 alert('인증이 완료되었습니다. 회원가입 버튼을 활성화합니다.');
                 document.querySelector('button[type="submit"]').disabled = false; // 회원가입 버튼 활성화
             } else {
                 alert('인증 실패: ' + response.message);
             }
         } catch (error) {
             console.error('인증 오류:', error);
             alert('인증 중 오류가 발생했습니다.');
         }
});

// 회원가입 기능
document.getElementById('registerForm').addEventListener('submit', async function(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    if (!isVerified) {
        alert('이메일 인증을 완료해야 회원가입을 진행할 수 있습니다.');
        return;
    }

    const formData = {
        memberId: document.getElementById('memberId').value,
        pw: document.getElementById('pw').value,
        pwConfirm: document.getElementById('pwConfirm').value,
        tel: document.getElementById('tel').value,
        email: document.getElementById('email').value,
        code: document.getElementById('code').value,
    };

    try {
            const response = await ajax.post('/api/auth/register', formData);
            if (response) {
                if (response.success) {
                    alert(response.message); // 성공 메시지 표시
                    window.location.href = '/'; // 초기 화면으로 리다이렉트
                } else {
                    alert('회원가입 오류: ' + response.message); // 오류 메시지 표시
                }
            } else {
                console.error('응답이 정의되지 않았습니다.');
                alert('응답이 정의되지 않았습니다.');
            }
        } catch (error) {
            if (error.response) {
                const jsonResponse = await error.response.json(); // 응답을 JSON으로 변환
                alert('회원가입 오류: ' + jsonResponse.message); // 오류 메시지 표시
            } else {
                console.error('회원가입 오류:', error);
                alert('회원가입 중 오류가 발생했습니다.');
            }
        }
    });

// 초기 상태에서 회원가입 버튼 비활성화
document.querySelector('button[type="submit"]').disabled = true;
