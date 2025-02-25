const kospiUpdateBtn = document.getElementById('kospi-stock-update');
const kosdaqUpdateBtn = document.getElementById('kosdaq-stock-update');
const etfUpdateBtn = document.getElementById('etf-stock-update');

const kospiRtStkUpdateBtn = document.getElementById('kospi-rt-stk-update');
const kosdaqRtStkUpdateBtn = document.getElementById('kosdaq-rt-stk-update');
const etfRtStkupdateBtn = document.getElementById('etf-rt-stk-update');

const recStkUpdateBtn = document.getElementById("update-rec-stk");


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
  recStkUpdateBtn.disabled = true;
  recStkUpdateBtn.textContent = 'REC_STK 테이블 업데이트중...';

  try {
    const response = await ajax.post(`/api/update-rec-stk`, {});
    
    if (response && response.success) {
      alert('REC_STK 리스트가 업데이트 되었습니다.');
    } else {
      alert(response?.message || 'REC_STK 업데이트 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('REC_STK 업데이트 중 오류', error);
    alert('REC_STK 업데이트중 오류발생(catch)');
  }
  button.disabled = false;
  button.textContent = 'REC_STK (종목 추천 테이블) 업데이트';
});


async function updateRtStk(market) {
  const button = document.getElementById(`${market.toLowerCase()}-rt-stk-update`);
  button.disabled = true;
  button.textContent = `${market} RT_STK 업데이트중...`;

  try {
    const response = await ajax.post(`http://localhost:8000/api/update-rt-stk`, {
      market: market
    });

    if (response && response.success) {
      alert(`${market} RT_STK 리스트가 업데이트 되었습니다.`);
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
  button.disabled = true;
  button.textContent = `${market} 업데이트 중...`;


  try {
    const response = await ajax.post(`http://localhost:8000/api/update-stocks`, {
      market: market,
      market_id: market_id
    });

    if (response && response.success) {
      alert(`${market} 종목 리스트가 업데이트 되었습니다.`);
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
