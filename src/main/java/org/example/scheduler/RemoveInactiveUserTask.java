package org.example.scheduler;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.timer.ScheduledTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.example.authenticator.utils.Constants.LAST_LOGIN;
import static org.example.authenticator.utils.Constants.MAXIMUM_INACTIVE_DAYS;

public class RemoveInactiveUserTask implements ScheduledTask {
    private final String realm;
    private static final Logger logger = LoggerFactory.getLogger(RemoveInactiveUserTask.class);

    public RemoveInactiveUserTask(String realm) {
        this.realm = realm;
    }

    @Override
    public void run(KeycloakSession session) {
        logger.info("Inactive user clean-up scheduler running in the realm, {}", realm);
        LocalDate currentDate = LocalDate.now();
        Map<String, String> params = new HashMap<>();
        RealmModel realmModel = session.realms().getRealmByName(realm);
        session.users().searchForUserStream(realmModel, params).forEach(user -> {
            String lastLogin = user.getFirstAttribute(LAST_LOGIN);

            if (lastLogin != null) {
                LocalDate userInActiveDate = LocalDate.parse(lastLogin).plusDays(MAXIMUM_INACTIVE_DAYS);
                if (currentDate.isAfter(userInActiveDate)) {
                    try {
                        session.users().removeUser(realmModel, user);
                        logger.info("User {} is deleted because inactive since {}", user.getUsername(), user.getFirstAttribute(LAST_LOGIN));
                    } catch (Exception e) {
                        logger.error("Failed to delete user {}, {}", user.getUsername(), e.getMessage());
                    }
                }
            }
        });

    }
}
