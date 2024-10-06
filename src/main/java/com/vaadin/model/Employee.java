package com.vaadin.model;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    public Employee() {
    }

    public Employee(String id, String uid, Boolean inactive, Identity identity, List<Team> teams) {
        this.id = id;
        this.uid = uid;
        this.inactive = inactive;
        this.identity = identity;
        this.teams = teams;
    }

    public String id;
    public String uid;
    public Boolean inactive;
    public Identity identity;
    public List<Team> teams;

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public Object getInactive() {
        return inactive;
    }

    public Identity getIdentity() {
        return identity;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public boolean isActive() {
        return inactive == null || !inactive;
    }

    public static class Identity{

        public Identity(String id, String fullName, String primaryEmail, String photoUrl, ArrayList<ConnectedAccounts> connectedAccounts, ArrayList<Object> imsUsers, ArrayList<CalUser> calUsers) {
            this.id = id;
            this.fullName = fullName;
            this.primaryEmail = primaryEmail;
            this.photoUrl = photoUrl;
            this.connectedAccounts = connectedAccounts;
            this.imsUsers = imsUsers;
            this.calUsers = calUsers;
        }

        public String id;
        public String fullName;
        public String primaryEmail;
        public String photoUrl;
        public ArrayList<ConnectedAccounts> connectedAccounts;
        public ArrayList<Object> imsUsers;
        public ArrayList<CalUser> calUsers;

        public String getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public String getPrimaryEmail() {
            return primaryEmail;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public ArrayList<ConnectedAccounts> getConnectedAccounts() {
            return connectedAccounts;
        }

        public ArrayList<Object> getImsUsers() {
            return imsUsers;
        }

        public ArrayList<CalUser> getCalUsers() {
            return calUsers;
        }
    }

    public static class Team {

        public static final String DEFAULT_TEAM_COLOR = "#E9560D";

        public String id;
        public String uid;
        public String name;
        public String color;

        public Team(String id, String uid, String name, String color) {
            this.id = id;
            this.uid = uid;
            this.name = name;
            this.color = color;
        }

        public String getId() {
            return id;
        }

        public String getUid() {
            return uid;
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }
    }

    public static class ConnectedAccounts {
        public String id;
        public String source;

        public ConnectedAccounts(String id, String source) {
            this.id = id;
            this.source = source;
        }

        public String getId() {
            return id;
        }

        public String getSource() {
            return source;
        }
    }

    public static class CalUser{
        public CalUser calUser;
        public Identity identity;

        public CalUser getCalUser() {
            return calUser;
        }

        public Identity getIdentity() {
            return identity;
        }
    }
}
