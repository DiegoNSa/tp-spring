package com.training.spring.bigcorp.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("FIXED")
public class FixedCaptor extends Captor{

    @Column()
    private Long defaultPowerInWatt;

    @Deprecated
    public FixedCaptor(){
        super();
    }

    public FixedCaptor(String name,Site site){
        super(name,site);
    }

    public FixedCaptor(String name,Site site,Long defaultPowerInWatt){
        super(name,site);
        this.defaultPowerInWatt = defaultPowerInWatt;
    }

    public Long getDefaultPowerInWatt() {
        return defaultPowerInWatt;
    }

    public void setDefaultPowerInWatt(Long defaultPowerInWatt) {
        this.defaultPowerInWatt = defaultPowerInWatt;
    }
}
