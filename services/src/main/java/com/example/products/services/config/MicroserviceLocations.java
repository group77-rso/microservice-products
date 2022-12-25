package com.example.products.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;



@ConfigBundle("microservice-locations")
@ApplicationScoped
public class MicroserviceLocations {

    @ConfigValue(watch = true)
    private String merchants;

    public String getMerchants() {
        return this.merchants;
    }

    public void setMerchants(final String merchants) {
        this.merchants = merchants;
    }
}
