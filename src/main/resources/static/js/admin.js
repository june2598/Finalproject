const kospiUpdateBtn = document.getElementById('kospi-stock-update');
const kosdaqUpdateBtn = document.getElementById('kosdaq-stock-update');
const etfUpdateBtn = document.getElementById('etf-stock-update');

const kospiRtStkUpdateBtn = document.getElementById('kospi-rt-stk-update');
const kosdaqRtStkUpdateBtn = document.getElementById('kosdaq-rt-stk-update');
const etfRtStkupdateBtn = document.getElementById('etf-rt-stk-update');

const recStkUpdateBtn = document.getElementById("update-rec-stk");
const domesticIndicesUpdateBtn = document.getElementById('domestic-indices-update');

const traitStkUpdateBtn = document.getElementById('trait-stk-update');
const traitSecUpdateBtn = document.getElementById('trait-sec-update');
const traitRecSecUpdateBtn = document.getElementById('trait-rec-sec-update');

const communityUpdateBtn = document.getElementById('community-update');

// 서버에서 저장된 마지막 업데이트 시간을 불러오기
async function loadTimestamps() {
  const buttons = [
    'kospi-stock-update',
    'kosdaq-stock-update',
    'etf-stock-update',
    'kospi-rt-stk-update',
    'kosdaq-rt-stk-update',
    'etf-rt-stk-update',
    'domestic-indices-update',
    'update-rec-stk',
    'trait-stk-update',
    'trait-sec-update',
    'trait-rec-sec-update',
    'community-update'
  ];

  try {
    const data = await ajax.get('http://localhost:8000/api/get-update-timestamps');

    buttons.forEach(buttonId => {
      const savedTime = data[buttonId];
      if (savedTime) {
        let timestampElement = document.getElementById(`${buttonId}-timestamp`);
        const button = document.getElementById(buttonId);

        if (!timestampElement) {
          timestampElement = document.createElement('p');
          timestampElement.id = `${buttonId}-timestamp`;
          timestampElement.className = 'text-sm text-gray-500 mt-1';
          button.parentNode.appendChild(timestampElement);
        }
        timestampElement.textContent = `마지막 업데이트: ${savedTime}`;
      }
    });
  } catch (error) {
    console.error('업데이트 타임스탬프 로드 실패', error);
  }
}

// 서버에 업데이트 시간 저장 요청
async function saveTimestampToServer(buttonId, timestamp) {
  try {
    await ajax.post('http://localhost:8000/api/save-update-timestamp', {
      buttonId: buttonId,
      timestamp: timestamp
    });
  } catch (error) {
    console.error('업데이트 타임스탬프 저장 실패', error);
  }
}

// 버튼 클릭 시 업데이트 시간 기록
function updateTimestamp(buttonId) {
  const button = document.getElementById(buttonId);
  let timestampElement = document.getElementById(`${buttonId}-timestamp`);

  const now = new Date();
  const formattedTime = now.toLocaleString();
  timestampElement.textContent = `마지막 업데이트: ${formattedTime}`;

  // 서버에 저장
  saveTimestampToServer(buttonId, formattedTime);
}

// 페이지 로드 시 실행
window.onload = loadTimestamps;



// STOCKS, SECTORS 업데이트 영역

kospiUpdateBtn.addEventListener('click', async (event) => {

  event.preventDefault();
  const market = 'KOSPI';
  const market_id = 1;
  updateStock(market, market_id);

});

kosdaqUpdateBtn.addEventListener('click', async (event) => {
  event.preventDefault();

  const market = 'KOSDAQ';
  const market_id = 2;

  updateStock(market, market_id);
});

etfUpdateBtn.addEventListener('click', async (event) => {
  event.preventDefault();

  const market = 'ETF';
  const market_id = 3;

  updateStock(market, market_id);
});

// RT_STK 테이블 업데이트 영역

