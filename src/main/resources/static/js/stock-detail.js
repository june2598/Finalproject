document.addEventListener('DOMContentLoaded', () => {
  const stkCode = new URLSearchParams(window.location.search).get('stkCode'); // url에서 stkCode
  if (stkCode) {
    loadStockNews(stkCode);
  }
});

const loadStockNews = async (stkCode) => {

  // API 호출 URL 생성

  const url = `http://localhost:9080/api/stockList/${stkCode}/news`

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
                <td>${item.title}</td> <!-- 기사제목 -->
                <td class="px-2 text-end">${item.mediaName}</td> <!-- 제공 언론사 -->
                <td class="px-2 text-end">${item.publishedDate}</td> <!-- 기사 제공 날짜 -->
            `;
      tbody.appendChild(row); // 생성한 행을 테이블에 추가
    });
  }
}