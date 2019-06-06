package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.PowerSource;
import com.training.spring.bigcorp.service.measure.MeasureService;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CaptorServiceImpl implements CaptorService{

    private PowerSource powerSource;
    private MeasureService measureService;


    public CaptorServiceImpl(@Nullable PowerSource powerSource, MeasureService measureService){
        this.powerSource = powerSource;
        this.measureService = measureService;
    }

    //@Monitored
    @Override
    public Set<Captor> findBySite(String siteId) {
        Set<Captor> captors = new HashSet<>();
        if (siteId == null) {
            return captors;
        }
        captors.add(new Captor("Capteur A"));
        return captors;
    }
}
