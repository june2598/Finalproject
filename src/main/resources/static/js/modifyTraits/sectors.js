const checkboxes = document.querySelectorAll('input[type="checkbox"]');
const maxSelections = 5;
const updateButton = document.getElementById('update-trait-sectors');

function updateCheckboxState() {
  const selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
  const selectedCount = selectedCheckboxes.length;
  
  checkboxes.forEach(checkbox => {
      if (!checkbox.checked) {
          checkbox.disabled = selectedCount >= maxSelections;
      }
    });
  }

  // 체크박스 클릭 시 이벤트 리스너 추가
  checkboxes.forEach(checkbox => {
    checkbox.addEventListener('change', updateCheckboxState);
});


updateButton.addEventListener('click', async (event) => {
  event.preventDefault();

  const selectedSectors = [...document.querySelectorAll('input[type="checkbox"]:checked')].map(input => input.value);


  console.log('선택된 관심업종:', selectedSectors);


  if (selectedSectors.length > maxSelections) {
    alert("관심업종은 최대 다섯개 까지 선택 가능합니다.");
    return;
  }


  try {
    const response = await ajax.post('/api/member/traits/update-sectors', {
      intSec: selectedSectors

    });

    if (response && response.success) {
      alert('관심 업종이 성공적으로 업데이트되었습니다.');
      window.location.href = "http://localhost:9080/propensity-test/my-page/modify";
    } else {
      alert(`업데이트 실패: ${response?.message || '알 수 없는 오류'}`);
    }
  } catch (error) {
    console.error('⚠ 관심 업종 수정 중 오류 발생:', error);
    alert('관심 업종 수정 중 오류가 발생했습니다.');
  }

});