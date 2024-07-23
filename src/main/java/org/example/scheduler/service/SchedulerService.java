package org.example.scheduler.service;

import org.example.scheduler.jpa.SchedulerProviderModel;
import org.keycloak.provider.Provider;

import java.util.List;

public interface SchedulerService extends Provider {

    SchedulerProviderModel addSchedulerProvider(SchedulerProviderModel model);

    void updateSchedulerProvider(SchedulerProviderModel model);

    List<SchedulerProviderModel> getSchedulerProviders();

    SchedulerProviderModel getSchedulerProviderByAliasAndRealm(String alias,String Realm);
    void removeSchedulerProvider(SchedulerProviderModel model,String realmName);

}