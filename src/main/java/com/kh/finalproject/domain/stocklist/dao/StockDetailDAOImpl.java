package com.kh.finalproject.domain.stocklist.dao;

import com.kh.finalproject.domain.dto.StockListDto;
import com.kh.finalproject.domain.dto.StockNewsDto;
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

public class StockDetailDAOImpl implements StockDetailDAO{

  private final NamedParameterJdbcTemplate template;

  @Override
  public List<StockNewsDto> getStockNewsList(Long stkId) {

    StringBuffer sql = new StringBuffer();

    // 특정 종목 뉴스 가져오기
    sql.append(" SELECT DISTINCT n.TITLE, m.STK_NM, m.STK_CODE, n.MEDIA_NAME, n.PUBLISHED_DATE, n.NEWS_URL ");
    sql.append(" FROM MKT_SEC_STK m ");
    sql.append(" JOIN NEWS n ON m.STK_ID = n.STK_ID ");
    sql.append(" WHERE n.STK_ID = :stkId ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("stkId", stkId);

    List<StockNewsDto> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(StockNewsDto.class));

    return list;
  }

  // 종목 코드로 종목 id 가져오기
  @Override
  public Long getStkIdByStkCode(String stkCode) {

    StringBuffer sql = new StringBuffer();

    sql.append(" SELECT STK_ID ");
    sql.append(" FROM STOCKS ");
    sql.append(" WHERE STK_CODE = :stkCode ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("stkCode",stkCode);

    Long stkId = template.queryForObject(sql.toString(), param, Long.class);
    return stkId;
  }

  @Override
  public String getStkNmByStkCode(String stkCode) {

    StringBuffer sql = new StringBuffer();

    sql.append(" SELECT STK_NM ");
    sql.append(" FROM STOCKS ");
    sql.append(" WHERE STK_CODE = :stkCode ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("stkCode",stkCode);

    String stkNm = template.queryForObject(sql.toString(),param, String.class);
    return stkNm;
  }

  @Override
  public List<StockListDto> getStockDetail(String stkCode) {

    StringBuffer sql = new StringBuffer();

    sql.append(" SELECT ");
    sql.append(" t.STK_ID, ");
    sql.append("     m.STK_CODE, ");
    sql.append("     m.STK_NM, ");
    sql.append("     r.PRICE, ");
    sql.append("     r.CHANGE, ");
    sql.append("     r.CHANGE_RATIO, ");
    sql.append("     r.VOLUME, ");
    sql.append("     TO_CHAR(r.amount / 1000000, 'FM9,999,999') AS AMOUNT, ");
    sql.append(" TO_CHAR(r.marcap / 100000000, 'FM9,999,999') AS MARCAP, ");
    sql.append(" t.TRAIT_STK_RISK ");
    sql.append(" FROM MKT_SEC_STK m ");
    sql.append(" JOIN RT_STK r ON m.STK_ID = r.STK_ID ");
    sql.append(" JOIN TRAIT_STK t ON r.STK_ID = t.STK_ID ");
    sql.append(" WHERE STK_CODE = :stkCode ");
    sql.append(" AND r.CDATE = (SELECT MAX(CDATE) FROM RT_STK WHERE STK_ID = m.STK_ID) ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("stkCode", stkCode);

    List<StockListDto> list = template.query(sql.toString(), param, new BeanPropertyRowMapper<>(StockListDto.class));
    return list;
  }

  @Override
  public String getStkCodeByStkNm(String stkNm) {

    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT STK_CODE ");
    sql.append(" FROM STOCKS ");
    sql.append(" WHERE STK_NM = :stkNm ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("stkNm",stkNm);

    String stkCode = template.queryForObject(sql.toString(),param, String.class);
    return stkCode;
  }
}
