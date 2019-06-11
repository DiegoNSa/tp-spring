package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.utils.H2DateConverter;
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
public class MeasureDaoImpl implements MeasureDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public void persist(Measure element) {
        em.persist(element);
    }

    @Override
    public Measure findById(Long aLong) {
        return em.find(Measure.class, aLong);
    }

    @Override
    public List<Measure> findAll() {
        return em.createQuery("select m from Measure m inner join m.captor c",
                Measure.class)
                .getResultList();
    }

    @Override
    public void delete(Measure measure) {
        em.remove(measure);
    }
}
