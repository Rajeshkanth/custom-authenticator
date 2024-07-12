package org.example.scheduler;

import org.keycloak.models.KeycloakSession;
import org.keycloak.timer.ScheduledTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.example.authenticator.utils.Constants.LAST_LOGIN;
import static org.example.authenticator.utils.Constants.MAXIMUM_INACTIVE_DAYS;

public class RemoveInactiveUserTask implements ScheduledTask {
    private static final Logger logger = LoggerFactory.getLogger(RemoveInactiveUserTask.class);

    @Override
    public void run(KeycloakSession session) {
        logger.info("Inactive user clean-up scheduler running");
        LocalDate currentDate = LocalDate.now();
        Map<String, String> params = new HashMap<>();
        session.realms().getRealmsStream().forEach(realm -> {
            logger.info("realm model, {}", realm.getName());

            session.users().searchForUserStream(realm, params).forEach(user -> {
                String lastLogin = user.getFirstAttribute(LAST_LOGIN);

                if (lastLogin != null) {
                    LocalDate userInActiveDate = LocalDate.parse(lastLogin).plusDays(MAXIMUM_INACTIVE_DAYS);
                    if (currentDate.isAfter(userInActiveDate)) {
                        try {
                            session.users().removeUser(realm, user);
                            logger.info("User {} is deleted because inactive since {}", user.getUsername(), user.getFirstAttribute(LAST_LOGIN));
                        } catch (Exception e) {
                            logger.error("Failed to delete user {}, {}", user.getUsername(), e.getMessage());
                        }
                    }
                }
            });
        });
    }
}
