const inputExpRtn = document.getElementById('expRtn');
const maxRtn = parseFloat(document.getElementById('maxRtn').value);
const finishBtn = document.getElementById('finish');

// 최대 희망수익률 넘게 설정하면 바로 비활성화
inputExpRtn.addEventListener('input', () => {

  const inputValue = parseFloat(inputExpRtn.value);
  if (inputValue > maxRtn) {
    finishBtn.setAttribute('disabled', true);
    finishBtn.classList.remove('bg-blue-500','cursor-pointer');  // 기본색 제거
    finishBtn.classList.add('bg-gray-400','opacity-50'); // 비활성화 색 구분
  } else {
    finishBtn.removeAttribute('disabled');
    finishBtn.classList.remove('bg-gray-400','opacity-50');
    finishBtn.classList.add('bg-blue-500','cursor-pointer');
  }
});


// 사고방지 : maxRtn에 비정상적인 값이 들어와 있는경우
if (!maxRtn || isNaN(maxRtn) || maxRtn <= 0) {
  modifyBtn.setAttribute('disabled', true);
}