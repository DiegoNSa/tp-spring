package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.PowerSource;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.service.measure.MeasureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CaptorServiceImpl implements CaptorService{

    private final static Logger logger = LoggerFactory.getLogger(CaptorServiceImpl.class);


    private PowerSource powerSource;
    private MeasureService measureService;
    private CaptorDao captorDao;

    public CaptorServiceImpl(@Nullable PowerSource powerSource, MeasureService measureService, CaptorDao captorDao){
        this.powerSource = powerSource;
        this.measureService = measureService;
        this.captorDao = captorDao;
    }

    //@Monitored
    @Override
    public Set<Captor> findBySite(String siteId) {
        logger.debug("Appel de FindBySite :" + this);
        if (siteId == null) {
            return new HashSet<>();
        }
        System.out.println();
        return captorDao.findBySiteId(siteId).stream().collect(Collectors.toSet());
    }
}
