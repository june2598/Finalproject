const pwInput = document.getElementById("pw");
const pwMessage = document.getElementById('pwMessage');
const telInput = document.getElementById("tel");
const telMessage = document.getElementById('telMessage');
const emailMessage = document.getElementById('emailMessage');
const emailInput = document.getElementById('email');
const sendEmailBtn = document.getElementById('sendVerificationEmail');
const modifyBtn = document.getElementById('request-modify');
let isVerified = false; // 이메일 인증 여부

document.getElementById("email").addEventListener("input", function () {
  isVerified = false; // 이메일 변경 시 인증 상태 초기화
  modifyBtn.setAttribute("disabled", true); // 수정 완료 버튼 다시 비활성화
});

// 비밀번호 입력 시 실시간 검사
pwInput.addEventListener("input", function () {
  const pw = pwInput.value;

  // 비밀번호 정규식 : 8~15자리, 특수문자, 대문자, 숫자를 반드시포함
  const pwRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+{}:<>?])[A-Za-z\d!@#$%^&*()_+{}:<>?]{8,15}$/;

  if (!pw) {
    pwMessage.innerText = "";
  } else if (!pwRegex.test(pw)) {
    pwMessage.innerText = "비밀번호는 8~15자이며, 대소문자, 숫자, 특수문자를 포함해야 합니다.";
    pwMessage.classList.add("text-red-500");
    pwMessage.classList.remove("text-green-500");
  } else {
    pwMessage.innerText = "사용 가능한 비밀번호입니다.";
    pwMessage.classList.add("text-green-500");
    pwMessage.classList.remove("text-red-500");
  }
});

// 전화번호 입력 시 실시간 검사 + 하이픈 입력 방지
telInput.addEventListener("input", function () {
  let tel = this.value.replace(/\D/g, ""); // 숫자 이외의 문자 제거

  // 최대 11자리까지만 입력 가능하도록 제한
  if (tel.length > 11) {
    tel = tel.slice(0, 11);
  }

  this.value = tel; // 입력 필드 업데이트

  if (!tel) {
    telMessage.innerText = "";
  } else if (tel.length < 10 || tel.length > 11) {
    telMessage.innerText = "유효한 전화번호를 입력하세요.";
    telMessage.classList.add("text-red-500");
    telMessage.classList.remove("text-green-500");
  } else {
    telMessage.innerText = "올바른 전화번호 형식입니다.";
    telMessage.classList.add("text-green-500");
    telMessage.classList.remove("text-red-500");
  }
});

// 이메일 입력시 실시간검사
emailInput.addEventListener("input", function () {
  const email = emailInput.value;

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  // 메시지 숨김 처리 (입력값이 없을 때)
  emailMessage.classList.toggle("hidden", email.length === 0);

  if (!emailRegex.test(email)) {
    emailMessage.innerText = "올바른 이메일 형식을 입력해 주세요.";
    emailMessage.classList.add("text-red-500");
    emailMessage.classList.remove("text-green-500");

    // 버튼 비활성화 및 스타일 변경
    sendEmailBtn.classList.replace('bg-blue-500', 'bg-gray-300');
    sendEmailBtn.classList.replace('text-white', 'text-black');
    sendEmailBtn.classList.remove('hover:bg-blue-600', 'cursor-pointer');
    sendEmailBtn.setAttribute('disabled', true);
  } else {
    emailMessage.innerText = "사용 가능한 이메일 입니다.";
    emailMessage.classList.add("text-green-500");
    emailMessage.classList.remove("text-red-500");

    // 버튼 활성화 및 스타일 변경
    sendEmailBtn.classList.replace('bg-gray-300', 'bg-blue-500');
    sendEmailBtn.classList.replace('text-black', 'text-white');
    sendEmailBtn.classList.add('hover:bg-blue-600', 'cursor-pointer');
    sendEmailBtn.removeAttribute('disabled');
  }
});




