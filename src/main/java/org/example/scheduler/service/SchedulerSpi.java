package org.example.scheduler.service;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

public class SchedulerSpi implements Spi {
    public static final String NAME = "schedulerSpi";

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Class<? extends Provider> getProviderClass() {
        return SchedulerService.class;
    }

    @Override
    public Class<? extends ProviderFactory> getProviderFactoryClass() {
        return SchedulerServiceProviderFactory.class;
    }
}
