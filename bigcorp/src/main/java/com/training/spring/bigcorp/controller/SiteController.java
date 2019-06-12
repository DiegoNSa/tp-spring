package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.repository.MeasureDao;
import com.training.spring.bigcorp.repository.SiteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.lang.reflect.Member;

@Controller
@Transactional
@RequestMapping(path = "/sites", method = RequestMethod.GET)
public class SiteController {

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private CaptorDao captorDao;

    @Autowired
    private MeasureDao measureDao;


    @GetMapping()
    public ModelAndView handle(Model model){

        System.out.println(">>>>>>>>>>>>>>>>>>>>");
        System.out.println("Returning to mian");


        ModelAndView mv = new ModelAndView("sites");

        mv.addObject("sites" ,siteDao.findAll());
        return mv;

    }

    @GetMapping("/{id}")
    public ModelAndView findById(@PathVariable String id) {
        return new ModelAndView("site")
                .addObject("site",
                        siteDao.findById(id).orElseThrow(IllegalArgumentException::new));
    }

    @GetMapping("/create")
    public ModelAndView create(Model model){
        return new ModelAndView("site").addObject("site", new Site());
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView save(Site site) {
        if (site.getId() == null) {
            return new ModelAndView("site").addObject("site", siteDao.save(site));
        } else {
            Site siteToPersist =
                    siteDao.findById(site.getId()).orElseThrow(IllegalArgumentException::new);
// L'utilisateur ne peut changer que le nom du site sur l'écran
            siteToPersist.setName(site.getName());
            return new ModelAndView("sites").addObject("sites", siteDao.findAll());
        }
    }

    @PostMapping(path = "/{id}/delete")
    public ModelAndView delete(@PathVariable String id){
        Site site = siteDao.findById(id).orElseThrow(IllegalArgumentException::new);
        site.getCaptors().forEach(c -> measureDao.deleteByCaptorId(c.getId()));
        captorDao.deleteBySiteId(id);
        siteDao.deleteById(id);
        return new ModelAndView("sites").addObject("sites", siteDao.findAll());
    }
}
