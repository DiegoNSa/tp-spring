package com.training.spring.bigcorp.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("REAL")
public class RealCaptor extends Captor {

    @Deprecated
    public RealCaptor() {
        super();
// used only by serializer and deserializer
    }
    public RealCaptor(String name, Site site) {
        super(name, site,PowerSource.REAL);
    }
}