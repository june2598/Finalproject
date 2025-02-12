package com.kh.finalproject.domain.trend.dao;

import com.kh.finalproject.domain.dto.SectorsTrendRateDto;
import com.kh.finalproject.domain.dto.StocksTrendRateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrendDAOImpl implements TrendDAO {
  private final NamedParameterJdbcTemplate template;

  @Override
  public List<SectorsTrendRateDto> sectorTrendByNews(int marketId) {
    return getSectorTrend(marketId,"NEWS_INCREASE_RATE");
  }

  @Override
  public List<SectorsTrendRateDto> sectorTrendByCommunity(int marketId) {
    return getSectorTrend(marketId,"COMMUNITY_INCREASE_RATE");
  }

  @Override
  public List<SectorsTrendRateDto> getSectorTrend(int marketId, String orderBy) {

    StringBuffer sql = new StringBuffer();

    sql.append("     SELECT ");
    sql.append(" sec_nm,   ");
    sql.append(" ROUND( ");
    sql.append("     CASE ");
    sql.append("     WHEN total_yesterday_news_count = 0 THEN NULL   ");
    sql.append("     ELSE (total_today_news_count - total_yesterday_news_count) / total_yesterday_news_count * 100 ");
    sql.append("     END, 2) AS NEWS_INCREASE_RATE, ");
    sql.append(" ROUND( ");
    sql.append("     CASE ");
    sql.append("     WHEN total_yesterday_community_count = 0 THEN NULL ");
    sql.append("     ELSE (total_today_community_count - total_yesterday_community_count) / total_yesterday_community_count * 100 ");
    sql.append("     END, 2) AS COMMUNITY_INCREASE_RATE ");
    sql.append(" FROM ");
    sql.append("     ( ");
    sql.append("         SELECT ");
    sql.append("         SEC_NM,  ");
    sql.append("         SUM(CASE WHEN SOURCE = 'NEWS' AND TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) THEN 1 ELSE 0 END) AS total_yesterday_news_count, ");
    sql.append("         SUM(CASE WHEN SOURCE = 'NEWS' AND TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE) THEN 1 ELSE 0 END) AS total_today_news_count, ");
    sql.append("         SUM(CASE WHEN SOURCE = 'COMMUNITY' AND TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) THEN 1 ELSE 0 END) AS total_yesterday_community_count, ");
    sql.append("         SUM(CASE WHEN SOURCE = 'COMMUNITY' AND TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE) THEN 1 ELSE 0 END) AS total_today_community_count ");
    sql.append("         FROM ");
    sql.append("             ( ");
    sql.append("                 SELECT ");
    sql.append("                 C.SEC_NM, ");
    sql.append("                 N.PUBLISHED_DATE, ");
    sql.append(" 'NEWS' AS SOURCE ");
    sql.append(" FROM ");
    sql.append(" MKT_SEC_STK C ");
    sql.append(" JOIN ");
    sql.append(" NEWS N ON C.STK_ID = N.STK_ID ");
    sql.append("     WHERE ");
    sql.append(" C.MARKET_ID = :marketId ");
    sql.append(" UNION ALL ");
    sql.append(" SELECT ");
    sql.append(" C.SEC_NM, ");
    sql.append(" CM.POST_DATE AS PUBLISHED_DATE, ");
    sql.append(" 'COMMUNITY' AS SOURCE ");
    sql.append(" FROM ");
    sql.append(" MKT_SEC_STK C ");
    sql.append(" JOIN ");
    sql.append(" COMMUNITY CM ON C.STK_ID = CM.STK_ID ");
    sql.append("     WHERE ");
    sql.append(" C.MARKET_ID = :marketId ");
    sql.append("         ) T ");
    sql.append(" GROUP BY ");
    sql.append(" SEC_NM ");
    sql.append(" ) sector_counts ");
    sql.append(" ORDER BY ");
    sql.append(orderBy + " DESC ");
    sql.append(" FETCH FIRST 10 ROWS ONLY ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("marketId", marketId);

    List<SectorsTrendRateDto> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(SectorsTrendRateDto.class));
    return list;
  }

  @Override
  public List<StocksTrendRateDto> getStocksTrend(String orderBy) {
    StringBuffer sql = new StringBuffer();

    sql.append("    SELECT ");
    sql.append(" stk_nm,");
    sql.append(" ROUND( ");
    sql.append("     CASE ");
    sql.append("     WHEN (SELECT COUNT(*) FROM NEWS WHERE TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) = 0 THEN NULL ");
    sql.append(" ELSE ( ");
    sql.append("         (SELECT COUNT(*) FROM NEWS WHERE TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE) AND STK_ID = C.STK_ID) - ");
    sql.append("     (SELECT COUNT(*) FROM NEWS WHERE TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) ");
    sql.append("         ) / (SELECT COUNT(*) FROM NEWS WHERE TRUNC(PUBLISHED_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) * 100 ");
    sql.append(" END, 2) AS NEWS_INCREASE_RATE, ");
    sql.append(" ROUND( ");
    sql.append("     CASE ");
    sql.append("     WHEN (SELECT COUNT(*) FROM COMMUNITY WHERE TRUNC(POST_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) = 0 THEN NULL ");
    sql.append(" ELSE ( ");
    sql.append("         (SELECT COUNT(*) FROM COMMUNITY WHERE TRUNC(POST_DATE) = TRUNC(SYSDATE) AND STK_ID = C.STK_ID) - ");
    sql.append("     (SELECT COUNT(*) FROM COMMUNITY WHERE TRUNC(POST_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) ");
    sql.append("         ) / (SELECT COUNT(*) FROM COMMUNITY WHERE TRUNC(POST_DATE) = TRUNC(SYSDATE - 1) AND STK_ID = C.STK_ID) * 100 ");
    sql.append(" END, 2) AS COMMUNITY_INCREASE_RATE ");
    sql.append(" FROM ");
    sql.append("     ( ");
    sql.append("         SELECT stk_id, stk_nm, sec_nm ");
    sql.append("         FROM mkt_sec_stk ");
    sql.append("         WHERE STK_ID IN (SELECT DISTINCT STK_ID FROM NEWS) ");
    sql.append("     ) C ");
    sql.append(" GROUP BY ");
    sql.append(" STK_ID, stk_nm ");
    sql.append(" ORDER BY ");
    sql.append(orderBy + " DESC ");
    sql.append(" FETCH FIRST 10 ROWS ONLY ");

    SqlParameterSource param = new MapSqlParameterSource();
    List<StocksTrendRateDto> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(StocksTrendRateDto.class));
    return list;
  }

  @Override
  public List<StocksTrendRateDto> stocksTrendByNews() {
    return getStocksTrend("NEWS_INCREASE_RATE");
  }

  @Override
  public List<StocksTrendRateDto> stocksTrendByCommunity() {
    return getStocksTrend("COMMUNITY_INCREASE_RATE");
  }
}
