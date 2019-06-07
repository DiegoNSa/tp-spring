package com.training.spring.bigcorp.model;
import java.time.Instant;

public class Measure {
    private Instant instant;
    private Integer valueInWatt;
    private Captor captor;
    private Long id;

    public Measure(Instant instant, Integer valueInWatt, Captor captor) {
        this.instant = instant;
        this.valueInWatt = valueInWatt;
        this.captor = captor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public Integer getValueInWatt() {
        return valueInWatt;
    }

    public void setValueInWatt(Integer valueInWatt) {
        this.valueInWatt = valueInWatt;
    }

    public Captor getCaptor() {
        return captor;
    }

    public void setCaptor(Captor captor) {
        this.captor = captor;
    }

    @Override
    public String toString() {
        return "instant : " + instant + ", Watt : " + valueInWatt + ", Captor" + captor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if(!(obj instanceof Measure)){
            return false;
        }
        Measure measObj = (Measure)obj;
        return measObj.valueInWatt.equals(valueInWatt) && measObj.instant.equals(instant) && measObj.captor.equals(captor);
    }

    @Override
    public int hashCode() {
        int result = 12;
        result = result * 7 + instant.hashCode();
        result = result * 7 + valueInWatt.hashCode();
        result = result * 7 + captor.hashCode();
        return result;
    }
}
