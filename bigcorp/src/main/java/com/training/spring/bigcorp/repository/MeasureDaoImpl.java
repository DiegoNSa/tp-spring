package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.utils.H2DateConverter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MeasureDaoImpl implements MeasureDao {
    private static String SELECT_WITH_JOIN =
            "SELECT m.id, m.instant, m.value_in_watt, m.captor_id, c.name as " +
                    "captor_name, c.site_id, s.name as site_name " +
            "FROM Measure m inner join Captor c on m.captor_id=c.id inner join " +
                    "site s on c.site_id = s.id ";


    private NamedParameterJdbcTemplate jdbcTemplate;
    private H2DateConverter h2DateConverter;
    public MeasureDaoImpl(NamedParameterJdbcTemplate jdbcTemplate, H2DateConverter h2DateConverter) {
        this.jdbcTemplate = jdbcTemplate;
        this.h2DateConverter = h2DateConverter;
    }


    @Override
    public void create(Measure element) {
        System.out.println("==================================== : ");
        System.out.println(element.getCaptor().getId());
        System.out.println(element.getInstant());
        System.out.println(element.getValueInWatt());

        jdbcTemplate.update("insert into Measure  (INSTANT, VALUE_IN_WATT, CAPTOR_ID)" +
                        " values(:instant,:value,:captor_id)",
                new MapSqlParameterSource()
                        .addValue("instant",element.getInstant())
                        .addValue("value",element.getValueInWatt())
                        .addValue("captor_id",element.getCaptor().getId()));
    }

    @Override
    public Measure findById(Long aLong) {
        Map<String,Long> keyMap = new HashMap<>();
        keyMap.put("id",aLong);
        List<Measure> resultList = jdbcTemplate.query(SELECT_WITH_JOIN + " where m.id = :id", keyMap,this::measureMapper);
        if(resultList.isEmpty()){
            return null;
        }
        return resultList.get(0);    }

    @Override
    public List<Measure> findAll() {
        return jdbcTemplate.query(SELECT_WITH_JOIN,this::measureMapper);
    }


    private Measure measureMapper(ResultSet rs, int rowNum) throws SQLException {
        Site site = new Site(rs.getString("site_name"));
        site.setId(rs.getString("site_id"));
        Captor captor = new Captor(rs.getString("captor_name"), site);
        captor.setId(rs.getString("captor_id"));
        Measure measure = new Measure(h2DateConverter.convert(rs.getString("instant")),
                rs.getInt("value_in_watt"),
                captor);
        measure.setId(rs.getLong("id"));
        return measure;
    }


    @Override
    public void update(Measure element) {
        jdbcTemplate.update("update MEASURE set instant = :instant, value_in_watt = :value_in_watt, captor_id = :captor_id where id=:id",new MapSqlParameterSource()
                .addValue("instant",element.getInstant())
                .addValue("value_in_watt",element.getValueInWatt())
                .addValue("captor_id",element.getCaptor().getId())
                .addValue("id",element.getId()));
    }

    @Override
    public void deleteById(Long aLong) {
        jdbcTemplate.update("delete from MEASURE" +
                        " where id=:id",
                new MapSqlParameterSource()
                        .addValue("id",aLong));

    }
}
