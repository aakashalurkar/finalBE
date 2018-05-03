package com.example.tensorflowtrial;

public class
Email {
    private long id;
    private String subject;
    private String body;
    private String author;
    private String bodyHTML;
    private String dateTime;
    private int urgency;

    public Email(){}

    public Email(String subject, String body, String author,String dateTime,String bodyHTML, int urgency){
        super();
        this.subject = subject;
        this.body = body;
        this.author = author;
        this.bodyHTML=bodyHTML;
        this.dateTime=dateTime;
        this.urgency = urgency;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody(){
        return body;
    }

    public void setBody(String body){
        this.body = body;
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBodyHTML(){return bodyHTML; }

    public void setBodyHTML(String bodyHTML){this.bodyHTML=bodyHTML; }

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime=dateTime;
    }

    public long getUrgency() {
        return urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return subject;
    }
}
