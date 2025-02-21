let currentPage = 0; // 현재 페이지
const pageSize = 10; // 한 페이지에 표시할 데이터 수

// 페이지 로드 시 기본 데이터 로드
document.addEventListener('DOMContentLoaded', () => {
  loadSectorList(); // 업종 리스트 불러오기
  loadStocksList(1, 'r.MARCAP', 3, currentPage * pageSize, null);

  // 시장 선택 이벤트
  document.querySelectorAll('.market-option').forEach(market => {
    market.addEventListener('click', () => {
      const marketId = market.getAttribute('id') === 'kospi' ? '1' :
                       market.getAttribute('id') === 'kosdaq' ? '2' : '3';
      selectMarket(marketId, 'r.MARCAP', 3, currentPage * pageSize);
    });
  });

  // 위험도 선택 이벤트
  document.getElementById('risk').addEventListener('change', () => {
    const marketId = document.getElementById('marketId').value;
    const risk = document.getElementById('risk').value;
    const secId = document.getElementById('sectorSelect').value;
    loadStocksList(marketId, 'r.MARCAP', risk, currentPage * pageSize, secId);
  });

  // 업종 선택 이벤트
  document.getElementById('sectorSelect').addEventListener('change', () => {
    const secId = document.getElementById('sectorSelect').value;
    const marketId = document.getElementById('marketId').value;
    const risk = document.getElementById('risk').value;
    loadStocksList(marketId, 'r.MARCAP', risk, currentPage * pageSize, secId);
  });

  // 정렬 기능 추가
  document.querySelectorAll('.sortable').forEach(header => {
    header.addEventListener('click', () => {
      const orderBy = header.getAttribute('data-order');
      const marketId = document.getElementById('marketId').value;
      const risk = document.getElementById('risk').value;
      const secId = document.getElementById('sectorSelect').value;
      loadStocksList(marketId, orderBy, risk, currentPage * pageSize, secId);
    });
  });
});

// 시장 선택 함수 (업종 초기화 포함)
function selectMarket(marketId, orderBy, risk, offset) {
  document.getElementById('marketId').value = marketId;
  document.getElementById('sectorSelect').value = ''; // 업종 초기화

  // 시장 버튼 스타일 업데이트
  document.querySelectorAll('.market-option').forEach(btn => btn.classList.remove('font-bold', 'text-black'));
  document.getElementById(`market-${marketId}`).classList.add('font-bold', 'text-black');

  loadSectorList(); // 시장 변경 시 업종 리스트 갱신
  loadStocksList(marketId, orderBy, risk, offset, null);
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

// 종목 리스트 호출 함수
const loadStocksList = async (marketId, orderBy, risk, offset, secId) => {
  const url = `/api/stockList?marketId=${marketId}&orderBy=${orderBy}&risk=${risk}&offset=${offset}`
               + (secId ? `&secId=${secId}` : ''); // 업종 ID 추가

  const data = await ajax.get(url);
  const tbody = document.getElementById('stocksTable').querySelector('tbody');
  tbody.innerHTML = ''; // 기존 데이터 삭제

  if (data.body.length === 0) {
    tbody.innerHTML = '<tr><td colspan="8">데이터가 없습니다.</td></tr>';
  } else {
    data.body.forEach(stock => {
      const row = document.createElement('tr');
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
      tbody.appendChild(row);
    });
  }
};

// 상세 페이지 이동
function goToStockDetail(stkCode) {
  window.location.href = `http://localhost:9080/stockList/stocks?stkCode=${stkCode}`;
}
