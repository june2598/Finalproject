document.addEventListener('DOMContentLoaded', async () => {
  let stkCode = new URLSearchParams(window.location.search).get('stkCode'); // URL에서 stkCode 가져오기

  if (stkCode) {
    // stkCode가 숫자 6자리가 아니라면(즉, 한글이라면) 종목 코드로 변환 요청
    if (!/^[0-9A-Z]{6}$/.test(stkCode)) {
      stkCode = await getStockCodeFromName(stkCode);
    }

    // 변환된 종목 코드로 API 호출
    if (stkCode) {
      loadStockNews(stkCode);
      loadStockDetail(stkCode);

      // 초기 차트 로딩
      updateChart(stkCode);
    }
  }

  // 라디오 버튼 변경 시 차트 업데이트
  document.querySelectorAll('input[name="chartType"]').forEach(radio => {
    radio.addEventListener('change', () => updateChart(stkCode));
  });

});



function updateChart(stkCode) {
  if (!stkCode) return;

  // 체크된 값 가져오기
  const type = document.querySelector('input[name="chartType"]:checked')?.value || 'return'; // 기본값 return
  const iframe = document.getElementById("stock-chart-frame");

  console.log(`✅ 업데이트: ${stkCode}_candle_${type}_3m.html`); // 디버깅용 로그

  // iframe의 src 업데이트
  iframe.src = `http://localhost:8000/images/${stkCode}_candle_${type}_3m.html`;
}


const loadStockNews = async (stkCode) => {

  // API 호출 URL 생성

  const url = `http://localhost:9080/api/stockList/${stkCode}/news`;

  const data = await ajax.get(url);

  // 종목 관련 뉴스 테이블의 tbody 요소 선택

  const tbody = document.getElementById('stock-news-table').querySelector('tbody');
  tbody.innerHTML = '';

  // 데이터가 없는 경우 메시지 표시
  if (data.body.length === 0) {
    tbody.innerHTML = '<tr><td colspan="3">데이터가 없습니다.</td></tr>';
  } else {
    // 데이터가 있는 경우 각 항목을 테이블에 추가
    data.body.forEach(item => {
      const row = document.createElement('tr'); // 새로운 행 생성
      row.innerHTML = `
                <td>
                <a href="javascript:void(0);" onclick="window.open('${item.newsUrl}', '_blank');"
                  class="hover:underline cursor-pointer">
                ${item.title}
                </a>
                </td> <!-- 기사제목 -->
                <td class="px-2 text-end">${item.mediaName}</td> <!-- 제공 언론사 -->
                <td class="px-2 text-end">${item.publishedDate}</td> <!-- 기사 제공 날짜 -->
            `;
      tbody.appendChild(row); // 생성한 행을 테이블에 추가
    });
  }
}


const loadStockDetail = async (stkCode) => {

  const url = `http://localhost:9080/api/stockList/${stkCode}/detail`;
  const data = await ajax.get(url);

  // 해당 종목의 현재 지표 테이블 tbody 요소 선택

  const tbody = document.getElementById('stock-detail-table').querySelector('tbody');
  tbody.innerHTML = '';

  // 데이터가 없는 경우 메시지 표시
  if (data.body.length === 0) {
    tbody.innerHTML = '<tr><td colspan="3">데이터가 없습니다.</td></tr>';
  } else {
    data.body.forEach(item => {
      const row = document.createElement('tr'); // 새로운 행 생성
      row.innerHTML = `
        <td>${item.stkNm}</td>
        <td class="text-right">${item.price}</td>
        <td class="text-right">${item.change}</td>
        <td class="text-right">${item.changeRatio + '%'}</td>
        <td class="text-right">${item.volume}</td>
        <td class="text-right">${item.amount}</td>
        <td class="text-right">${item.marcap !== undefined ? item.marcap.toLocaleString() : 'N/A'}</td>
        <td class="text-center">${item.traitStkRisk}</td>
      `;
      tbody.appendChild(row); // 생성한 행을 테이블에 추가
    });
  }
}


// 🔄 종목명을 종목 코드로 변환하는 함수
const getStockCodeFromName = async (stkNm) => {
  try {
    const response = await ajax.get(`http://localhost:9080/api/stockList/convert?stkNm=${encodeURIComponent(stkNm)}`);
    return response.body ? response.body.stkCode : null;
  } catch (error) {
    console.error('종목 코드 변환 실패:', error);
    return null;
  }
};
