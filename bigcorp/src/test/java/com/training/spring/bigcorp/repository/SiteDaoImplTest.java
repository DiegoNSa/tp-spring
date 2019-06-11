package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.PowerSource;
import com.training.spring.bigcorp.model.Site;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class SiteDaoImplTest {
    @Autowired
    private SiteDao siteDao;

    @Autowired
    private EntityManager entityManager;


    @Test
    public void findById() {
        Site site = siteDao.findById("site1").get();
        Assertions.assertThat(site.getId()).isEqualTo("site1");
        Assertions.assertThat(site.getName()).isEqualTo("Bigcorp Lyon");
    }
    @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
        Optional<Site> site = siteDao.findById("unknown");
        Assertions.assertThat(site.isPresent()).isFalse();
    }
    @Test
    public void findAll() {
        List<Site> sites = siteDao.findAll();
        Assertions.assertThat(sites)
                .hasSize(1)
                .extracting("id", "name")
                .contains(Tuple.tuple("site1", "Bigcorp Lyon"));
    }
    @Test
    public void create() {
        Site site = new Site("Site");
        site.setId("site2");
        Assertions.assertThat(siteDao.findAll()).hasSize(1);
        siteDao.save(site);
        Assertions.assertThat(siteDao.findAll()).hasSize(2)
                .extracting("id", "name")
                .contains(Tuple.tuple("site1", "Bigcorp Lyon"))
                .contains(Tuple.tuple("site2", "Site"));
    }

    @Test
    public void update() {
        Site site = siteDao.findById("site1").get();
        Assertions.assertThat(site.getName()).isEqualTo("Bigcorp Lyon");
        site.setName("Site updated");
        siteDao.save(site);
        site = siteDao.findById("site1").get();
        Assertions.assertThat(site.getName()).isEqualTo("Site updated");
    }
    @Test
    public void deleteById() {
        Site site = new Site("Site");
        site.setId("site2");
        siteDao.save(site);
        Assertions.assertThat(siteDao.findAll()).hasSize(2);
        siteDao.delete(siteDao.findById("site2").get());
        Assertions.assertThat(siteDao.findAll()).hasSize(1);
    }

    @Test
    public void deleteByIdShouldThrowExceptionWhenIdIsUsedAsForeignKey() {
        Site site = siteDao.findById("site1").get();
        Assertions
                .assertThatThrownBy(() -> {
                    siteDao.delete(site);
                    entityManager.flush();
                })
                .isExactlyInstanceOf(PersistenceException.class)
                .hasCauseExactlyInstanceOf(ConstraintViolationException.class);
    }
}