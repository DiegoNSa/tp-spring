package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface MeasureDao extends JpaRepository<Measure,Long> {
    void deleteByCaptorId(String captorId);

    @Query("select m from Measure m join m.captor c where c.id=:captor_id AND (m.instant BETWEEN :start AND :end)")
    List<Measure> findMeasureByIntervalAndCaptor(@Param("start") Instant start, @Param("end") Instant end, @Param("captor_id") String captor_id);

    Measure findTopByCaptorIdOrderByInstantDesc(String captorId);
    Measure findTopByCaptorIdOrderByInstantAsc(String captorId);

}
