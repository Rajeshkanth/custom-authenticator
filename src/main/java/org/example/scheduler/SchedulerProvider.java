package org.example.scheduler;

import org.example.scheduler.jpa.SchedulerProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;
import org.keycloak.timer.ScheduledTask;

import java.util.Map;

public interface SchedulerProvider extends Provider, ScheduledTask {
    void realmContext(String realmName);
    void schedulerSettings(Map<String, Object> settings);
    void run(KeycloakSession session, String realmName, long intervalMillis, String taskName);
    void cancelTask(String taskName);
    void restoreTasks();
}
