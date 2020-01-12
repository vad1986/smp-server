package com.vertex.dataObjects;

public class MailFormat {
    private String subject;

    private String content;

    private String to;

    private String cc;

    public MailFormat(String subject, String content, String to, String cc) {
        this.subject = subject;
        this.content = content;
        this.to = to;
        this.cc = cc;
    }

    public MailFormat() {}

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return this.cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }
}
