package com.training.spring.bigcorp.model;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.Instant;

@Entity
public class Measure {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Past
    private Instant instant;

    @NotNull
    private Integer valueInWatt;

    @Version
    private int version;


    @OneToOne
    private Captor captor;

    public Measure(){

    }

    public Measure(Instant instant, Integer valueInWatt, Captor captor) {
        this.instant = instant;
        this.valueInWatt = valueInWatt;
        this.captor = captor;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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
