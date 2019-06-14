package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.controller.dto.MeasureDto;
import com.training.spring.bigcorp.controller.dto.MeasureWithCaptorDto;
import com.training.spring.bigcorp.model.*;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.service.measure.MeasureService;
import com.training.spring.bigcorp.utils.SseEmitterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping(path = "/measures")
public class MeasureController {

    private static final Logger logger =
            LoggerFactory.getLogger(MeasureController.class);

    @Autowired
    private MeasureService measureService;

    @Autowired
    private SseEmitterUtils sseEmitterUtils;


    @Autowired
    private CaptorDao captorDao;

    /*
    //captors/{id}/last/hours/{nbHours}
    */
    @GetMapping("/captors/{id}/last/hours/{nbHours}")
    public List handle(@PathVariable String id, @PathVariable Integer nbHours){
        Captor captor = captorDao.findById(id).orElseThrow(IllegalArgumentException::new);
        if (captor.getPowerSource() == PowerSource.SIMULATED) {
            return  measureService.readMeasures(captor,Instant.now().minus(Duration.ofHours(nbHours)).truncatedTo(ChronoUnit.MINUTES),
                    Instant.now().truncatedTo(ChronoUnit.MINUTES),MeasureStep.ONE_MINUTE).stream()
                    .map(m -> new MeasureDto(m.getInstant(),
                            m.getValueInWatt()))
                    .collect(Collectors.toList());
            /* return simulatedMeasureService.readMeasures(((SimulatedCaptor) captor),
                    Instant.now().minus(Duration.ofHours(nbHours)).truncatedTo(ChronoUnit.MINUTES),
                    Instant.now().truncatedTo(ChronoUnit.MINUTES),
                    MeasureStep.ONE_MINUTE)
                    .stream()
                    .map(m -> new MeasureDto(m.getInstant(),
                            m.getValueInWatt()))
                    .collect(Collectors.toList());
                    return new ArrayList<>();*/
        }
// Pour le moment on ne gère qu'un type
        return new ArrayList<>();
    }

    @GetMapping
    public ModelAndView handle(Model model){
        ModelAndView mv = new ModelAndView("measures");
        mv.addObject("captors",
                captorDao.findAll()
                        .stream()
                        .sorted(Comparator.comparing(Captor::getName))
                        .map(c -> "{ id: '" + c.getId() + "', name: '" +
                                c.getName() + "'}")
                        .collect(Collectors.joining(",")));
        return mv;
    }

    @Scheduled(initialDelay = 2000, fixedDelay = 2000)
    public void readMeasure() {
        captorDao
                .findAll()
                .stream()
                .map(captor -> {
                    Measure measure = measureService.readAndSaveMeasure(captor);
                    return new MeasureWithCaptorDto(captor, measure.getInstant(),
                            measure.getValueInWatt());
                })
                .forEach(this::sendEventForUser);
    }
    private void sendEventForUser(MeasureWithCaptorDto measure) {
        sseEmitterUtils.getEmitters().forEach(sseEmitter -> {
            try {
                sseEmitter.send(measure);
            } catch (IOException e) {
                logger.error("Error on event emit", e);
            }
        });
    }

    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events() {
        return sseEmitterUtils.createEmitter();
    }
}
