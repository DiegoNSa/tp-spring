package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.BigcorpApplication;
import com.training.spring.bigcorp.model.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@Service
//@Scope(scopeName = "prototype")
//@Lazy(value = true)
public class SiteServiceImpl implements SiteService {

    private final static Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);

    private CaptorService captorService;
    private ResourceLoader ressourceLoader;

    public SiteServiceImpl(){}

    @Autowired
    public SiteServiceImpl(CaptorService captorService, ResourceLoader ressourceLoader){
        logger.debug("Init SiteServiceImpl :" + this);
        this.captorService = captorService;
        this.ressourceLoader = ressourceLoader;
    }

    //@Monitored
    @Override
    public Site findById(String siteId) {
        logger.debug("Appel de findById :" + this);

        if (siteId == null) {
            return null;
        }

        Site site = new Site("Florange");
        site.setId(siteId);
        site.setCaptors(captorService.findBySite(siteId));
        return site;
    }

    @Override
    public void readFile(String path) {

        Resource resource = ressourceLoader.getResource(path);
        try (InputStream stream = resource.getInputStream()) {
            Scanner scanner = new Scanner(stream).useDelimiter("\\n");
            while (scanner.hasNext()) {
                System.out.println(scanner.next());
            }
        }
        catch (IOException e) {
            logger.error("Erreur sur chargement fichier", e);
        }
    }
}
