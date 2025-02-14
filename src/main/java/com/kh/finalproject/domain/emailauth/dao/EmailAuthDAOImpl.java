package com.kh.finalproject.domain.emailauth.dao;

import com.kh.finalproject.domain.entity.AuthCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor

public class EmailAuthDAOImpl implements EmailAuthDAO {

  @PersistenceContext
  private EntityManager entityManager;


  @Override
  public AuthCode findByEmail(String email) {
    TypedQuery<AuthCode> query = entityManager.createQuery(
        "SELECT a FROM AuthCode a WHERE a.email = :email ORDER BY a.expirationTime DESC", AuthCode.class);
    query.setParameter("email", email);
    List<AuthCode> results = query.setMaxResults(1).getResultList();
    return results.isEmpty() ? null : results.get(0);
  }

  @Override
  public void save(AuthCode authCode) {
    entityManager.persist(authCode);

  }
}
