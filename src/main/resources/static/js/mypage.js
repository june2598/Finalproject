document.addEventListener("DOMContentLoaded", function () {
  // const memberRisk = /*[[${memberTraits.memberRisk}]]*/;
  setRiskLevel(memberRisk);
});


function setRiskLevel(level) {
  const riskLevels = document.querySelectorAll('.risk-level');

  riskLevels.forEach((riskLevel, index) => {
    riskLevel.innerHTML = ''; // 기존 점 제거
    if (index === level - 1) {
      // 큰 점
      const outerPoint = document.createElement('div');
      outerPoint.classList.add('outer-point');

      // 작은 점
      const innerPoint = document.createElement('div');
      innerPoint.classList.add('inner-point');

      // 큰 점 안에 작은 점을 추가

      riskLevel.appendChild(outerPoint);
      riskLevel.appendChild(innerPoint);
    }
  });
}


