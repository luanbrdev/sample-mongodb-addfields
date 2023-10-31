package br.com.luanbrdev;

import java.time.LocalDateTime;
import java.util.Objects;

public class ClientReportConfig {
    private String id;
    private String name;
    private LocalDateTime lastExecution;
    private int waitingTimeInMinutes;
    private boolean active;

    public ClientReportConfig() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastExecution() {
        return lastExecution;
    }

    public void setLastExecution(LocalDateTime lastExecution) {
        this.lastExecution = lastExecution;
    }

    public int getWaitingTimeInMinutes() {
        return waitingTimeInMinutes;
    }

    public void setWaitingTimeInMinutes(int waitingTimeInMinutes) {
        this.waitingTimeInMinutes = waitingTimeInMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientReportConfig that = (ClientReportConfig) o;
        return waitingTimeInMinutes == that.waitingTimeInMinutes && active == that.active && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(lastExecution, that.lastExecution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastExecution, waitingTimeInMinutes, active);
    }
}
