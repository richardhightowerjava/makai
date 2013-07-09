package io.jamp.example.model;


import example.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TaskManagerTest {

    TaskManager manager = new TaskManager();
    Group group;
    Person person;
    OwnerRepo repo = new OwnerRepo();

    Task taskToAdd = new Task("2");
    @Before
    public void setUp() throws Exception {
        manager = new TaskManager();
        group = new Group(repo, "engineering");

        person = new Person(repo, "rick@caucho.com").firstName("Rick")
                .lastName("Smith").group(group);


    }

    @Test
    public void testName() throws Exception {
        manager.addTask(person, taskToAdd);
        assertTrue(manager.tasks().contains(taskToAdd));
        manager.removeTask(taskToAdd);

        assertTrue(!manager.tasks().contains(taskToAdd));

    }
}
