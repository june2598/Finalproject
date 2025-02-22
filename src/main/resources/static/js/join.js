document.addEventListener("DOMContentLoaded", function () {
  const memberIdInput = document.getElementById("memberId");
  const idValidBtn = document.getElementById("idValid");
  const idMessage = document.getElementById("idMessage");
  const pwInput = document.getElementById("pw");
  const pwConfirmInput = document.getElementById("pwConfirm");
  const pwMessage = document.getElementById("pwMessage");
  const pwConfirmMessage = document.getElementById("pwConfirmMessage");
  const telInput = document.getElementById("tel");
  const emailInput = document.getElementById("email");
  const sendVerificationBtn = document.getElementById("sendVerificationEmail");
  const emailValidBtn = document.getElementById("emailValid");
  const registerForm = document.getElementById("registerForm");
  const registerBtn = document.querySelector("button[type='submit']");
  let isVerified = false; // 이메일 인증 여부

  // 아이디 중복 확인 기능
  idValidBtn.addEventListener("click", async function () {
    const memberId = memberIdInput.value.trim();

    if (!memberId) {
      idMessage.innerText = "아이디를 입력해 주세요.";
      idMessage.classList.add("text-red-500");
      return;
    }

    try {
      const response = await ajax.post("/api/auth/check-member-id", { memberId });
      idMessage.innerText = response.message;
      idMessage.classList.toggle("text-green-500", response.success);
      idMessage.classList.toggle("text-red-500", !response.success);
    } catch (error) {
      console.error("서버 응답 처리 중 오류:", error);
      idMessage.innerText = "서버 응답을 처리하는 중 오류가 발생했습니다.";
      idMessage.classList.add("text-red-500");
    }
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

  // 비밀번호 확인 입력 시 실시간 검사
  pwConfirmInput.addEventListener("input", function () {
    const pw = pwInput.value;
    const pwConfirm = pwConfirmInput.value;

    if (!pwConfirm) {
      pwConfirmMessage.innerText = "";
    } else if (pw !== pwConfirm) {
      pwConfirmMessage.innerText = "비밀번호가 일치하지 않습니다.";
      pwConfirmMessage.classList.add("text-red-500");
      pwConfirmMessage.classList.remove("text-green-500");
    } else {
      pwConfirmMessage.innerText = "비밀번호가 일치합니다.";
      pwConfirmMessage.classList.add("text-green-500");
      pwConfirmMessage.classList.remove("text-red-500");
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


  // 이메일 인증 코드 전송 기능
  sendVerificationBtn.addEventListener("click", async function () {
    const email = emailInput.value.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+.[^\s@]+$/;

    if (!emailRegex.test(email)) {
      alert("올바른 이메일 형식을 입력해 주세요.");
      return;
    }

    try {
      const response = await ajax.post("/api/auth/send-verification-email/join", { email });
      alert(response.message);
    } catch (error) {
      console.error("이메일 인증 요청 중 오류 발생:", error);
      alert("이메일 인증 요청 중 오류가 발생했습니다.");
    }
  });

  // 이메일 인증 확인 기능
  emailValidBtn.addEventListener("click", async function () {
    const email = emailInput.value.trim();
    const code = document.getElementById("code").value.trim();

    try {
      const response = await ajax.post("/api/auth/verify-code", { email, code });

      if (response.success) {
        isVerified = true;
        alert("인증이 완료되었습니다. 회원가입 버튼을 활성화합니다.");
        registerBtn.disabled = false;
      } else {
        alert("인증 실패: " + response.message);
      }
    } catch (error) {
      console.error("인증 오류:", error);
      alert("인증 중 오류가 발생했습니다.");
    }
  });

  // 회원가입 기능
  registerForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    if (!isVerified) {
      alert("이메일 인증을 완료해야 회원가입을 진행할 수 있습니다.");
      return;
    }

    const pw = pwInput.value.trim();
    const pwConfirm = pwConfirmInput.value.trim();
    const tel = telInput.value.trim();
    const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,15}$/;
    const telRegex = /^\d{10,11}$/;

    

    // 유효성 검사 영역
    if (!pwRegex.test(pw)) {
      alert("비밀번호는 8~15자이며, 대소문자, 숫자, 특수문자를 포함해야 합니다.");
      return;
    }

    if (pw !== pwConfirm) {
      alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
      return;
    }

    if (!telRegex.test(tel)) {
      alert("유효한 전화번호를 입력하세요.");
      return;
    }

    const formData = {
      memberId: memberIdInput.value.trim(),
      pw,
      pwConfirm,
      tel: document.getElementById("tel").value.trim(),
      email: emailInput.value.trim(),
      code: document.getElementById("code").value.trim(),
    };

    try {
      const response = await ajax.post("/api/auth/register", formData);
      if (response.success) {
        alert(response.message);
        window.location.href = "/";
      } else {
        alert("회원가입 오류: " + response.message);
      }
    } catch (error) {
      console.error("회원가입 오류:", error);
      alert("회원가입 중 오류가 발생했습니다.");
    }
  });

  // 초기 상태에서 회원가입 버튼 비활성화
  registerBtn.disabled = true;
});
