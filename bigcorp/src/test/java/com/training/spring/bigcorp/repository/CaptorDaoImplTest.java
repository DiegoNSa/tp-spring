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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
        Captor captor = captorDao.findById("c1").get();
        Assertions.assertThat(captor.getId()).isEqualTo("c1");
        Assertions.assertThat(captor.getName()).isEqualTo("Eolienne");
        Assertions.assertThat(captor.getSite().getId()).isEqualTo("site1");
        Assertions.assertThat(captor.getPowerSource()).isEqualTo(PowerSource.SIMULATED);
        Assertions.assertThat(captor.getDefaultPowerInWatt()).isNull();
    }

    @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
        Optional<Captor> captor = captorDao.findById("c3");
        Assertions.assertThat(captor.isPresent()).isEqualTo(false);
    }
    @Test
    public void findAll() {
        List<Captor> captors = captorDao.findAll();
        Assertions.assertThat(captors).hasSize(2);
    }

    @Test
    public void findByExample() {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", matcher1 -> matcher1.ignoreCase().contains())
                .withIgnorePaths("id")
                .withIgnoreNullValues();

        Site site = new Site();
        site.setId("site1");

        Captor captor = new Captor("lienne",site);
        List<Captor> captors = captorDao.findAll(Example.of(captor, matcher));
        Assertions.assertThat(captors)
                .hasSize(1)
                .extracting("id", "name")
                .containsExactly(Tuple.tuple("c1", "Eolienne"));
    }

    @Test
    public void create() {
        Site newSite = new Site("site");
        newSite.setId("site2");
        siteDao.save(newSite);

        Captor captor = new Captor("Voiture", newSite);
        captor.setId("c3");
        captor.setPowerSource(PowerSource.FIXED);
        Assertions.assertThat(captorDao.findAll()).hasSize(2);
        captorDao.save(captor);
        Assertions.assertThat(captorDao.findAll()).hasSize(3)
                .extracting(Captor::getName)
                .contains("Eolienne", "Laminoire à chaud", "Voiture");
    }

    @Test
    public void update() {
        Captor captor = captorDao.findById("c1").get();
        Assertions.assertThat(captor.getName()).isEqualTo("Eolienne");
        captor.setName("Voiture");
        captorDao.save(captor);
        captor = captorDao.findById("c1").get();
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
        captorDao.delete(captorDao.findById("c1").get());
        Assertions.assertThat(captorDao.findAll()).hasSize(1);
    }

    @Test
    public void deleteByIdShouldThrowExceptionWhenIdIsUsedAsForeignKey() {
        Captor captor = captorDao.findById("c1").get();
        Assertions
                .assertThatThrownBy(() -> {
                    captorDao.delete(captor);
                    entityManager.flush();
                })
                .isExactlyInstanceOf(PersistenceException.class)
                .hasCauseExactlyInstanceOf(ConstraintViolationException.class);
    }
}