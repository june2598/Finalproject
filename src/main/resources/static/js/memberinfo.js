// // 회원정보 수정 버튼
// document.getElementById('request-modify').addEventListener('click', async () => {
//   const email = document.getElementById('email').value;

//   try {
//     const response = await ajax.post('/api/auth/send-verification-email', { email });
//     alert(response.message); // 서버에서 전송된 메시지 표시

//     document.getElementById('auth-input').classList.remove('hidden'); // 인증코드 input과 인증하기 버튼 보이기
//     document.getElementById('code').removeAttribute('disabled');      // 인증코드 input disabled 해제
//     document.getElementById('request-modify').style.display = 'none'; // 회원 정보 수정 버튼 숨김

//   } catch (error) {
//     console.error('이메일 전송 오류', error);
//     alert('이메일 전송 중 오류가 발생했습니다.');
//   }
// });

// // 인증하기 버튼 기능
// document.getElementById('emailValid').addEventListener('click', async () => {
//   const email = document.getElementById('email').value;
//   const code = document.getElementById('code').value;

//   try {
//     const response = await ajax.post('/api/auth/verify-code', {
//       email: email,
//       code: code
//     });

//     if (!response || !response.success) {
//           alert('인증 실패: ' + (response?.message || "잘못된 응답입니다."));
//           disableEditMode();
//           return;
//         }
//         alert('인증이 완료되었습니다. 회원정보 수정을 활성화합니다.');
//         document.getElementById('code').setAttribute('disabled',true);
//         enableEditMode();
//       } catch (error) {
//         console.error('인증 오류:', error);
//         alert('서버 오류: 인증 처리 중 문제가 발생했습니다.');
//       }
//     });

// // 회원정보 수정 완료 시 인증 상태 초기화
// document.getElementById('modify').addEventListener('click', () => {
//  // 인증 상태 초기화
//  disableEditMode();
// });


// // 인증 성공 후 회원 수정 활성화
// function enableEditMode() {
//   document.getElementById('pw').removeAttribute('disabled');
//   document.getElementById('tel').removeAttribute('disabled');
//   document.getElementById('email').removeAttribute('disabled');
//   document.getElementById('modify').style.display = 'block';
//   document.getElementById('modify').removeAttribute('disabled');
//   document.getElementById('request-modify').style.display = 'none'; // 회원 정보 수정 버튼 숨김
// }


// // 수정 양식 비활성화 (인증 실패 또는 페이지 로드 시)
// function disableEditMode() {
//   document.getElementById('pw').setAttribute('disabled', true);
//   document.getElementById('tel').setAttribute('disabled', true);
//   document.getElementById('email').setAttribute('disabled', true);

//   document.getElementById('modify').style.display = 'none'; // 수정 완료 버튼 숨김
//   document.getElementById('modify').setAttribute('disabled', true);

//   document.getElementById('request-modify').style.display = 'block'; // 회원 정보 수정 버튼 다시 표시
//   document.getElementById('auth-input').classList.add('hidden'); // 인증코드 입력칸 숨김
//   document.getElementById('code').value = ''; // 인증코드 입력 필드 초기화

// }


