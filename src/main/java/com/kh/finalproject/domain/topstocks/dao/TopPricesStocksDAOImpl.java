package com.kh.finalproject.domain.topstocks.dao;

import com.kh.finalproject.domain.vo.RealTimeStockPriceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor

public class TopPricesStocksDAOImpl implements TopPricesStocksDAO {

  private final NamedParameterJdbcTemplate template;
  private static final Set<String> ALLOWED_ORDER_COLUMNS = Set.of("CHANGE", "CHANGE_RATIO");
  @Override
  public List<RealTimeStockPriceVO> findTop5ByPrice(String orderBy) {

    // 허용된 컬럼명인지 검증 (injection 방지)
    if (!ALLOWED_ORDER_COLUMNS.contains(orderBy)) {
      throw new IllegalArgumentException("Invalid orderBy column: " + orderBy);
    }

    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT ");
    sql.append(" m.STK_NM, m.STK_CODE, ");
    sql.append("     r_today.PRICE AS TODAY_PRICE, ");
    sql.append(" r_yesterday.PRICE AS YESTERDAY_PRICE, ");
    sql.append("     (r_today.PRICE - r_yesterday.PRICE) AS CHANGE, ");
    sql.append(" ROUND((r_today.PRICE - r_yesterday.PRICE) / r_yesterday.PRICE * 100, 2) AS CHANGE_RATIO ");
    sql.append(" FROM MKT_SEC_STK m ");
    sql.append(" JOIN RT_STK r_today ON m.STK_ID = r_today.STK_ID ");
    sql.append(" JOIN RT_STK r_yesterday ON m.STK_ID = r_yesterday.STK_ID ");
    sql.append(" WHERE ");
    sql.append(" TO_CHAR(r_today.CDATE, 'YY/MM/DD') = TO_CHAR(SYSDATE, 'YY/MM/DD') ");
    sql.append(" AND TO_CHAR(r_yesterday.CDATE, 'YY/MM/DD') = TO_CHAR(SYSDATE - 1, 'YY/MM/DD') ");
    sql.append(" ORDER BY ");
    sql.append(orderBy + " DESC NULLS LAST ");
    sql.append(" FETCH FIRST 5 ROWS ONLY ");

    SqlParameterSource param = new MapSqlParameterSource();

    List<RealTimeStockPriceVO> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(RealTimeStockPriceVO.class));
    return list;

  }

  @Override
  public List<RealTimeStockPriceVO> findTop5ByChangePrice() {
    return findTop5ByPrice("CHANGE");
  }

  @Override
  public List<RealTimeStockPriceVO> findTop5ByChangeRatioPrice() {
    return findTop5ByPrice("CHANGE_RATIO");
  }
}
