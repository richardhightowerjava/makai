package com.caucho.pilot;

/**
 * Created with IntelliJ IDEA.
 * User: cmathias
 * Date: 5/1/13
 * Time: 11:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class Entry {

    private long id;
    private String author;
    private String title;
    private String body;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
