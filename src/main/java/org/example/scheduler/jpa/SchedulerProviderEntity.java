package org.example.scheduler.jpa;


import jakarta.persistence.*;
import org.keycloak.models.jpa.entities.RealmEntity;

@Table(name="SCHEDULER_PROVIDER")
@Entity
@NamedQueries({
        @NamedQuery(name="deleteSchedulerProviderByRealm", query="delete from SchedulerProviderEntity where realm = :realm and providerId = :providerId"),
        @NamedQuery(name="getAllSchedulerProviders", query="from SchedulerProviderEntity")})
public class SchedulerProviderEntity {
    @Id
    @Column(name = "ID", length = 36)
    @Access(AccessType.PROPERTY)
    protected String id;

    @Column(name = "ALIAS")
    protected String alias;

    @Column(name = "NAME")
    protected String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REALM_ID")
    protected RealmEntity realm;

    @Column(name = "PROVIDER_ID")
    protected String providerId;

    @Column(name = "INTRVL")
    protected int interval;

    @Column(name = "INTRVL_UNIT")
    protected String intrvlUnit;

    @Column(name = "ENABLED")
    protected boolean enabled;

    @Column(name = "SETTINGS")
    protected String settings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntrvlUnit() {
        return intrvlUnit;
    }

    public void setIntrvlUnit(String intrvlUnit) {
        this.intrvlUnit = intrvlUnit;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public RealmEntity getRealm() {
        return realm;
    }

    public void setRealm(RealmEntity realm) {
        this.realm = realm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof SchedulerProviderEntity)) return false;

        SchedulerProviderEntity that = (SchedulerProviderEntity) o;

        if (!id.equals(that.getId())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}