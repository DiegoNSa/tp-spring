package com.training.spring.bigcorp.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@ConfigurationProperties(prefix = "bigcorp")
public class BigCorpApplicationProperties{
    private String name;
    private Integer version;
    private Set<String> emails;
    private String webSiteUrl;

    public String getName() {
        return name;
    }

    public Integer getVersion() {
        return version;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public String getWebSiteUrl() {
        return webSiteUrl;
    }

    @NestedConfigurationProperty
    private BigCorpApplicationMeasureProperties measure;

    public BigCorpApplicationProperties() {
        /*this.name = environment.getRequiredProperty("bigcorp.name");
        this.version = environment.getRequiredProperty("bigcorp.version",Integer.class);
        this.emails = (Set<String>) environment.getRequiredProperty("bigcorp.emails",Set.class);
        this.webSiteUrl = environment.getRequiredProperty("bigcorp.webSiteUrl");*/
        this.measure = new BigCorpApplicationMeasureProperties();
    }

    public BigCorpApplicationMeasureProperties getMeasure() {
        return measure;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    public void setWebSiteUrl(String webSiteUrl) {
        this.webSiteUrl = webSiteUrl;
    }

    public void setMeasure(BigCorpApplicationMeasureProperties measure) {
        this.measure = measure;
    }
}
