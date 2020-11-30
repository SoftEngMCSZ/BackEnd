package me.whatdo.app.model.request;

public class CollaboratorRequest {

    private String username;

    private String password;

    private String choiceID;

    public CollaboratorRequest(){

    }

    public CollaboratorRequest(String ID, String name, String password){
        this.choiceID = ID;
        this.username = name;
        this.password = password;
    }

    public void setChoiceID(String choiceID) {
        this.choiceID = choiceID;
    }

    public String getChoiceID() {
        return choiceID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
