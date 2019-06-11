package com.training.spring.bigcorp.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("SIMULATED")
public class SimulatedCaptor  extends Captor{

    @Column()
    private Long minPowerInWatt;

    @Column()
    private Long maxPowerInWatt;


    @Deprecated
    public SimulatedCaptor(){
        super();
    }

    public SimulatedCaptor(String name,Site site){
        super(name,site);
    }

    public SimulatedCaptor(String name,Site site,Long minPowerInWatt,Long maxPowerInWatt){
        super(name,site);
        this.minPowerInWatt = minPowerInWatt;
        this.maxPowerInWatt = maxPowerInWatt;
    }

    public Long getMinPowerInWatt() {
        return minPowerInWatt;
    }

    public void setMinPowerInWatt(Long minPowerInWatt) {
        this.minPowerInWatt = minPowerInWatt;
    }

    public Long getMaxPowerInWatt() {
        return maxPowerInWatt;
    }

    public void setMaxPowerInWatt(Long maxPowerInWatt) {
        this.maxPowerInWatt = maxPowerInWatt;
    }
}
