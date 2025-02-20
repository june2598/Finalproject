// 페이지 로드 시 기본 데이터 로드
document.addEventListener('DOMContentLoaded', () => {
  // KOSPI 시장의 뉴스 증가율을 기준으로 업종 데이터 로드
  loadTrendData(1, 'NEWS_INCREASE_RATE');
  // 주식 데이터의 뉴스 증가율을 기준으로 로드
  loadStocksTrend('NEWS_INCREASE_RATE');
  loadWordCloud();

  // KOSPI 클릭 이벤트
  document.getElementById('kospi').addEventListener('click', () => {
    selectMarket('1', 'NEWS_INCREASE_RATE'); // KOSPI로 선택
  });

  // KOSDAQ 클릭 이벤트
  document.getElementById('kosdaq').addEventListener('click', () => {
    selectMarket('2', 'NEWS_INCREASE_RATE'); // KOSDAQ으로 선택
  });

  // 업종 테이블 정렬 기능
  document.querySelectorAll('#trendTable .sortable').forEach(header => {
    header.addEventListener('click', () => {
      const orderBy = header.getAttribute('data-order');
      loadTrendData(document.getElementById('marketId').value, orderBy);
    });
  });

  // 주식 테이블 정렬 기능
  document.querySelectorAll('#stocksTable .sortable').forEach(header => {
    header.addEventListener('click', () => {
      const orderBy = header.getAttribute('data-order');
      loadStocksTrend(orderBy);
    });
  });
});

// 시장 선택 함수
function selectMarket(marketId, orderBy) {
  // 선택한 요소 확인
  const markets = {
    '1': document.getElementById("kospi"),
    '2': document.getElementById("kosdaq"),
  };

  // 모든 시장을 초기화
  Object.values(markets).forEach(market => {
    market.classList.remove("font-bold", "text-black");
    market.classList.add("text-gray-300");
  });

  // 선택된 시장 스타일 변경
  if (marketId in markets) {
    const selectedMarket = markets[marketId];
    selectedMarket.classList.remove("text-gray-300");
    selectedMarket.classList.add("font-bold", "text-black");
  }

  document.getElementById('marketId').value = marketId;
  loadTrendData(marketId, orderBy); // 선택된 시장과 정렬 기준으로 데이터 로드
}

// 업종 데이터 로드 함수
const loadTrendData = async (marketId, orderBy) => {
  // API 호출 URL 생성
  const url = `/api/trend?marketId=${marketId}&orderBy=${orderBy}`;

  // AJAX GET 요청을 통해 데이터 가져오기
  const data = await ajax.get(url);

  // 업종 테이블의 tbody 요소 선택
  const tbody = document.getElementById('trendTable').querySelector('tbody');
  tbody.innerHTML = ''; // 기존 데이터 삭제

  // 데이터가 없는 경우 메시지 표시
  if (data.body.length === 0) {
    tbody.innerHTML = '<tr><td colspan="3">데이터가 없습니다.</td></tr>';
  } else {
    // 데이터가 있는 경우 각 항목을 테이블에 추가
    data.body.forEach(item => {
      const row = document.createElement('tr'); // 새로운 행 생성
      row.innerHTML = `
                <td>${item.secNm}</td> <!-- 업종명 -->
                <td>${item.newsIncreaseRate + '%' || 'N/A'}</td> <!-- 뉴스 증가율 --> 
                <td>${item.communityIncreaseRate + '%' || 'N/A'}</td> <!-- 커뮤니티 증가율 -->
            `;
      tbody.appendChild(row); // 생성한 행을 테이블에 추가
    });
  }
};

// 주식 트렌드 데이터 로드 함수
const loadStocksTrend = async (orderBy) => {
  // API 호출 URL 생성
  const url = `/api/trend/stocks?orderBy=${orderBy}`;

  // AJAX GET 요청을 통해 데이터 가져오기
  const data = await ajax.get(url);

  // 주식 테이블의 tbody 요소 선택
  const stocksTableBody = document.getElementById('stocksTable').querySelector('tbody');
  stocksTableBody.innerHTML = ''; // 기존 데이터 삭제

  // 데이터가 없는 경우 메시지 표시
  if (data.body.length === 0) {
    stocksTableBody.innerHTML = '<tr><td colspan="3">데이터가 없습니다.</td></tr>';
  } else {
    // 데이터가 있는 경우 각 항목을 테이블에 추가
    data.body.forEach(item => {
      const row = document.createElement('tr'); // 새로운 행 생성
      row.innerHTML = `
                <td>${item.stkNm}</td> <!-- 주식명 -->
                <td>${item.newsIncreaseRate + '%' || 'N/A'}</td> <!-- 뉴스 증가율 -->
                <td>${item.communityIncreaseRate + '%' || 'N/A'}</td> <!-- 커뮤니티 증가율 -->
            `;
      stocksTableBody.appendChild(row); // 생성한 행을 테이블에 추가
    });
  }
};

// 워드 클라우드 샘플 테스트
const loadWordCloud = async () => {
  try {

    const url = `http://127.0.0.1:8000/wordcloud`;

    const data = await ajax.get(url);

    if (data && data.image) {
      document.getElementById('wordcloud').src = data.image;
    } else {
      console.error("error", data);
    }
  } catch (error) {
    console.error("error loading word cloud:", error);
  }
}
