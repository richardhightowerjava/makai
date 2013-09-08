package com.caucho.makai.example;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cmathias
 */
public class Message implements Serializable {
    public User user;
    public String message;
    public Date arrived;
}
