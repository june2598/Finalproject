document.getElementById('update-trait-sectors').addEventListener('click', async (event) => {
  event.preventDefault();

  const selectedSectors = [...document.querySelectorAll('input[type="checkbox"]:checked')].map(input => input.value);


  console.log('선택된 관심업종:', selectedSectors);

  if (selectedSectors.length === 0) {
    alert("최소 한 개 이상의 관심 업종을 선택해야 합니다.");
    return;
  }

  if (selectedSectors.length > 5) {
    alert("관심업종은 최대 다섯개 까지 선택 가능합니다.");
    return;
  }


  try {
    const response = await ajax.post('/api/member/traits/update-sectors', {
      intSec: selectedSectors

    });

  // try {
  //   // ✅ fetch()로 JSON 형식의 데이터 전송
  //   const response = await fetch('/api/member/traits/update-sectors', {  // ✅ URL 확인
  //     method: 'POST',
  //     headers: {
  //       'Content-Type': 'application/json' // ✅ JSON 형식으로 전송
  //     },
  //     body: JSON.stringify({ intSec: selectedSectors }) // ✅ JSON 변환 후 전송
  //   });


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