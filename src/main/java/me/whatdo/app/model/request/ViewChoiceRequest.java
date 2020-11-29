package me.whatdo.app.model.request;

public class ViewChoiceRequest {

    private String choiceID;
    private String authentication;

    public ViewChoiceRequest(){

    }

    public ViewChoiceRequest(String id, String auth){
        this.choiceID = id;
        this.authentication = auth;
    }

    public void setChoiceID(String choiceID) {
        this.choiceID = choiceID;
    }

    public String getID() {
        return choiceID;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getAuthentication() {
        return authentication;
    }
}
