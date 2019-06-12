package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.controller.dto.MeasureDto;
import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.MeasureStep;
import com.training.spring.bigcorp.model.PowerSource;
import com.training.spring.bigcorp.model.SimulatedCaptor;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.service.measure.SimulatedMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping(path = "/measures/captors/{id}/last/hours/{nbHours}")
public class MeasureController {

    @Autowired
    private SimulatedMeasureService simulatedMeasureService;

    @Autowired
    private CaptorDao captorDao;


    @GetMapping
    public List handle(@PathVariable String id, @PathVariable Integer nbHours){
        Captor captor = captorDao.findById(id).orElseThrow(IllegalArgumentException::new);
        if (captor.getPowerSource() == PowerSource.SIMULATED) {
            return simulatedMeasureService.readMeasures(((SimulatedCaptor) captor),
                    Instant.now().minus(Duration.ofHours(nbHours)).truncatedTo(ChronoUnit.MINUTES),
                    Instant.now().truncatedTo(ChronoUnit.MINUTES),
                    MeasureStep.ONE_MINUTE)
                    .stream()
                    .map(m -> new MeasureDto(m.getInstant(),
                            m.getValueInWatt()))
                    .collect(Collectors.toList());
        }
// Pour le moment on ne gère qu'un type
        return new ArrayList<>();
    }
}
