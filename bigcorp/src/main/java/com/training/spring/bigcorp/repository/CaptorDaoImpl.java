package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Site;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CaptorDaoImpl implements CaptorDao {

    private NamedParameterJdbcTemplate jdbcTemplate;
    public CaptorDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Captor> findBySiteId(String siteId) {
        Map<String,String> keyMap = new HashMap<>();
        keyMap.put("id",siteId);
        return jdbcTemplate.query(SELECT_WITH_JOIN + " where s.id = :id", keyMap,this::captorMapper);
    }

    @Override
    public void create(Captor element) {
        jdbcTemplate.update("insert into Captor " +
                        "values(:id,:name,:site_id)",
                new MapSqlParameterSource()
                        .addValue("name",element.getName())
                        .addValue("site_id",element.getSite().getId())
                        .addValue("id",element.getId()));
    }


    private static String SELECT_WITH_JOIN =
            "SELECT c.id, c.name, c.site_id, s.name as site_name " +
                    "FROM Captor c inner join Site s on c.site_id = s.id ";

    @Override
    public Captor findById(String s) {
        Map<String,String> keyMap = new HashMap<>();
        keyMap.put("id",s);
        List<Captor> resultList = jdbcTemplate.query(SELECT_WITH_JOIN + " where c.id = :id", keyMap,this::captorMapper);
        if(resultList.isEmpty()){
            return null;
        }
        return resultList.get(0);
    }

    @Override
    public List<Captor> findAll() {
        return jdbcTemplate.query(SELECT_WITH_JOIN, this::captorMapper);
    }
    private Captor captorMapper(ResultSet rs, int rowNum) throws SQLException {
        Site site = new Site(rs.getString("site_name"));
        site.setId(rs.getString("site_id"));
        Captor captor = new Captor(rs.getString("name"), site);
        captor.setId(rs.getString("id"));
        return captor;
    }

    @Override
    public void update(Captor element) {
        jdbcTemplate.update("update Captor set name = :name, site_id = :siteId where id =:id ",
                new MapSqlParameterSource()
                        .addValue("id", element.getId())
                        .addValue("siteId", element.getSite().getId())
                        .addValue("name", element.getName()));
    }

    @Override
    public void deleteById(String s) {
        jdbcTemplate.update("delete from CAPTOR" +
                        " where id=:id",
                new MapSqlParameterSource()
                        .addValue("id",s));
    }
}
