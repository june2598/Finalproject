# FinalProject : 투자 성향별 종목 추천 플랫폼 - 스마트 투자 

## 프로젝트 소개

 국비과정 최종 프로젝트로, 초보 투자자들이 자신의 투자 성향에 맞는 주식 종목을 추천받을 수 있는 웹 플랫폼 개발. 사용자의 위험 선호도와 투자 목적에 따라 맞춤형 포트폴리오 제안.


## 프로젝트 목적

  주식 초심자들을 위한 투자 성향별 투자종목 플랫폼 구축

  ### 프로젝트 목표

  - 과거 지표 기반 사용자의 투자 성향에 맞는 종목 추천

  - 뉴스기사/ 커뮤니티 반응 기반 긍정점수를 통해 투자 의사 결정 지원 도구 제공

## 개발 기간

2025.01.20 - 2025.02.28 (40일) | 팀 프로젝트 (팀원 3명)


## 핵심 기능 :

- 고객 투자 성향 검사
- 투자 성향별 종목 추천
- 뉴스기사/커뮤니티 반응기반 종목별 긍정점수 제공

## 활용 방안

- 과거 데이터 기반으로 분석된 지표가 종목 파악
- 시장 트렌드 및 투자자들의 투자 심리 파악


### 사용 기술 :

| Languages | Java 17, Python 3, Thymeleaf 3, HTML5, CSS, JavaScript(ES6+) |
| DataBase | Oracle 18c XE, SpringJDBC |
| Framework | SpringBoot 3.4.2, FastAPI 0.112.2 |
| Data Exchange | RESTful API, AJAX, JSON |
| Version Control | Git 2.47.0, Github |
| Testing Tools | Postman 11.33.5 |

### 사용 라이브러리 : 

Pandas 2.2.2
NumPy 1.26.4
Requests 2.32.3
Selenium 4.31.1
BeautifulSoup 4.12.2
Matplotlib 3.9.2
Plotly 5.24.1
WordCloud 1.9.3
TensorFlow 2.18.0
Scikit-learn 1.5.1
Transformers 4.48.0
FinanceDataReader 0.9.94
Torch 2.5.1

### 외부 수집 데이터 출처

KRX 한국 거래소 정보 데이터 시스템 <http://data.krx.co.kr>
네이버 증권 종목별 뉴스 <https://finance.naver.com/item/news.naver?code=005930> (예시 - 삼성전자 )
토스증권 종목별 커뮤니티 <https://tossinvest.com/stocks/A005930/community> (예시 - 삼성전자)
KNU 한국어 감성사전 <https://github.com/park1200656/KnuSentiLex>


