package org.example.scheduler;

import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderFactory;

import java.util.List;

public interface SchedulerProviderFactory extends ProviderFactory<SchedulerProvider> {
    String getHelpText();
}
