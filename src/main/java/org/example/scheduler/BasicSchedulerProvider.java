package org.example.scheduler;

import jakarta.transaction.SystemException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.KeycloakTransactionManager;
import org.keycloak.timer.TimerProvider;
import org.keycloak.timer.TimerProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.ServiceLoader;

public class BasicSchedulerProvider implements SchedulerProvider {
    private String realmName;
    private Map<String, Object> settings;
    private static final Logger logger = LoggerFactory.getLogger(BasicSchedulerProvider.class);

    public BasicSchedulerProvider() {
    }

    @Override
    public void realmContext(String realmName) {
        this.realmName = realmName;
    }

    @Override
    public void schedulerSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    @Override
    public void run(KeycloakSession session) {
        logger.info("BasicScheduler: Test : {} - {}", System.currentTimeMillis(), realmName);

//            RemoveInactiveUserTask task = new RemoveInactiveUserTask();
//            task.run(session);
        KeycloakSessionFactory factory = session.getKeycloakSessionFactory();
        TimerProviderFactory timerProviderFactory = (TimerProviderFactory) factory.getProviderFactory(TimerProvider.class);
        TimerProvider timerProvider = timerProviderFactory.create(session);

        // Schedule the task
        timerProvider.scheduleTask(new RemoveInactiveUserTask(realmName), 1000);
    }

    @Override
    public void close() {
//    do nothing
    }
}
