package com.vaadin.model;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeFilter {

    String name = "";

    Set<String> trackingStatus = new HashSet<>();

    Set<String> teams = new HashSet<>();

    Set<String> accountsConnectedNames = new HashSet<>();

    public EmployeeFilter() {
    }

    public EmployeeFilter(String name, Set<String> trackingStatus, Set<String> teams, Set<String> accountsConnectedNames) {
        this.name = name;
        this.trackingStatus = trackingStatus;
        this.teams = teams;
        this.accountsConnectedNames = accountsConnectedNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getTrackingStatus() {
        return trackingStatus;
    }

    public void setTrackingStatus(Set<String> trackingStatus) {
        this.trackingStatus = trackingStatus;
    }

    public Set<String> getTeams() {
        return teams;
    }

    public void setTeams(Set<String> teams) {
        this.teams = teams;
    }

    public Set<String> getAccountsConnectedNames() {
        return accountsConnectedNames;
    }

    public void setAccountsConnectedNames(Set<String> accountsConnectedNames) {
        this.accountsConnectedNames = accountsConnectedNames;
    }

    public List<Employee> apply(List<Employee> empleados) {
        return empleados.stream().filter(e -> {
            return
                    // Nombre
                    ((name == null || name.isBlank() || e.getIdentity().getFullName().toLowerCase().contains(name.toLowerCase())) &&
                    // Teams
                    (teams == null || teams.isEmpty() || teams.stream().anyMatch(t ->
                            e.getTeams().stream().map(Employee.Team::getName).
                                    collect(Collectors.toList()).contains(t))) &&
                    // Tracking status
                    (trackingStatus == null || trackingStatus.isEmpty() || trackingStatus.stream().anyMatch(
                            t -> (e.isActive() && t.contains("Active")) || (!e.isActive() && t.contains("Inactive")))) &&
                    // Connected accounts
                    (accountsConnectedNames == null || accountsConnectedNames.isEmpty() || accountsConnectedNames.stream().anyMatch(
                            ac -> e.getIdentity().getConnectedAccounts().stream().map(Employee.ConnectedAccounts::getSource).
                                    collect(Collectors.toList()).contains(ac))));
                        })
                .collect(Collectors.toList());
    }
}