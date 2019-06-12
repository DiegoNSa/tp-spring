package com.training.spring.bigcorp.service.measure;

import com.training.spring.bigcorp.config.properties.BigCorpApplicationProperties;
import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.MeasureStep;
import com.training.spring.bigcorp.model.RealCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class RealMeasureService implements MeasureService<RealCaptor> {

    private Integer defaultValue;

    private BigCorpApplicationProperties bigCorpApplicationProperties;

    @Autowired
    public RealMeasureService(BigCorpApplicationProperties bigCorpApplicationProperties){
        this.bigCorpApplicationProperties = bigCorpApplicationProperties;
    }


    public List<Measure> readMeasures(RealCaptor captor, Instant start, Instant end,
                                      MeasureStep step) {
        defaultValue = bigCorpApplicationProperties.getMeasure().getDefaultReal();
        checkReadMeasuresAgrs(captor, start, end, step);
        List<Measure> measures = new ArrayList<>();
        Instant current = start;
        while(current.isBefore(end)){
            measures.add(new Measure(current, defaultValue, captor));
            current = current.plusSeconds(step.getDurationInSecondes());
        }
        return measures;
    }
}
