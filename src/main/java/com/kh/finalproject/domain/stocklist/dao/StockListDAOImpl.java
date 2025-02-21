package com.kh.finalproject.domain.stocklist.dao;

import com.kh.finalproject.domain.dto.SectorListDto;
import com.kh.finalproject.domain.dto.StockListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor

public class StockListDAOImpl implements StockListDAO{

  private final NamedParameterJdbcTemplate template;

  @Override
  public List<StockListDto> getStockList(int marketId, String orderBy, int risk, int offset, Long secId) {
    // 허용된 정렬 기준을 안전한 SQL 컬럼명으로 매핑
    Map<String, String> orderByMap = Map.of(
        "R.MARCAP", "r.marcap",
        "T.TRAIT_STK_RISK", "t.trait_stk_risk",
        "R.CHANGE_RATIO", "r.change_ratio",
        "R.VOLUME", "r.volume"
    );

    String safeOrderBy = orderByMap.get(orderBy.toUpperCase());
    if (safeOrderBy == null) {
      throw new IllegalArgumentException("Invalid orderBy parameter: " + orderBy);
    }

    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT ");
    sql.append(" t.stk_id, m.stk_code, m.stk_nm, m.sec_nm, ");
    sql.append(" r.price, r.change, r.change_ratio, r.volume, ");
    sql.append(" TO_CHAR(r.amount / 1000000, 'FM9,999,999') AS amount, ");
    sql.append(" TO_CHAR(r.marcap / 100000000, 'FM9,999,999') AS marcap, ");
    sql.append(" t.trait_stk_risk ");
    sql.append(" FROM mkt_sec_stk m ");
    sql.append(" JOIN ( ");
    sql.append("     SELECT r1.* FROM rt_stk r1 ");
    sql.append("     WHERE r1.cdate = (SELECT MAX(r2.cdate) FROM rt_stk r2 WHERE r1.stk_id = r2.stk_id) ");
    sql.append(" ) r ON m.stk_id = r.stk_id ");
    sql.append(" JOIN trait_stk t ON r.stk_id = t.stk_id ");
    sql.append(" WHERE m.market_id = :marketId ");
    sql.append(" AND t.trait_stk_risk <= :risk ");

    // secId가 주어진 경우 조건 추가
    if (secId != null) {
      sql.append(" AND m.sec_id = :secId ");
    }

    sql.append(" ORDER BY ").append(safeOrderBy).append(" DESC ");
    sql.append(" OFFSET :offset ROWS FETCH NEXT 10 ROWS ONLY ");

    // SQL 파라미터 설정
    MapSqlParameterSource param = new MapSqlParameterSource()
        .addValue("marketId", marketId)
        .addValue("risk", risk)
        .addValue("offset", offset);

    if (secId != null) {
      param.addValue("secId", secId);
    }

    return template.query(sql.toString(), param, new BeanPropertyRowMapper<>(StockListDto.class));
  }

  @Override
  public List<SectorListDto> getSectorList(int marketId) {

    StringBuffer sql = new StringBuffer();

    sql.append(" SELECT SEC_ID, SEC_NM, MARKET_ID ");
    sql.append(" FROM SECTORS ");
    sql.append(" WHERE MARKET_ID = :marketId ");


    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("marketId", marketId);

    List<SectorListDto> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(SectorListDto.class));
    return list;
  }
}
