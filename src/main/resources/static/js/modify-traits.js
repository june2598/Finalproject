const updateFinishBtn = document.getElementById('update-finish-btn');
const cancelEditBtn = document.getElementById('cancel-edit-btn');
const overExpRtnError = document.getElementById("over-expRtn-error");


// 수정 완료 이벤트
updateFinishBtn.addEventListener('click', async (event) => {
  event.preventDefault();

  try {
    const response = await ajax.post('/api/member/traits/update-finish', {});

    if (response && response.success) {
      alert("성향 정보가 성공적으로 업데이트 되었습니다.");
      window.location.href = "/propensity-test/my-page";
    } else {
      alert("업데이트 실패: " + (response?.message || "알 수 없는 오류"));
    }
  } catch (error) {
    console.error("최종 저장 중 오류 발생 : ", error);
    alert("서버 오류가 발생했습니다.");
  }

});

// 수정 취소 이벤트
cancelEditBtn.addEventListener('click', async (event) => {
  event.preventDefault();

  try {
    const response = await ajax.post("/api/member/traits/cancel-edit", {});

    if (response && response.success) {
        alert("수정이 취소되었습니다.");
        window.location.href = "/propensity-test/my-page";  // 마이페이지로 이동
    } else {
        alert("수정 취소 실패: " + (response?.message || "알 수 없는 오류"));
    }
} catch (error) {
    console.error("수정 취소 중 오류 발생:", error);
    alert("서버 오류가 발생했습니다.");
}
});


// th:value에서 가져온 maxRtn과 expRtn 값 가져오기
const maxRtn = parseFloat(document.getElementById("maxRtn").value) || 0;
const expRtnElement = document.querySelector("#expReturn span");
let expRtn = parseFloat(expRtnElement.textContent.replace("% 이상", "")) || 0;

console.log("🔍 maxRtn:", maxRtn, "expRtn:", expRtn);

// 희망 수익률 유효 여부 체크
function checkExpRtnValidity() {
    if (isNaN(expRtn) || expRtn > maxRtn) {
        updateFinishBtn.setAttribute("disabled", true); // 수정 완료 버튼 비활성화
        updateFinishBtn.classList.remove('bg-blue-500','cursor-pointer');  // 기본색 제거, 커서속성해제, 숨김해제
        updateFinishBtn.classList.add('bg-gray-400','opacity-50'); // 수정 완료버튼 비활성화 색 구분
        overExpRtnError.classList.remove("hidden"); // 경고 메시지 표시
    } else {
        updateFinishBtn.removeAttribute("disabled");  // 비활성화 해제
        updateFinishBtn.classList.remove('bg-gray-400','opacity-50'); // 수정 완료 버튼 활성화
        updateFinishBtn.classList.add('bg-blue-500','cursor-pointer'); //기본색 활성화, 커서속성 재설정
        overExpRtnError.classList.add("hidden"); // 경고 메시지 숨김
    }
}

// MutationObserver로 expRtn 변경 감지
const observer = new MutationObserver(() => {
  let newExpRtn = parseFloat(expRtnElement.textContent.replace("% 이상", "")) || 0;
  if (newExpRtn !== expRtn) {
      expRtn = newExpRtn;
      console.log("희망 수익률 변경 감지:", expRtn);
      checkExpRtnValidity();
  }
});

// expRtnElement의 변화를 감지
observer.observe(expRtnElement, { childList: true, subtree: true });

document.addEventListener("DOMContentLoaded", () => {
  // 페이지 로드 시 초기 체크 실행
  checkExpRtnValidity();

});

