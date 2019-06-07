package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.Site;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@JdbcTest
@ContextConfiguration(classes = {DaoTestConfig.class})
public class MeasureDaoImplTest {
    @Autowired
    private MeasureDao measureDao;
    @Autowired
    private CaptorDao captorDao;
    private Site site;
    private Captor captor;

    @Before
    public void init(){
        site = new Site("name");
        site.setId("site1");
        captor = new Captor("New captor",site);
        captor.setId("c3");
    }

    @Test
    public void findById() {
        Measure measure = measureDao.findById(new Long(1));
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(1000000);
    }
   @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
       Measure measure = measureDao.findById(new Long(-1));
        Assertions.assertThat(measure).isNull();
    }

    @Test
    public void findAll() {
        List<Measure> measures = measureDao.findAll();
        Assertions.assertThat(measures)
                .hasSize(10);
        Assertions.assertThat(measures.get(0).getCaptor().getId()).isEqualTo("c1");
        Assertions.assertThat(measures.get(1).getCaptor().getId()).isEqualTo("c1");
        Assertions.assertThat(measures.get(9).getCaptor().getId()).isEqualTo("c2");
    }

    @Test
    public void create() {
        Assertions.assertThat(measureDao.findAll()).hasSize(10);
        captorDao.create(captor);
        Measure newMeasure = new Measure(Instant.now(), new Integer(400),captor);
        newMeasure.setId(new Long(12));
        measureDao.create(newMeasure);
        Assertions.assertThat(measureDao.findAll())
                .hasSize(11)
                .extracting(Measure::getValueInWatt)
                .contains(new Integer(400));
    }
    @Test
    public void update() {
        captorDao.create(captor);
        Measure measure = measureDao.findById(new Long(1));
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(new Integer(1000000));
        measure.setValueInWatt(new Integer(200000));
        measureDao.update(measure);
        measure = measureDao.findById(new Long(1));
        Assertions.assertThat(measure.getValueInWatt().intValue() ==200000);
    }
   @Test
    public void deleteById() {
        captorDao.create(captor);
        Measure newMeasure = new Measure(Instant.now(), new Integer(400),captor);
        newMeasure.setId(new Long(12));
        measureDao.create(newMeasure);
        Assertions.assertThat(measureDao.findById(newMeasure.getId())).isNotNull();
       measureDao.deleteById(newMeasure.getId());
        Assertions.assertThat(measureDao.findById(newMeasure.getId())).isNull();
    }

}