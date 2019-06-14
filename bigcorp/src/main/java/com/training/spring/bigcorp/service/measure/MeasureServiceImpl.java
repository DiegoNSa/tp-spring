package com.training.spring.bigcorp.service.measure;

import com.training.spring.bigcorp.model.*;
import com.training.spring.bigcorp.repository.MeasureDao;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class MeasureServiceImpl implements MeasureService {

    private static Integer lastValue;

    private MeasureDao measureDao;

    private RestTemplate restTemplate;

    public MeasureServiceImpl(MeasureDao measureDao, RestTemplateBuilder builder) {
        this.measureDao = measureDao;
        this.restTemplate = builder.setConnectTimeout(3600).build();
    }

    @Override
    public List<Measure> readMeasures(Captor captor, Instant start, Instant end,
                                      MeasureStep step) {
        checkReadMeasuresAgrs(captor, start, end, step);
        Set<MeasureByInterval> measureByIntervals = computeIntervals(start, end,
                step);
        List<Measure> measures = measureDao.findMeasureByIntervalAndCaptor(start, end,
                captor.getId());

        // A coder : Vous devez ici ajouter chacune des mesures retournées par le DAO dans le Set `measureByIntervals`
        for(Measure measure : measures){
            for(MeasureByInterval measureByInterval : measureByIntervals){
                if(measureByInterval.contains(measure.getInstant())){
                    measureByInterval.power.add(measure.getValueInWatt());
                    break;
                }
            }
        }

        // A coder : parcourir la liste des intervals et les transformer en `Measure`
        List<Measure> resultMeasures = new ArrayList<>();
        for(MeasureByInterval measureByInterval : measureByIntervals){
            resultMeasures.add(new Measure(measureByInterval.start,measureByInterval.avegare(),captor));
        }
        resultMeasures.sort((Measure m1, Measure m2)-> m1.getInstant().compareTo(m2.getInstant()));

        return resultMeasures;
    }

    private Set<MeasureByInterval> computeIntervals(Instant start, Instant end,
                                                    MeasureStep step) {
        Set<MeasureByInterval> measureByIntervals = new HashSet<>();
        Instant current = start;
        Instant endInstant =
                end.isBefore(start.plusSeconds(step.getDurationInSecondes())) ?
                        start.plusSeconds(step.getDurationInSecondes()) : end;
        while (current.isBefore(endInstant)) {
            measureByIntervals.add(new MeasureByInterval(current,current.plusSeconds(step.getDurationInSecondes())));
            current = current.plusSeconds(step.getDurationInSecondes());
        }
        return measureByIntervals;
    }


    class MeasureByInterval {
        private Instant start;
        private Instant end;
        private List<Integer> power = new ArrayList<>();

        public MeasureByInterval(Instant start, Instant end) {
            this.start = start;
            this.end = end;
        }
        public boolean contains(Instant instant) {
            return (instant.equals(start) || instant.isAfter(start)) &&
                    instant.isBefore(end);
        }
        public int avegare() {
            if (power.isEmpty()) {
                return 0;
            }
            return power
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.averagingInt(t -> t))
                    .intValue();
        }
    }

    @Override
    public Measure readAndSaveMeasure(Captor captor) {
        Measure newMeasure = new Measure();
        switch(captor.getPowerSource()){
            case FIXED:
                FixedCaptor fixedCaptor = (FixedCaptor)captor;
                newMeasure.setValueInWatt(fixedCaptor.getDefaultPowerInWatt().intValue());
                newMeasure.setCaptor(captor);
                newMeasure.setInstant(Instant.now());
                break;

            case SIMULATED:
                lastValue = new Integer(0);
                SimulatedCaptor simulatedCaptor = (SimulatedCaptor) captor;
                lastValue = (simulatedCaptor.getMinPowerInWatt().intValue()+simulatedCaptor.getMaxPowerInWatt().intValue())/2;

            case REAL:
                if(lastValue == null){
                    lastValue = new Integer(0);
                }
                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromHttpUrl("http://localhost:8090/measures/one")
                        .path("")
                        .queryParam("lastValue", lastValue)
                        .queryParam("variance", (lastValue==0)?1_000_000:(lastValue*10/100));
                System.out.println(builder.toUriString());
                Measure measure = this.restTemplate.getForObject(builder.toUriString(), Measure.class);
                newMeasure = measure;
                newMeasure.setCaptor(captor);
                lastValue = measure.getValueInWatt();
                break;
        }
        return newMeasure;
    }
}
