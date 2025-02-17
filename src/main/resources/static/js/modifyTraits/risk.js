

document.addEventListener('DOMContentLoaded', () => {

  // 현재 위험도 단계
  const currentRisk = document.getElementById('currentRisk').value;

  if (isNaN(currentRisk)) {
    currentRisk = 1;
  }

  const riskInputs = document.querySelectorAll('input[name="memberRisk"]');

  riskInputs.forEach(input => {
    if (input.value === currentRisk) {
      input.checked = true; // 현재 위험도 값에 미리 체크
      const label = document.querySelector(`label[for="${input.id}"]`);

      console.log(`현재 위험도: ${currentRisk}, 선택된 input id: ${input.id}, 찾은 label:`, label);
      if (label) {
        label.classList.add('text-red-500');
      }
    }
  })

  document.getElementById('updateRisk').addEventListener('click', async (event) => {
    event.preventDefault();

    const selectedRisk = document.querySelector('input[name="memberRisk"]:checked').value;

    console.log('선택된 위험도:', selectedRisk);

    try {
      const response = await ajax.post('/api/member/traits/update-risk', {
        memberRisk: selectedRisk
      });

      if (response.success) {
        alert('위험도가 수정되었습니다.');
        window.location.href = "http://localhost:9080/propensity-test/my-page/modify";
      } else {
        alert('수정 실패');
      }
    } catch (error) {
      console.error('위험도 수정중 오류 발생:',error);
      alert('서버 오류 발생');
    }

  });

});
