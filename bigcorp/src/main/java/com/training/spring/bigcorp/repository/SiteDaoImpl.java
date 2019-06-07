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
public class SiteDaoImpl implements SiteDao{

    private NamedParameterJdbcTemplate jdbcTemplate;
    public SiteDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void create(Site element) {
        jdbcTemplate.update("insert into SITE " +
                "values(:id,:name)",
                new MapSqlParameterSource()
                        .addValue("name",element.getName())
                        .addValue("id",element.getId()));
    }

    @Override
    public Site findById(String s) {
        Map<String,String> keyMap = new HashMap<>();
        keyMap.put("id",s);

        List<Site> resultSites = jdbcTemplate.query("SELECT s.id, s.name" +
                " from Site s where s.id=:id",keyMap,this::siteMapper);
        if(resultSites.isEmpty()){
            return null;
        }
        return resultSites.get(0);

    }

    @Override
    public List<Site> findAll() {
        return jdbcTemplate.query("SELECT s.id, s.name" +
                " from Site s",this::siteMapper);
    }




    private Site siteMapper(ResultSet rs, int rowNum) throws SQLException {
        Site site = new Site(rs.getString("name"));
        site.setId(rs.getString("id"));
        return site;
    }


    @Override
    public void update(Site element) {
        jdbcTemplate.update("update SITE set name = :name where id=:id",new MapSqlParameterSource()
            .addValue("name",element.getName())
            .addValue("id",element.getId()));
    }

    @Override
    public void deleteById(String s) {
        jdbcTemplate.update("delete from SITE" +
                " where id=:id",
                new MapSqlParameterSource()
                .addValue("id",s));
    }
}
