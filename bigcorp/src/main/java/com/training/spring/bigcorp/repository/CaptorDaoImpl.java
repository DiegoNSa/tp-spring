package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Site;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CaptorDaoImpl implements CaptorDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Captor> findBySiteId(String siteId) {
        return em.createQuery("select c from Captor c inner join c.site s where s.id =:siteId",
        Captor.class).setParameter("siteId", siteId)
                .getResultList();
    }

    @Override
    public void persist(Captor captor) {
        em.persist(captor);
    }

    @Override
    public Captor findById(String id) {
        return em.find(Captor.class, id);
    }

    @Override
    public List<Captor> findAll() {
        return em.createQuery("select c from Captor c inner join c.site s",
                Captor.class)
                .getResultList();
    }

    @Override
    public void delete(Captor captor) {
        em.remove(captor);
    }
}
