package org.example.scheduler;

import org.example.scheduler.jpa.SchedulerProviderModel;
import org.example.scheduler.service.impl.SchedulerServiceImpl;
import org.keycloak.models.KeycloakSession;
import org.keycloak.timer.TimerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RemoveInactiveUser implements SchedulerProvider {
    private KeycloakSession session;
    private SchedulerServiceImpl schedulerService;
    private static final Logger logger = LoggerFactory.getLogger(RemoveInactiveUser.class);

    public RemoveInactiveUser(KeycloakSession session) {
        this.session = session;
        this.schedulerService = new SchedulerServiceImpl(session);
    }

    @Override
    public void realmContext(String realmName) {
        // do nothing
    }

    @Override
    public void schedulerSettings(Map<String, Object> settings) {
        // do nothing
    }

    @Override
    public void run(KeycloakSession session, String realmName, long intervalMillis, String taskName) {
        logger.info("BasicScheduler: Test : {} - {}", System.currentTimeMillis(), realmName);
        TimerProvider timerProvider = session.getProvider(TimerProvider.class);
        timerProvider.scheduleTask(new RemoveInactiveUserTask(realmName), intervalMillis, taskName);
    }

    @Override
    public void cancelTask(String taskName) {
        TimerProvider timerProvider = session.getProvider(TimerProvider.class);
        timerProvider.cancelTask(taskName);
    }

    @Override
    public void restoreTasks() {
        List<SchedulerProviderModel> models = schedulerService.getSchedulerProviders();
        for (SchedulerProviderModel model : models) {
            if (model.isEnabled()) {
                logger.info("Scheduled tasks restored! for the realm {}", model.getRealmName());
                long intervalMillis = convertToMillis(model.getInterval(), model.getIntrvl_unit());
                run(session, model.getRealmName(), intervalMillis, model.getName());
            }
        }
    }

    private long convertToMillis(int interval, String intervalUnit) {
        switch (intervalUnit) {
            case "minutes":
                return TimeUnit.MINUTES.toMillis(interval);
            case "hours":
                return TimeUnit.HOURS.toMillis(interval);
            case "seconds":
            default:
                return TimeUnit.SECONDS.toMillis(interval);
        }
    }

    @Override
    public void close() {
//    do nothing
    }

    @Override
    public void run(KeycloakSession session) {
        // do nothing
    }
}
