document.addEventListener("DOMContentLoaded", () => {
  const checkboxes = document.querySelectorAll('input[type="checkbox"]');
  // 관심업종 최대수 5개
  const maxSelections = 5;

  
  function updateCheckboxState() {
      const selectedCount = document.querySelectorAll('input[type="checkbox"]:checked').length;
      
      checkboxes.forEach(checkbox => {
          if (!checkbox.checked) {
              checkbox.disabled = selectedCount >= maxSelections;
          }
      });
  }

  // 체크박스 클릭 시 실행
  checkboxes.forEach(checkbox => {
      checkbox.addEventListener('change', updateCheckboxState);
  });
});
