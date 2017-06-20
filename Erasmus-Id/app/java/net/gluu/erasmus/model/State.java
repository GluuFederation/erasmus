package net.gluu.erasmus.model;

import org.json.JSONArray;

/**
 * Created by Meghna Joshi on 16/5/17.
 */

public class State {
    private String name;
    private JSONArray cities;

    public State(String name, JSONArray cities) {
        this.name = name;
        this.cities = cities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONArray getCities() {
        return cities;
    }

    public void setCities(JSONArray cities) {
        this.cities = cities;
    }
}
