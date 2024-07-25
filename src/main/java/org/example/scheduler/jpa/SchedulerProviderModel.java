package org.example.scheduler.jpa;

import java.io.Serializable;

public class SchedulerProviderModel implements Serializable, Cloneable {
    private String id;
    private String alias;
    private String name;
    private String providerId;
    private int interval;
    private String intrvl_unit;
    private String realmName;
    private boolean enabled;
    protected String settings;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getIntrvl_unit() {
        return intrvl_unit;
    }

    public void setIntrvl_unit(String intrvl_unit) {
        this.intrvl_unit = intrvl_unit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Used for display purposes.  Probably should clean this code up and make alias and name the same, but
     * the old code references an Enum and the admin console creates a "friendly" name for each enum.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "SchedulerProviderModel{" +
                "id='" + id + '\'' +
                ", alias='" + alias + '\'' +
                ", name='" + name + '\'' +
                ", providerId='" + providerId + '\'' +
                ", interval=" + interval +
                ", realmName='" + realmName + '\'' +
                ", settings='" + settings + '\'' +
                ", enabled='" + enabled + '\'' +
                '}';
    }

    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }
}
