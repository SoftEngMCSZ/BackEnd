package me.whatdo.app.model.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.whatdo.app.model.entity.Choice;

import java.util.List;


public class AdminRequest {
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    List<Choice> choices;

    public AdminRequest() {

    }

    public AdminRequest(List<Choice> choices) {

        this.choices = choices;
    }

    public JsonObject toJsonObject() {
        return new Gson().fromJson(gson.toJson(choices), JsonObject.class);
    }

    public String toJson(){
        return gson.toJson(this);
    }
}
