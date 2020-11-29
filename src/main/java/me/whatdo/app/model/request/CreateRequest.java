package me.whatdo.app.model.request;

public class CreateRequest {

    private String body;

    public CreateRequest(){

    }

    public CreateRequest(String body){
        this.body = body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
