package org.example.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.keycloak.crypto.AsymmetricSignatureSignerContext;
import org.keycloak.crypto.KeyUse;
import org.keycloak.crypto.KeyWrapper;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.jose.jws.JWSBuilder;
import org.keycloak.models.*;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.Urls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import static org.example.authenticator.utils.Constants.*;


public class CustomEventListener implements EventListenerProvider {
    private static final Logger logger = LoggerFactory.getLogger(CustomEventListener.class);
    public final KeycloakSession session;

    public CustomEventListener(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        logger.info("In onEvent from Custom Event Listener.");
        logger.info("Event type {}", event.getType());
        logger.info("on event {}", toString(event));

        String mobileNumber = event.getDetails().get(USERNAME);
        String password = event.getDetails().get(PASSWORD);
        KeycloakContext keycloakContext = session.getContext();
        RealmModel realm = session.getContext().getRealm();
        UserModel user = session.users().getUserById(realm, event.getUserId());
        String token = getAccessToken(keycloakContext, realm, user);

        if (event.getType() == EventType.REGISTER) {
            if (token != null) {
                sendUserRegistration(mobileNumber, password, token);
            } else {
                logger.info("Token generation failed, {},{},{}", keycloakContext, realm, user);
            }
        } else {
            logger.info("Event type not handled {}", event.getType());
        }

    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        logger.info("In admin onEvent from Custom Event Listener.");
        logger.info("Event type {}", adminEvent.getAuthDetails().getRealmId());
    }

    public String getAccessToken(KeycloakContext keycloakContext, RealmModel realm, UserModel user) {
        AccessToken token = new AccessToken();
        token.subject(user.getId());
        token.issuer(Urls.realmIssuer(keycloakContext.getUri().getBaseUri(), realm.getName()));
        token.issuedNow();
        token.expiration((int) (token.getIat() + 60L));

        KeyWrapper key = session.keys().getActiveKey(realm, KeyUse.SIG, ALGORITHM);
        return new JWSBuilder().kid(key.getKid()).type(JWT).jsonContent(token).sign(new AsymmetricSignatureSignerContext(key));
    }

    public void sendUserRegistration(String mobileNumber, String password, String token) {

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            logger.info("Calling external api to store the user in backend.");
            // Sending post req to save the user in backend db
            HttpPost httpPost = new HttpPost(System.getenv(SIGNUP_USER_API) + SIGN_UP_PATH);

            Map<String, String> userMap = new HashMap<>();
            userMap.put(MOBILE_NUMBER, mobileNumber);
            userMap.put(PASSWORD, password);

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(userMap);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader(ACCEPT, HEADER_VALUE);
            httpPost.setHeader(CONTENT_TYPE, HEADER_VALUE);
            httpPost.setHeader(AUTHORIZATION, BEARER + token);

            HttpResponse response = client.execute(httpPost);
            logger.info("Response status: {}", response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            logger.error("Failed to send user registration request", e);
        }
    }

    private String toString(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("type=");
        sb.append(event.getType());
        sb.append(", realmId=");
        sb.append(event.getRealmId());
        sb.append(", clientId=");
        sb.append(event.getClientId());
        sb.append(", userId=");
        sb.append(event.getUserId());
        sb.append(", ipAddress=");
        sb.append(event.getIpAddress());
        if (event.getError() != null) {
            sb.append(", error=");
            sb.append(event.getError());
        }

        if (event.getDetails() != null) {
            for (Map.Entry<String, String> e : event.getDetails().entrySet()) {
                sb.append(", ");
                sb.append(e.getKey());
                if (e.getValue() == null || e.getValue().indexOf(' ') == -1) {
                    sb.append("=");
                    sb.append(e.getValue());
                } else {
                    sb.append("='");
                    sb.append(e.getValue());
                    sb.append("'");
                }
            }
        }

        return sb.toString();
    }

    @Override
    public void close() {
//      not needed
    }
}
