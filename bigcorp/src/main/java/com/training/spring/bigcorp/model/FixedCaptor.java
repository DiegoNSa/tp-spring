package com.training.spring.bigcorp.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("FIXED")
public class FixedCaptor extends Captor{

    @NotNull
    private Long defaultPowerInWatt;

    @Deprecated
    public FixedCaptor(){
        super();
    }

    public FixedCaptor(String name,Site site){
        super(name,site,PowerSource.FIXED);
    }

    public FixedCaptor(String name,Site site,Long defaultPowerInWatt){
        super(name,site,PowerSource.FIXED);
        this.defaultPowerInWatt = defaultPowerInWatt;
    }

    public Long getDefaultPowerInWatt() {
        return defaultPowerInWatt;
    }

    public void setDefaultPowerInWatt(Long defaultPowerInWatt) {
        this.defaultPowerInWatt = defaultPowerInWatt;
    }
}
