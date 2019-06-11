package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.*;
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
        FixedCaptor captor = (FixedCaptor)captorDao.findById("c1").get();
        Assertions.assertThat(captor.getId()).isEqualTo("c1");
        Assertions.assertThat(captor.getName()).isEqualTo("Eolienne");
        Assertions.assertThat(captor.getSite().getId()).isEqualTo("site1");
        Assertions.assertThat(captor.getDefaultPowerInWatt()).isEqualTo(1000000);
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
                .withIgnorePaths("defaultPowerInWatt")
                .withIgnoreNullValues();

        Site site = new Site();
        site.setId("site1");

        Captor captor = new FixedCaptor("lienne",site);
        List<Captor> captors = captorDao.findAll(Example.of(captor, matcher));
        Assertions.assertThat(captors)
                .hasSize(1)
                .extracting("id", "name")
                .containsExactly(Tuple.tuple("c1", "Eolienne"));
    }

    @Test
    public void createReal() {
        Site newSite = new Site("site");
        newSite.setId("site2");
        siteDao.save(newSite);

        Captor captor = new RealCaptor("Voiture", newSite);
        captor.setId("c3");
        Assertions.assertThat(captorDao.findAll()).hasSize(2);
        captorDao.save(captor);
        Assertions.assertThat(captorDao.findAll()).hasSize(3)
                .extracting(Captor::getName)
                .contains("Eolienne", "Laminoire à chaud", "Voiture");
    }

    @Test
    public void createFixed() {
        Site newSite = new Site("site");
        newSite.setId("site2");
        siteDao.save(newSite);

        Captor captor = new FixedCaptor("Voiture", newSite, new Long(10000));
        captor.setId("c3");
        Assertions.assertThat(captorDao.findAll()).hasSize(2);
        captorDao.save(captor);
        Assertions.assertThat(captorDao.findAll()).hasSize(3)
                .extracting(Captor::getName)
                .contains("Eolienne", "Laminoire à chaud", "Voiture");
    }


    @Test
    public void createSimulated() {
        Site newSite = new Site("site");
        newSite.setId("site2");
        siteDao.save(newSite);

        Captor captor = new SimulatedCaptor("Voiture", newSite,new Long(200),new Long(1000000));
        captor.setId("c3");
        Assertions.assertThat(captorDao.findAll()).hasSize(2);
        captorDao.save(captor);
        Assertions.assertThat(captorDao.findAll()).hasSize(3)
                .extracting(Captor::getName)
                .contains("Eolienne", "Laminoire à chaud", "Voiture");
    }

    @Test
    public void createWithNulldName() {
        Site newSite = new Site("site");
        newSite.setId("site2");
        siteDao.save(newSite);

        Captor captor = new RealCaptor(null, newSite);
        captor.setId("c3");
        Assertions
                .assertThatThrownBy(() -> {
                    captorDao.save(captor);
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("ne peut pas être nul");
    }

    @Test
    public void createWithInvalidName() {
        Site newSite = new Site("site");
        newSite.setId("site2");
        siteDao.save(newSite);

        Captor captor = new RealCaptor("V", newSite);
        captor.setId("c3");
        Assertions
                .assertThatThrownBy(() -> {
                    captorDao.save(captor);
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("la taille doit être comprise entre 3 et 100");
    }


    @Test
    public void createSimulatedWithMinSupMax() {
        Site newSite = new Site("site");
        newSite.setId("site2");
        siteDao.save(newSite);

        Captor captor = new SimulatedCaptor("Voiture", newSite,new Long(200000),new Long(100));
        captor.setId("c3");
        Assertions
                .assertThatThrownBy(() -> {
                    captorDao.save(captor);
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("minPowerInWatt : should be less than maxPowerInWatt");
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

    @Test
    public void preventConcurrentWrite() {
        Captor captor = captorDao.getOne("c1");

        // A la base le numéro de version est à sa valeur initiale
        Assertions.assertThat(captor.getVersion()).isEqualTo(0);

        // On detache cet objet du contexte de persistence
        entityManager.detach(captor);
        captor.setName("Captor updated");

        // On force la mise à jour en base (via le flush) et on vérifie que l'obje retourné
        // et attaché à la session a été mis à jour
        Captor attachedCaptor = captorDao.save(captor);
        entityManager.flush();
        Assertions.assertThat(attachedCaptor.getName()).isEqualTo("Captor updated");
        Assertions.assertThat(attachedCaptor.getVersion()).isEqualTo(1);

        // Si maintenant je réessaie d'enregistrer captor, comme le numéro de version est
        // à 0 je dois avoir une exception
        Assertions.assertThatThrownBy(() -> captorDao.save(captor))
                .isExactlyInstanceOf(ObjectOptimisticLockingFailureException.class);
    }
}