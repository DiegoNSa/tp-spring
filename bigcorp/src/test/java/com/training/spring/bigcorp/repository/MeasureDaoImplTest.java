package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.FixedCaptor;
import com.training.spring.bigcorp.model.Measure;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class MeasureDaoImplTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MeasureDao measureDao;
    @Test
    public void findById() {
        Measure measure = measureDao.findById(-1L).get();
        Assertions.assertThat(measure.getId()).isEqualTo(-1L);
        Assertions.assertThat(measure.getInstant()).isEqualTo(Instant.parse("2018-08-09T11:00:00.000Z"));
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(1_000_000);
        Assertions.assertThat(measure.getCaptor().getName()).isEqualTo("Eolienne");
        Assertions.assertThat(measure.getCaptor().getSite().getName()).isEqualTo("Bigcorp Lyon");
    }
    @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
        Optional<Measure> measure = measureDao.findById(-1000L);
        Assertions.assertThat(measure.isPresent()).isFalse();
    }
    @Test
    public void findAll() {
        List<Measure> measures = measureDao.findAll();
        Assertions.assertThat(measures).hasSize(10);
    }
    @Test
    public void create() {
        Captor captor = new FixedCaptor("Eolienne", new Site("site"),(long)1000000);
        captor.setId("c1");
        Assertions.assertThat(measureDao.findAll()).hasSize(10);
        measureDao.save(new Measure(Instant.now(), 2_333_666, captor));
        Assertions.assertThat(measureDao.findAll()).hasSize(11);
    }
    @Test
    public void update() {
        Measure measure = measureDao.findById(-1L).get();
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(1_000_000);
        measure.setValueInWatt(2_333_666);
        measureDao.save(measure);
        measure = measureDao.findById(-1L).get();
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(2_333_666);
    }
    @Test
    public void deleteById() {
        Assertions.assertThat(measureDao.findAll()).hasSize(10);
        measureDao.delete(measureDao.findById(-1L).get());
        Assertions.assertThat(measureDao.findAll()).hasSize(9);
    }

    @Test
    public void findMeasureByIntervalAndCaptor() {

        List<Measure> measures = measureDao.findMeasureByIntervalAndCaptor(Instant.parse("2018-08-09T11:01:30.000Z"),Instant.parse("2018-08-09T11:03:30.000Z"),"c1");
        Assertions.assertThat(measures).hasSize(2);
    }

    @Test
    public void preventConcurrentWrite() {
        Measure measure = measureDao.getOne(-1L);

        // A la base le numéro de version est à sa valeur initiale
        Assertions.assertThat(measure.getVersion()).isEqualTo(0);

        // On detache cet objet du contexte de persistence
        entityManager.detach(measure);
        measure.setValueInWatt(new Integer(123456));

        // On force la mise à jour en base (via le flush) et on vérifie que l'obje retourné
        // et attaché à la session a été mis à jour
        Measure attachedMeasure = measureDao.save(measure);
        measureDao.flush();
        Assertions.assertThat(attachedMeasure.getValueInWatt()).isEqualTo(123456);
        Assertions.assertThat(attachedMeasure.getVersion()).isEqualTo(1);

        // Si maintenant je réessaie d'enregistrer captor, comme le numéro de version est
        // à 0 je dois avoir une exception
        Assertions.assertThatThrownBy(() -> measureDao.save(measure))
                .isExactlyInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    public void findTopByCaptorIdOrderByInstantDesc() {
        Measure lastMeasure = measureDao.findTopByCaptorIdOrderByInstantDesc("c1");
        Assertions.assertThat(lastMeasure.getId()).isEqualTo(-5L);
        Assertions.assertThat(lastMeasure.getInstant()).isEqualTo(Instant.parse("2018-08-09T11:04:00.000Z"));
        Assertions.assertThat(lastMeasure.getValueInWatt()).isEqualTo(1_009_678);
        Assertions.assertThat(lastMeasure.getCaptor().getName()).isEqualTo("Eolienne");
        Assertions.assertThat(lastMeasure.getCaptor().getSite().getName()).isEqualTo("Bigcorp Lyon");
    }


    @Test
    public void deleteByCaptorId() {
        Assertions.assertThat(measureDao.findAll().stream().filter(m ->
                m.getCaptor().getId().equals("c1"))).hasSize(5);
        measureDao.deleteByCaptorId("c1");
        Assertions.assertThat(measureDao.findAll().stream().filter(m ->
                m.getCaptor().getId().equals("c1"))).isEmpty();
    }
}