package com.kh.finalproject.domain.stocklist.dao;

import com.kh.finalproject.domain.dto.StockListDto;
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

public class StockListDAOImpl implements StockListDAO{

  private final NamedParameterJdbcTemplate template;


  @Override
  public List<StockListDto> getStockList(int marketId, String orderBy, int risk, int offset) {

    // 허용된 정렬 기준 목록
    List<String> validOrderBys = List.of("R.MARCAP", "T.TRAIT_STK_RISK", "R.CHANGE_RATIO", "R.VOLUME");

    // orderBy 검증
    if (!validOrderBys.contains(orderBy.toUpperCase())) {
      throw new IllegalArgumentException("Invalid orderBy parameter: " + orderBy);
    }

    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT ");
    sql.append(" t.stk_id, ");
    sql.append("     m.stk_nm, ");
    sql.append("     r.price, ");
    sql.append("     r.change, ");
    sql.append("     r.change_ratio, ");
    sql.append("     r.volume, ");
    sql.append("     TO_CHAR(r.amount / 1000000, 'FM9,999,999') AS amount, ");
    sql.append(" TO_CHAR(r.marcap / 100000000, 'FM9,999,999') AS marcap, ");
    sql.append(" t.trait_stk_risk ");
    sql.append("     FROM ");
    sql.append(" mkt_sec_stk m ");
    sql.append(" JOIN ");
    sql.append(" rt_stk r ON m.stk_id = r.stk_id ");
    sql.append(" JOIN ");
    sql.append(" trait_stk t ON r.stk_id = t.stk_id ");
    sql.append("     WHERE ");
    sql.append(" m.market_id = :marketId and t.trait_stk_risk <= :risk ");
    sql.append(" ORDER BY ");
    sql.append(orderBy + " DESC ");
    sql.append(" OFFSET :offset ROWS FETCH NEXT 10 ROWS ONLY ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("marketId",marketId)
        .addValue("risk", risk)
        .addValue("offset", offset);

    List<StockListDto> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(StockListDto.class));
    return list;
  }
}
