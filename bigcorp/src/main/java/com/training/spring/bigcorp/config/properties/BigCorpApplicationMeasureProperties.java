package com.training.spring.bigcorp.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
//@ConfigurationProperties(prefix = "bigcorp")
public class BigCorpApplicationMeasureProperties{
    private Integer defaultFixed;
    private Integer defaultSimulated;
    private Integer defaultReal;

    public Integer getDefaultFixed() {
        return defaultFixed;
    }

    public Integer getDefaultSimulated() {
        return defaultSimulated;
    }

    public Integer getDefaultReal() {
        return defaultReal;
    }


    public BigCorpApplicationMeasureProperties() {
        /*this.defaultFixed = environment.getRequiredProperty("bigcorp.measure.default-fixed",Integer.class);
        this.defaultSimulated = environment.getRequiredProperty("bigcorp.measure.default-simulated",Integer.class);
        this.defaultReal = environment.getRequiredProperty("bigcorp.measure.default-real",Integer.class);*/
    }

    public void setDefaultFixed(Integer defaultFixed) {
        this.defaultFixed = defaultFixed;
    }

    public void setDefaultSimulated(Integer defaultSimulated) {
        this.defaultSimulated = defaultSimulated;
    }

    public void setDefaultReal(Integer defaultReal) {
        this.defaultReal = defaultReal;
    }
}
