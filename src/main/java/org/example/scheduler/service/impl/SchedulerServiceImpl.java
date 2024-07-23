package org.example.scheduler.service.impl;

import jakarta.persistence.EntityManager;
import org.example.scheduler.jpa.SchedulerProviderEntity;
import org.example.scheduler.jpa.SchedulerProviderModel;
import org.example.scheduler.service.SchedulerService;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.jpa.entities.RealmEntity;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SchedulerServiceImpl implements SchedulerService {

    protected KeycloakSession session;
    protected SchedulerProviderEntity entity;
    private EntityManager em;
    public static final Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    public SchedulerServiceImpl(KeycloakSession session) {
        logger.info("Running in SchedulerServiceImpl");
        this.session=session;
        em=session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    @Override
    public void close() {
//      do nothing
    }
    public RealmEntity getRealmEntity(KeycloakSession session, String realmName) {
        return em.find(RealmEntity.class,
                session.realms().getRealmByName(realmName).getId());
    }

    @Override
    public SchedulerProviderModel addSchedulerProvider(SchedulerProviderModel model) {
        logger.info("add scheduler provider method running in SchedulerServiceImpl");
        SchedulerProviderEntity auth = new SchedulerProviderEntity();
        String id = (model.getId() == null) ? KeycloakModelUtils.generateId(): model.getId();
        auth.setId(id);
        auth.setAlias(model.getAlias());
        auth.setName(model.getName());
        auth.setRealm(getRealmEntity(session, model.getRealmName()));
        auth.setProviderId(model.getProviderId());
        auth.setInterval(model.getInterval());
        auth.setCreateTime(model.getCreateTime());
        auth.setLastRunTime(model.getLastRunTime());
        auth.setNextRunTime(model.getNextRunTime());
        auth.setSettings(model.getSettings());
        getSchedulerProvidersWithEntity().add(auth);
        em.persist(auth);
        em.flush();
        model.setId(auth.getId());
        return model;
    }

    @Override
    public void updateSchedulerProvider(SchedulerProviderModel model) {
        SchedulerProviderEntity schedulerProviderEntity = em.find(SchedulerProviderEntity.class, model.getId());
        if (schedulerProviderEntity == null) return;
        entity.setAlias(model.getAlias());
        entity.setProviderId(model.getProviderId());
        entity.setName(model.getName());
        entity.setInterval(model.getInterval());
        entity.setLastRunTime(model.getLastRunTime());
        entity.setNextRunTime(model.getNextRunTime());
        em.flush();
    }

    @Override
    public List<SchedulerProviderModel> getSchedulerProviders() {
        Collection<SchedulerProviderEntity> entities = getSchedulerProvidersWithEntity();
        if (entities.isEmpty()) return Collections.emptyList();
        List<SchedulerProviderModel> actions = new LinkedList<>();
        for (SchedulerProviderEntity e : entities) {
            actions.add(entityToModel(e));
        }
        return Collections.unmodifiableList(actions);
    }

    public List<SchedulerProviderEntity> getSchedulerProvidersWithEntity() {
        return em.createNamedQuery("getAllSchedulerProviders", SchedulerProviderEntity.class)
                .getResultList();
    }

    @Override
    public void removeSchedulerProvider(SchedulerProviderModel model,String realm) {
        em.createNamedQuery("deleteSchedulerProviderByRealm")
                .setParameter("providerId", model.getProviderId())
                .setParameter("realm",getRealmEntity(session,realm))
                .executeUpdate();
        em.flush();
    }

    @Override
    public SchedulerProviderModel getSchedulerProviderByAliasAndRealm(String alias,String realm) {
        for (SchedulerProviderModel action : getSchedulerProviders()) {
            if (action.getAlias().equals(alias)&& action.getRealmName().equals(realm)) return action;
        }
        return null;
    }

    public SchedulerProviderModel entityToModel(SchedulerProviderEntity entity) {
        SchedulerProviderModel model = new SchedulerProviderModel();
        model.setId(entity.getId());
        model.setProviderId(entity.getProviderId());
        model.setAlias(entity.getAlias());
        model.setName(entity.getName());
        model.setInterval(entity.getInterval());
        model.setRealmName(entity.getRealm().getName());
        model.setCreateTime(entity.getCreateTime());
        model.setLastRunTime(entity.getLastRunTime());
        model.setNextRunTime(entity.getNextRunTime());
        model.setSettings(entity.getSettings());
        return model;
    }


}