kospiRtStkUpdateBtn.addEventListener('click', async (event) => {
  event.preventDefault();

  const market = 'KOSPI';
  updateRtStk(market);
});

kosdaqRtStkUpdateBtn.addEventListener('click', async (event) => {
  event.preventDefault();

  const market = 'KOSDAQ';
  updateRtStk(market);
});

etfRtStkupdateBtn.addEventListener('click', async (event) => {
  event.preventDefault();
  const market = 'ETF';
  updateRtStk(market);
});

// REC_STK 테이블 업데이트 영역

recStkUpdateBtn.addEventListener('click', async (event) => {
  event.preventDefault();
  const buttonId = 'update-rec-stk';
  recStkUpdateBtn.disabled = true;
  recStkUpdateBtn.textContent = 'REC_STK 테이블 업데이트중...';

  try {
    const response = await ajax.post(`http://localhost:8000/api/update-rec-stk`, {});

    if (response && response.success) {
      alert('REC_STK 리스트가 업데이트 되었습니다.');
      updateTimestamp(buttonId);
    } else {
      alert(response?.message || 'REC_STK 업데이트 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('REC_STK 업데이트 중 오류', error);
    alert('REC_STK 업데이트중 오류발생(catch)');
  }
  recStkUpdateBtn.disabled = false;
  recStkUpdateBtn.textContent = 'REC_STK (종목 추천 테이블) 업데이트';
});

// 국내 시장지수 (DOMESTIC_INDICES) 테이블 업데이트 영역

domesticIndicesUpdateBtn.addEventListener('click', async (event) => {
  event.preventDefault();
  const buttonId = 'domestic-indices-update';

  domesticIndicesUpdateBtn.disabled = true;
  domesticIndicesUpdateBtn.textContent = '국내 시장지수 업데이트중...';

  try {
    const response = await ajax.post(`http://localhost:8000/api/update-domestic-indices`, {});

    if (response && response.success) {
      alert('국내 시장지수 리스트가 업데이트 되었습니다.');
      updateTimestamp(buttonId);
    } else {
      alert(response?.message || '국내 시장지수 업데이트 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('국내 시장지수 업데이트 중 오류', error);
    alert('국내 시장지수 업데이트중 오류발생(catch)');
  }
  domesticIndicesUpdateBtn.disabled = false;
  domesticIndicesUpdateBtn.textContent = '국내 시장 지수 업데이트';
});


// TRAIT 업데이트 영역

traitStkUpdateBtn.addEventListener('click', async (event) => {
  event.preventDefault();
  const buttonId = 'trait-stk-update';

  traitStkUpdateBtn.disabled = true;
  traitStkUpdateBtn.textContent = 'TRAIT_STK 테이블 업데이트중...';

  try {
    const response = await ajax.post(`http://localhost:8000/api/update-trait-stk`, {});

    if (response && response.success) {
      alert('TRAIT_STK 테이블이 업데이트 되었습니다.');
      updateTimestamp(buttonId);
    } else {
      alert(response?.message || 'TRAIT_STK 테이블 업데이트 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('TRAIT_STK 테이블 업데이트 중 오류', error);
    alert('TRAIT_STK 테이블 업데이트중 오류발생(catch)');
  }
  traitStkUpdateBtn.disabled = false;
  traitStkUpdateBtn.textContent = 'TRAIT-STK 업데이트';
});

traitSecUpdateBtn.addEventListener('click', async (event) => {
  event.preventDefault();
  const buttonId = 'trait-sec-update';

  traitSecUpdateBtn.disabled = true;
  traitSecUpdateBtn.textContent = 'TRAIT_SEC 테이블 업데이트중...';

  try {
    const response = await ajax.post(`http://localhost:8000/api/update-trait-sec`, {});

    if (response && response.success) {
      alert('TRAIT_SEC 테이블이 업데이트 되었습니다.');
      updateTimestamp(buttonId);
    } else {
      alert(response?.message || 'TRAIT_SEC 테이블 업데이트 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('TRAIT_SEC 테이블 업데이트 중 오류', error);
    alert('TRAIT_SEC 테이블 업데이트중 오류발생(catch)');
  }
  traitSecUpdateBtn.disabled = false;
  traitSecUpdateBtn.textContent = 'TRAIT-SEC 업데이트';
});

traitRecSecUpdateBtn.addEventListener('click', async (event) => {
  event.preventDefault();
  const buttonId = 'trait-rec-sec-update';

  traitRecSecUpdateBtn.disabled = true;
  traitRecSecUpdateBtn.textContent = 'TRAIT_REC_SEC 테이블 업데이트중 ...';

  try {
    const response = await ajax.post(`http://localhost:8000/api/update-trait-rec-sec`, {});

    if (response && response.success) {
      alert('TRAIT_REC_SEC 테이블이 업데이트 되었습니다.');
      updateTimestamp(buttonId);
    } else {
      alert(response?.message || 'TRAIT_REC_SEC 테이블 업데이트 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('TRAIT_REC_SEC 테이블 업데이트 중 오류', error);
    alert('TRAIT_REC_SEC 테이블 업데이트중 오류발생(catch)');
  }
  traitRecSecUpdateBtn.disabled = false;
  traitRecSecUpdateBtn.textContent = 'TRAIT-REC-SEC 업데이트';
});

// COMMUNITY 업데이트 영역
communityUpdateBtn.addEventListener('click', async (event) =>{
  event.preventDefault();
  const buttonId = 'community-update';

  communityUpdateBtn.disabled = true;
  communityUpdateBtn.textContent = 'COMMUNITY 테이블 업데이트중...';

  try {
    const response = await ajax.post(`http://localhost:8000/scrape`, {});

    if (response && response.success) {
      alert('COMMUNITY 테이블이 업데이트 되었습니다.');
      updateTimestamp(buttonId);
    } else {
      alert(response?.message || 'COMMUNITY 테이블 업데이트 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('COMMUNITY 테이블 업데이트 중 오류', error);
    alert('COMMUNITY 테이블 업데이트중 오류발생(catch)');
  }
  communityUpdateBtn.disabled = false;
  communityUpdateBtn.textContent = 'COMMUNITY 업데이트';
});




async function updateRtStk(market) {
  const buttonId = `${market.toLowerCase()}-rt-stk-update`;
  const button = document.getElementById(`${market.toLowerCase()}-rt-stk-update`);
  button.disabled = true;
  button.textContent = `${market} RT_STK 업데이트중...`;

  try {
    const response = await ajax.post(`http://localhost:8000/api/update-rt-stk`, {
      market: market
    });

    if (response && response.success) {
      alert(`${market} RT_STK 리스트가 업데이트 되었습니다.`);
      updateTimestamp(buttonId);
    } else {
      alert(response?.message || 'RT_STK 업데이트 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('RT_STK 업데이트중 오류', error);
    alert('RT_STK 업데이트중 오류발생(catch)');
  }
  button.disabled = false;
  button.textContent = `${market} RT_STK 업데이트`;
};




async function updateStock(market, market_id) {

  const button = document.getElementById(`${market.toLowerCase()}-stock-update`);
  const buttonId = `${market.toLowerCase()}-stock-update`;
  button.disabled = true;
  button.textContent = `${market} 업데이트 중...`;


  try {
    const response = await ajax.post(`http://localhost:8000/api/update-stocks`, {
      market: market,
      market_id: market_id
    });

    if (response && response.success) {
      alert(`${market} 종목 리스트가 업데이트 되었습니다.`);
      updateTimestamp(buttonId);
    } else {
      alert(response?.message || '종목 업데이트 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('종목 업데이트중 오류', error);
    alert('종목 업데이트중 오류발생(catch)');
  }
  button.disabled = false;
  button.textContent = `${market} 종목 업데이트`;
};



