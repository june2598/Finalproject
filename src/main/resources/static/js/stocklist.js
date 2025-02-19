let currentPage = 0; // 현재 페이지
const pageSize = 10; // 한 페이지에 표시할 데이터 수

// 페이지 로드 시 기본 데이터 로드
document.addEventListener('DOMContentLoaded', () => {
  loadStocksList(1, 'r.MARCAP', 3, currentPage * pageSize);

  // KOSPI 클릭 이벤트
  document.getElementById('kospi').addEventListener('click', () => {
    selectMarket('1', 'r.MARCAP', 3, currentPage * pageSize); // KOSPI로 선택
  });

  // KOSDAQ 클릭 이벤트
  document.getElementById('kosdaq').addEventListener('click', () => {
    selectMarket('2', 'r.MARCAP', 3, currentPage * pageSize); // KOSDAQ으로 선택
  });

  // KOSDAQ 클릭 이벤트
  document.getElementById('etf').addEventListener('click', () => {
    selectMarket('3', 'r.MARCAP', 3, currentPage * pageSize); // KOSDAQ으로 선택
  });


  // 주식 테이블 정렬 기능
  document.querySelectorAll('#stocksTable .sortable').forEach(header => {
    header.addEventListener('click', () => {
      const orderBy = header.getAttribute('data-order');
      loadStocksList(document.getElementById('marketId').value, orderBy, document.getElementById('risk').value, currentPage * pageSize);
    });
  });

  // 위험도 선택 시 자동으로 데이터 로드
  document.getElementById('risk').addEventListener('change', () => {
    const marketId = document.getElementById('marketId').value;
    const orderBy = 'r.MARCAP'; // 기본 정렬 기준으로 설정
    const risk = document.getElementById('risk').value;
    loadStocksList(marketId, orderBy, risk, currentPage * pageSize);
  });
});

// 시장 선택 함수
function selectMarket(marketId, orderBy, risk, offset) {
  // 선택한 요소 확인
  const markets = {
    '1': document.getElementById("kospi"),
    '2': document.getElementById("kosdaq"),
    '3': document.getElementById("etf"),
  };

  // 모든 시장을 회색으로 초기화
  Object.values(markets).forEach(market => {
    market.classList.remove("font-bold", "text-black");
    market.classList.add("text-gray-300");
  });

  // 선택된 시장 스타일 변경
  const selectedMarket = markets[marketId];
  selectedMarket.classList.remove("text-gray-300");
  selectedMarket.classList.add("font-bold", "text-black");

  document.getElementById('marketId').value = marketId;
  const riskValue = document.getElementById('risk').value;
  loadStocksList(marketId, orderBy, riskValue, offset);
}

// 종목 리스트 호출 함수
const loadStocksList = async (marketId, orderBy, risk, offset) => {
  // API 호출 URL 생성
  const url = `/api/stockList?marketId=${marketId}&orderBy=${orderBy}&risk=${risk}&offset=${offset}`;

  // AJAX GET 요청
  const data = await ajax.get(url);

  // tbody 선택
  const tbody = document.getElementById('stocksTable').querySelector('tbody');

  tbody.innerHTML = ''; // 기존 데이터 삭제

  // 데이터가 없는 경우 메시지 표시
  if (data.body.length === 0) {
    tbody.innerHTML = '<tr><td colspan="8">데이터가 없습니다.</td></tr>';
    document.getElementById('prevBtn').disabled = true;
    document.getElementById('nextBtn').disabled = true;
  } else {
    // 데이터가 있는 경우 각 항목을 테이블에 추가
    data.body.forEach(stock => {
      const row = document.createElement('tr'); // 새로운 행 생성
      row.innerHTML = `
        <td>
          <a href="javascript:void(0);" onclick="goToStockDetail('${stock.stkCode}')"
         class="text-blue-500 hover:underline cursor-pointer">
         ${stock.stkNm}
         </a>
        </td>
        <td>${stock.price}</td>
        <td>${stock.change}</td>
        <td>${stock.changeRatio + '%'}</td>
        <td>${stock.volume}</td>
        <td>${stock.amount}</td>
        <td>${stock.marcap !== undefined ? stock.marcap.toLocaleString() : 'N/A'}</td>
        <td>${stock.traitStkRisk}</td>
      `;
      tbody.appendChild(row); // 생성한 행을 테이블에 추가
    });

    // 버튼 상태 업데이트
    document.getElementById('prevBtn').disabled = currentPage === 0; // 첫 페이지에서 이전 버튼 비활성화
    document.getElementById('nextBtn').disabled = data.body.length < pageSize; // 다음 버튼 비활성화
  }
};

// 이전 버튼 클릭 이벤트
document.getElementById('prevBtn').addEventListener('click', () => {
  if (currentPage > 0) {
    currentPage--;
    const marketId = document.getElementById('marketId').value;
    const orderBy = document.querySelector('#stocksTable .sortable.selected')
      ? document.querySelector('#stocksTable .sortable.selected').getAttribute('data-order')
      : 'r.MARCAP'; // 기본 정렬 기준 설정
    const risk = document.getElementById('risk').value;
    loadStocksList(marketId, orderBy, risk, currentPage * pageSize);
  }
});

// 다음 버튼 클릭 이벤트
document.getElementById('nextBtn').addEventListener('click', () => {
  currentPage++;
  const marketId = document.getElementById('marketId').value;
  const orderBy = document.querySelector('#stocksTable .sortable.selected')
    ? document.querySelector('#stocksTable .sortable.selected').getAttribute('data-order')
    : 'r.MARCAP'; // 기본 정렬 기준 설정
  const risk = document.getElementById('risk').value;
  // offset 계산
  const offset = currentPage * pageSize;
  console.log(`다음 버튼 클릭: currentPage=${currentPage}, offset=${offset}`); // 로그 추가
  loadStocksList(marketId, orderBy, risk, offset);
});

function goToStockDetail(stkCode) {
  window.location.href = `http://localhost:9080/stockList/stocks?stkCode=${stkCode}`;
}