// 페이지 로드 시 기본 데이터 로드
document.addEventListener('DOMContentLoaded', () => {
  // KOSPI 시장의 뉴스 증가율을 기준으로 업종 데이터 로드
  loadRealTimePriceData('CHANGE_RATIO');
  // 주식 데이터의 뉴스 증가율을 기준으로 로드
  loadRealTimeVolumeData('CHANGE_RATIO_VOLUME');
});




const loadRealTimePriceData = async (orderBy) => {

  const url = `/api/index/prices?orderBy=${orderBy}`;

  const data = await ajax.get(url);

  const priceTableBody = document.getElementById('priceTable').querySelector('tbody');
  priceTableBody.innerHTML = ''; // 기존 데이터 삭제

  if (data.body.length === 0) {
    stocksTableBody.innerHTML = '<tr><td colspan="3">데이터가 없습니다.</td></tr>';
  } else {

    data.body.forEach(item => {
      const row = document.createElement('tr'); // 새로운 행 생성
      row.innerHTML = `
                <td class="text-left p-1 w-2/3 whitespace-nowrap truncate">${item.stkNm}</td>
                <td class="text-right py-2 px-4 w-1/6">${item.change}</td>
                <td class="text-right py-2 px-4 w-1/6">${item.changeRatio + '%' || 'N/A'}</td>
    `;
      priceTableBody.appendChild(row); // 생성한 행을 테이블에 추가
    });
  }
};

const loadRealTimeVolumeData = async (orderBy) => {

  const url = `/api/index/volume?orderBy=${orderBy}`;

  const data = await ajax.get(url);

  const volumeTableBody = document.getElementById('volumeTable').querySelector('tbody');
  volumeTableBody.innerHTML = '';

  // 데이터가 없는 경우 메시지 표시
  if (data.body.length === 0) {
    volumeTableBody.innerHTML = '<tr><td colspan="3">데이터가 없습니다.</td></tr>';
  } else {

    data.body.forEach(item => {
      const row = document.createElement('tr');
      row.innerHTML = `
              <td class="text-left p-1 w-2/3 whitespace-nowrap truncate">${item.stkNm}</td>
              <td class="text-right py-2 px-4 w-1/6">${item.changeVolume}</td>
              <td class="text-right py-2 px-4 w-1/6">${item.changeRatioVolume + '%' ||'N/A'}</td>
    `;
      volumeTableBody.appendChild(row);
    });
  }
};