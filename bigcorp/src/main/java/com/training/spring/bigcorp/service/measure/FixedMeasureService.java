package com.training.spring.bigcorp.service.measure;

import com.training.spring.bigcorp.config.properties.BigCorpApplicationProperties;
import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.MeasureStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Primary
public class FixedMeasureService implements MeasureService{

    private Integer defaultValue;

    private BigCorpApplicationProperties bigCorpApplicationProperties;

    @Autowired
    public FixedMeasureService(BigCorpApplicationProperties bigCorpApplicationProperties){
        this.bigCorpApplicationProperties = bigCorpApplicationProperties;
    }

    public List<Measure> readMeasures(Captor captor, Instant start, Instant end,
                                      MeasureStep step) {
        System.out.println("========================================================================");
        System.out.println(bigCorpApplicationProperties.getMeasure());
        defaultValue = bigCorpApplicationProperties.getMeasure().getDefaultFixed();
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