// 이메일 인증 코드 전송 기능
document.getElementById('sendVerificationEmail').addEventListener('click', async function () {
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
document.getElementById('emailValid').addEventListener('click', async function () {
  const email = document.getElementById('email').value.trim();
  const code = document.getElementById('code').value.trim();

  if (!code) {
    alert("인증 코드를 입력해주세요.");
    return;
  }

  try {
    const response = await ajax.post('/api/auth/verify-code', {
      email: email,
      code: code
    });

    if (response && response.success) {
      isVerified = true; // 인증 성공
      alert('인증이 완료되었습니다. 수정완료 버튼을 활성화합니다.');
      modifyBtn.classList.remove('bg-gray-500', 'text-black');  // 수정 완료 버튼 회색, 글자 색깔 삭제
      modifyBtn.classList.add('bg-blue-500', 'text-white');     // 수정 완료 버튼 파란색으로, 글씨 흰색으로 변경
      modifyBtn.removeAttribute('disabled'); // 수정 완료 버튼 활성화
      document.getElementById('email').setAttribute('disabled', true); // 이메일 인증 완료되면 이메일 수정 더 못함
      document.getElementById('email').classList.add('bg-gray-500');  // 이메일 수정하고 이메일 인증 완료되면 다시 수정할 수 없음

      // 인증 성공 메시지 추가
      const emailSuccessMessage = document.getElementById("emailSuccessMessage");
      emailSuccessMessage.innerText = "이메일 인증 완료";
      emailSuccessMessage.classList.remove("hidden");
    } else {
      alert('인증 실패: ' + response.message);
    }
  } catch (error) {
    console.error('인증 오류:', error);
    alert('인증 중 오류가 발생했습니다.');
  }
});



modifyBtn.addEventListener('click', async () => {

  if (!isVerified) {
    alert("이메일 인증을 완료해주세요.");
    return;
  }

  const memberId = document.getElementById("memberId").value.trim(); // 회원 ID 값 가져오기
  const pw = document.getElementById('pw').value.trim();
  const tel = document.getElementById('tel').value.trim();
  const email = document.getElementById('email').value.trim();


  // 클라이언트 유효성 검사

  if (!validateForm(pw, tel, email)) {
    return;
  }

  try {
    const response = await ajax.post('/api/member/update', {
      memberId: memberId,
      pw: pw,
      tel: tel,
      email: email
    });

    if (response && response.success) {
      alert('회원정보가 수정 되었습니다.');
      window.location.href = "/member-info"; // 회원 정보 페이지로 이동
    } else {
      alert(response?.message || "수정 중 오류가 발생했습니다.");
    }
  } catch (error) {
    console.error('회원 정보 수정 중 오류', error);
    alert('회원정보 수정 중 오류가 발생했습니다.')
  };
});

function validateForm(pw, tel, email) {

  let isValid = true;  // 모든 검사를 통과하면 true 유지

  // 초기화: 오류 메시지 숨기기
  pwMessage.classList.add('hidden');
  telMessage.classList.add('hidden');
  emailMessage.classList.add('hidden');

  // 비밀번호 길이 검증
  if (pw.length < 8 || pw.length > 15) {
    pwMessage.classList.remove('hidden');
    pwMessage.innerText = '비밀번호는 8자리 이상 15자리 이하여야 합니다.';
    isValid = false;
  }

  // 비밀번호 정규표현식 검증
  const pwRegx = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+{}:<>?]).*$/;

  if (!pwRegx.test(pw)) {
    pwMessage.classList.remove('hidden');
    pwMessage.innerText = ' 비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다.'
    isValid = false;
  }

  // 전화번호 검증
  const telRegx = /^\d{10,11}$/;
  if (!telRegx.test(tel)) {

    telMessage.classList.remove('hidden');
    telMessage.innerText = '휴대폰번호를 제대로 입력해주세요.';
    isValid = false;
  }

  // 이메일 검증
  const emailRegx = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!emailRegx.test(email)) {

    emailMessage.classList.remove('hidden');
    emailMessage.innerText = '올바른 이메일 형식을 입력해주세요.';
    isValid = false;
  }

  return isValid;
}