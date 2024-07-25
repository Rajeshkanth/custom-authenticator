package org.example.scheduler.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.example.scheduler.RemoveInactiveUserTask;
import org.example.scheduler.SchedulerProvider;
import org.example.scheduler.jpa.SchedulerProviderModel;
import org.example.scheduler.jpa.SchedulerProviderRepresentation;
import org.example.scheduler.service.impl.SchedulerServiceImpl;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resources.KeycloakOpenAPI;
import org.keycloak.timer.TimerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.example.authenticator.utils.Constants.*;

public class SchedulerResourceProvider implements RealmResourceProvider {
    private final KeycloakSession session;
    private final RealmModel realm;
    private final SchedulerServiceImpl schedulerServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(SchedulerResourceProvider.class);

    public SchedulerResourceProvider(KeycloakSession session) {
        this.realm = session.getContext().getRealm();
        this.schedulerServiceImpl = new SchedulerServiceImpl(session);
        logger.info("Running in schedulerResourceProvider");
        this.session = session;
    }

    @Override
    public Object getResource() {
        return this;
    }

    @GET
    @Path("/registered-schedulers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public Response getSchedulers() {
        logger.info("registered-schedulers endpoint reached");

        String realmId = realm.getId(); // Assuming realm ID is needed to filter
        List<SchedulerProviderRepresentation> list = new LinkedList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:/opt/keycloak/data/h2/keycloakdb;AUTO_SERVER=TRUE", "sa", "password");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM SCHEDULER_PROVIDER WHERE REALM_ID = '" + realmId + "'")) {

            while (resultSet.next()) {
                SchedulerProviderRepresentation rep = new SchedulerProviderRepresentation();
                rep.setAlias(resultSet.getString("ALIAS"));
                rep.setName(resultSet.getString("NAME"));
                rep.setRealmId(resultSet.getString("REALM_ID"));
                rep.setProviderId(resultSet.getString("PROVIDER_ID"));
                rep.setInterval(resultSet.getInt("INTRVL"));
                rep.setIntervalUnit(resultSet.getString("INTRVL_UNIT"));
                rep.setEnabled(resultSet.getBoolean("ENABLED"));
                rep.setSettings(resultSet.getString("SETTINGS"));

                list.add(rep);
            }

        } catch (Exception e) {
            logger.error("Error fetching schedulers: ", e);
        }

        logger.info("Number of schedulers fetched: " + list.size());
        return Response.ok(list).build();

    }

    @GET
    @Path("/unregistered-schedulers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    @Tag(name = KeycloakOpenAPI.Admin.Tags.AUTHENTICATION_MANAGEMENT)
    public Stream<Map<String, Object>> getSchedulerProviders() {
        logger.info("unregistered endpoint reached, {}", buildProviderMetadata(session.getKeycloakSessionFactory().getProviderFactoriesStream(SchedulerProvider.class)));
        return buildProviderMetadata(session.getKeycloakSessionFactory().getProviderFactoriesStream(SchedulerProvider.class));
    }

    @Path("/register-scheduler")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    @Tag(name = "Scheduler Management")
    @Operation(summary = "Register and run a new scheduler task")
    @APIResponse(responseCode = "204", description = "No Content")
    public Response registerScheduler(@Parameter(description = "JSON containing 'providerId', 'name', and 'interval' attributes.") Map<String, String> data) {
        logger.info("register scheduler endpoint reached");
        String providerId = data.get("providerId");
        String name = data.get("name");
        String intervalStr = data.get("interval");
        String realmName = data.get("realm");
        String intervalUnit = data.get("intervalUnit"); // "seconds", "minutes", "hours"
        int interval;

        if (providerId == null || session.getKeycloakSessionFactory().getProviderFactory(SchedulerProvider.class, providerId) == null) {
            throw new BadRequestException("Scheduler task Provider with given providerId not found");
        }

        try {
            interval = Integer.parseInt(intervalStr);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid interval value");
        }

        long intervalMillis;
        switch (intervalUnit) {
            case "minutes":
                intervalMillis = TimeUnit.MINUTES.toMillis(interval);
                break;
            case "hours":
                intervalMillis = TimeUnit.HOURS.toMillis(interval);
                break;
            case "seconds":
            default:
                intervalMillis = TimeUnit.SECONDS.toMillis(interval);
                break;
        }

        SchedulerProviderModel schedulerProviderModel = new SchedulerProviderModel();
        schedulerProviderModel.setAlias(providerId);
        schedulerProviderModel.setName(name);
        schedulerProviderModel.setProviderId(providerId);
        schedulerProviderModel.setInterval(interval);
        schedulerProviderModel.setIntrvl_unit(intervalUnit);
        schedulerProviderModel.setEnabled(true);
        schedulerProviderModel.setRealmName(realmName);

        // Save the scheduler provider model
        schedulerProviderModel = schedulerServiceImpl.addSchedulerProvider(schedulerProviderModel);
        data.put("id", schedulerProviderModel.getId());
        SchedulerProvider provider = session.getProvider(SchedulerProvider.class, providerId);
        if (provider == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Provider not found").build();
        }

        provider.run(session, realmName, intervalMillis, name);

        logger.info("Scheduler task {} registered and scheduled successfully", providerId);
        return Response.status(Response.Status.OK).entity("Provider found and task registered").build();
    }

    @POST
    @Path("/unregister-scheduler")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public Response unregisterScheduler(@PathParam("realm") String realm, SchedulerProviderModel request) {
        logger.info("unregister scheduler endpoint reached");
        logger.info("request {}", request);
        String providerId = request.getProviderId();
        String realmName = request.getRealmName();
        String taskName = request.getName();

        try {
            SchedulerProvider provider = session.getProvider(SchedulerProvider.class, providerId);
            provider.cancelTask(taskName);

            // Remove the scheduler provider from the database
            SchedulerProviderModel model = schedulerServiceImpl.getSchedulerProviderByAliasAndRealm(providerId, realmName);
            if (model != null) {
                schedulerServiceImpl.removeSchedulerProvider(model, realmName);
                return Response.ok().entity("{\"status\": \"success\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Scheduler task not found\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        } catch (Exception e) {
            logger.info("Failed to stop the scheduled task", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    public Stream<Map<String, Object>> buildProviderMetadata(Stream<ProviderFactory> factories) {
        return factories.map(factory -> {
            Map<String, Object> data = new HashMap<>();
            buildProviderMetadataHelper(data, factory);
            return data;
        });
    }

    private void buildProviderMetadataHelper(Map<String, Object> data, ProviderFactory factory) {
        data.put("providerId", factory.getId());
        logger.info("Factory instance is {}", factory.getClass().getName());
        if (factory instanceof ConfigurableAuthenticatorFactory) {
            ConfigurableAuthenticatorFactory configured = (ConfigurableAuthenticatorFactory) factory;
            data.put(DESCRIPTION, configured.getHelpText());
            data.put(DISPLAY_NAME, configured.getDisplayType());
        } else {
            data.put(DESCRIPTION, "N/A");
            data.put(DISPLAY_NAME, "N/A");
        }
        logger.info("Provider metadata: {}", data);
    }

    private SchedulerProviderRepresentation toRepresentation(SchedulerProviderModel model) {
        SchedulerProviderRepresentation representation = new SchedulerProviderRepresentation();
        representation.setAlias(model.getAlias());
        representation.setProviderId(model.getProviderId());
        representation.setName(model.getName());
        representation.setInterval(model.getInterval());
        representation.setSettings(model.getSettings());
        return representation;
    }


    @Override
    public void close() {
//      do nothing
    }
}
