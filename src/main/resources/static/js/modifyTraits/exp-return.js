const inputExpRtn = document.getElementById('expRtn');
const maxRtn = parseFloat(document.getElementById('maxRtn').value);
const modifyBtn = document.getElementById('update-exp-return');

console.log(typeof inputExpRtn.value); // "string"
console.log(typeof maxRtn); // number

// 초기설정 : 최대 희망수익률 넘게 설정되어 있으면 수정 버튼 잠김
if (parseFloat(inputExpRtn.value) > maxRtn) {
  modifyBtn.setAttribute('disabled',true);
  modifyBtn.classList.remove('bg-blue-500','cursor-pointer');  // 기본색 제거
  modifyBtn.classList.add('bg-gray-400','opacity-50'); // 비활성화 색 구분   
}

// 최대 희망수익률 넘게 설정하면 바로 비활성화
inputExpRtn.addEventListener('input', () => {

  const inputValue = parseFloat(inputExpRtn.value);
  if (inputValue > maxRtn) {
    modifyBtn.setAttribute('disabled', true);
    modifyBtn.classList.remove('bg-blue-500','cursor-pointer');  // 기본색 제거
    modifyBtn.classList.add('bg-gray-400','opacity-50'); // 비활성화 색 구분
  } else {
    modifyBtn.removeAttribute('disabled');
    modifyBtn.classList.remove('bg-gray-400','opacity-50');
    modifyBtn.classList.add('bg-blue-500','cursor-pointer');
  }
});

// 사고방지 : maxRtn에 비정상적인 값이 들어와 있는경우
if (!maxRtn || isNaN(maxRtn) || maxRtn <= 0) {
  modifyBtn.setAttribute('disabled', true);
}

modifyBtn.addEventListener('click', async (event) => {
  event.preventDefault();

  selectedExpRtn = document.getElementById('expRtn').value;

  try {
    const response = await ajax.post('/api/member/traits/update-exp-return', {
      expRtn: selectedExpRtn

    });

    if (response.success) {
      alert('희망 수익률이 수정되었습니다. ');
      window.location.href = "http://localhost:9080/propensity-test/my-page/modify";
    } else {
      alert('수정 실패');
    }
  } catch (error) {
    console.error('위험도 수정중 오류 발생:',error);
    alert('서버 오류 발생');
  }

});




