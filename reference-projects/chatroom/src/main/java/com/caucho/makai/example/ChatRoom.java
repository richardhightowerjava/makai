package com.caucho.makai.example;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ChatRoom {

    private String id;
    private String welcomeMessage;
    private User createdBy;
    private String name;
    private List<User> users = new ArrayList<>();
    private Deque<Message> messages = new ArrayDeque<>(100);

    public ChatRoom() {

    }

    public void addMessage(Message message) {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addUser(User user) {
        getUsers().add(user);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }
}