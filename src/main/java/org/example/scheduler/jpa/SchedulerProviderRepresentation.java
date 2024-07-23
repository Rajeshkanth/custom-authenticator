package org.example.scheduler.jpa;

public class SchedulerProviderRepresentation {

    //    private String id;
//    private String displayText;
//
//    // Getters and setters
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getDisplayText() {
//        return displayText;
//    }
//
//    public void setDisplayText(String displayText) {
//        this.displayText = displayText;
//    }
    private String alias;
    private String name;
    private String providerId;
    private int interval;
    private String realmName;
    protected long createTime;
    protected long lastRunTime;
    protected long nextRunTime;
    private String settings;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(long lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public long getNextRunTime() {
        return nextRunTime;
    }

    public void setNextRunTime(long nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

}
