package com.jakub.example;

import java.util.HashMap;
import java.util.Map;

public class User {
    private int kills;
    private int deaths;

    private static final Map<String, User> USERS = new HashMap<>();

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void increaseKills() {
        kills++;
    }

    public void increaseDeaths() {
        deaths++;
    }

    public static User get(String nickname) {
        return USERS.get(nickname);
    }
}
