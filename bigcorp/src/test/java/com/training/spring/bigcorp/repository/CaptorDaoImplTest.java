package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.PowerSource;
import com.training.spring.bigcorp.model.Site;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class CaptorDaoImplTest {

    @Autowired
    private CaptorDao captorDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private MeasureDao measureDao;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void findById() {
        Captor captor = captorDao.findById("c1");
        Assertions.assertThat(captor.getId()).isEqualTo("c1");
        Assertions.assertThat(captor.getName()).isEqualTo("Eolienne");
        Assertions.assertThat(captor.getSite().getId()).isEqualTo("site1");
        Assertions.assertThat(captor.getPowerSource()).isEqualTo(PowerSource.SIMULATED);
        Assertions.assertThat(captor.getDefaultPowerInWatt()).isNull();
    }

    @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
        Captor captor = captorDao.findById("c3");
        Assertions.assertThat(captor).isNull();
    }
    @Test
    public void findAll() {
        List<Captor> captors = captorDao.findAll();
        Assertions.assertThat(captors).hasSize(2);
    }

    @Test
    public void create() {
        Site newSite = new Site("site");
        newSite.setId("site2");
        siteDao.persist(newSite);

        Captor captor = new Captor("Voiture", newSite);
        captor.setId("c3");
        captor.setPowerSource(PowerSource.FIXED);
        Assertions.assertThat(captorDao.findAll()).hasSize(2);
        captorDao.persist(captor);
        Assertions.assertThat(captorDao.findAll()).hasSize(3)
                .extracting(Captor::getName)
                .contains("Eolienne", "Laminoire à chaud", "Voiture");
    }

    @Test
    public void update() {
        Captor captor = captorDao.findById("c1");
        Assertions.assertThat(captor.getName()).isEqualTo("Eolienne");
        captor.setName("Voiture");
        captorDao.persist(captor);
        captor = captorDao.findById("c1");
        Assertions.assertThat(captor.getName()).isEqualTo("Voiture");
    }
    @Test
    public void deleteById() {
        Assertions.assertThat(captorDao.findAll()).hasSize(2);
        measureDao.findAll().forEach(m -> {
            if (m.getCaptor().getId().equals("c1")){
                measureDao.delete(m);
            }
        });
        captorDao.delete(captorDao.findById("c1"));
        Assertions.assertThat(captorDao.findAll()).hasSize(1);
    }

    @Test
    public void deleteByIdShouldThrowExceptionWhenIdIsUsedAsForeignKey() {
        Captor captor = captorDao.findById("c1");
        Assertions
                .assertThatThrownBy(() -> {
                    captorDao.delete(captor);
                    entityManager.flush();
                })
                .isExactlyInstanceOf(PersistenceException.class)
                .hasCauseExactlyInstanceOf(ConstraintViolationException.class);
    }
}