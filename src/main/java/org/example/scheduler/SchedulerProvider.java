package org.example.scheduler;

import org.keycloak.provider.Provider;
import org.keycloak.timer.ScheduledTask;

import java.util.Map;

public interface SchedulerProvider extends Provider, ScheduledTask {
    void realmContext(String realmName);
    void schedulerSettings(Map<String, Object> settings);
}
