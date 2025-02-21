let currentPage = 0; // 현재 페이지
const pageSize = 10; // 한 페이지에 표시할 데이터 수

// 페이지 로드 시 기본 데이터 로드
document.addEventListener('DOMContentLoaded', () => {
  loadSectorList();
  loadStocksList(1, 'r.MARCAP', 3, currentPage * pageSize, null);


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

  // 업종 선택 이벤트
  document.getElementById('sectorSelect').addEventListener('change', () => {
    const secId = document.getElementById('sectorSelect').value;
    const marketId = document.getElementById('marketId').value;
    const risk = document.getElementById('risk').value;
    loadStocksList(marketId, 'r.MARCAP', risk, currentPage * pageSize, secId);
  });


  // 주식 테이블 정렬 기능
  document.querySelectorAll('#stocksTable .sortable').forEach(header => {
    header.addEventListener('click', () => {
      const orderBy = header.getAttribute('data-order');
      const marketId = document.getElementById('marketId').value;
      const risk = document.getElementById('risk').value;
      const secId = document.getElementById('sectorSelect').value;
      loadStocksList(marketId, orderBy, risk, currentPage * pageSize, secId);
    });
  });

  // 위험도 선택 시 자동으로 데이터 로드
  document.getElementById('risk').addEventListener('change', () => {
    const marketId = document.getElementById('marketId').value;
    const orderBy = 'r.MARCAP'; // 기본 정렬 기준으로 설정
    const risk = document.getElementById('risk').value;
    const secId = document.getElementById('sectorSelect').value || null; // 업종 유지
    loadStocksList(marketId, orderBy, risk, currentPage * pageSize, secId);
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

  // 시장 ID 설정
  document.getElementById('marketId').value = marketId;

  // 시장을 선택할 때 업종 필터 초기화
  document.getElementById('sectorSelect').value = '';

  // 위험도 값 유지
  const riskValue = document.getElementById('risk').value;

  loadSectorList(); // 시장 변경 시 업종 리스트 갱신
  // 종목 리스트 호출 (secId는 기본적으로 null)
  loadStocksList(marketId, orderBy, riskValue, offset, '');

}

// 업종 리스트 불러오기 (marketId 반영)
const loadSectorList = async () => {
  try {
    const marketId = document.getElementById('marketId').value; // 현재 선택된 시장 ID 가져오기
    const response = await ajax.get(`/api/stockList/sectorList?marketId=${marketId}`); // API 호출
    const sectorSelect = document.getElementById('sectorSelect');

    // 기존 옵션 삭제 후 기본값 추가
    sectorSelect.innerHTML = '<option value="">전체</option>';

    // 받아온 업종 리스트 추가
    response.body.forEach(sector => {
      const option = document.createElement('option');
      option.value = sector.secId;  // secId 사용
      option.textContent = sector.secNm; // secNm 표시
      sectorSelect.appendChild(option);

    });

  } catch (error) {
    console.error("업종 목록을 불러오는 중 오류 발생:", error);
  }
};



// 종목 리스트 호출 함수 (secId 추가됨)
const loadStocksList = async (marketId, orderBy, risk, offset, secId) => {
  const url = `/api/stockList?marketId=${marketId}&orderBy=${orderBy}&risk=${risk}&offset=${offset}`
    + (secId ? `&secId=${secId}` : ''); // 업종 ID 추가

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
    const secId = document.getElementById('sectorSelect').value || null; // 업종 유지
    loadStocksList(marketId, orderBy, risk, currentPage * pageSize, secId);
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
  const secId = document.getElementById('sectorSelect').value || null; // 업종 유지
  console.log(`다음 버튼 클릭: currentPage=${currentPage}, offset=${offset}`); // 로그 추가
  loadStocksList(marketId, orderBy, risk, offset, secId);

});

function goToStockDetail(stkCode) {
  window.location.href = `http://localhost:9080/stockList/stocks?stkCode=${stkCode}`;
}