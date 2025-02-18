package com.kh.finalproject.domain.topstocks.dao;

import com.kh.finalproject.domain.vo.RealTimeStockVolumeVO;
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

public class TopTradedStocksDAOImpl implements TopTradedStocksDAO {

  private final NamedParameterJdbcTemplate template;
  private static final Set<String> ALLOWED_ORDER_COLUMNS = Set.of("CHANGE_VOLUME", "CHANGE_RATIO_VOLUME");

  @Override
  public List<RealTimeStockVolumeVO> findTop5ByVolume (String orderBy) {

    // 허용된 컬럼명인지 검증 (injection 방지)
    if (!ALLOWED_ORDER_COLUMNS.contains(orderBy)) {
      throw new IllegalArgumentException("Invalid orderBy column: " + orderBy);
    }

    StringBuffer sql = new StringBuffer();

    sql.append(" SELECT ");
    sql.append(" m.STK_NM, ");
    sql.append("     r_today.VOLUME AS TODAY_VOLUME, ");
    sql.append(" r_yesterday.VOLUME AS YESTERDAY_VOLUME, ");
    sql.append("     (r_today.VOLUME - r_yesterday.VOLUME) AS CHANGE_VOLUME, ");
    sql.append("     CASE ");
    sql.append(" WHEN r_yesterday.VOLUME = 0 THEN NULL ");
    sql.append(" ELSE ROUND((r_today.VOLUME - r_yesterday.VOLUME) / r_yesterday.VOLUME * 100, 2) ");
    sql.append(" END AS CHANGE_RATIO_VOLUME ");
    sql.append(" FROM MKT_SEC_STK m ");
    sql.append(" JOIN RT_STK r_today ON m.STK_ID = r_today.STK_ID ");
    sql.append(" JOIN RT_STK r_yesterday ON m.STK_ID = r_yesterday.STK_ID ");
    sql.append(" WHERE ");
    sql.append(" TO_CHAR(r_today.CDATE, 'YY/MM/DD') = TO_CHAR(SYSDATE, 'YY/MM/DD') ");
    sql.append(" AND TO_CHAR(r_yesterday.CDATE, 'YY/MM/DD') = TO_CHAR(SYSDATE - 1, 'YY/MM/DD') ");
    sql.append(" ORDER BY " );
    sql.append(orderBy + " DESC NULLS LAST ");
    sql.append(" FETCH FIRST 5 ROWS ONLY ");

    SqlParameterSource param = new MapSqlParameterSource();

    List<RealTimeStockVolumeVO> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(RealTimeStockVolumeVO.class));
    return list;

  }

  @Override
  public List<RealTimeStockVolumeVO> findTop5ByChangeVolume() {
    return findTop5ByVolume("CHANGE_VOLUME");
  }

  @Override
  public List<RealTimeStockVolumeVO> findTop5ByChangeRatioVolume() {
    return findTop5ByVolume("CHANGE_RATIO_VOLUME");
  }
}
