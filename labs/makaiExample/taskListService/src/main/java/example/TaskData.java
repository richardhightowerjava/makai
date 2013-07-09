package example;

import example.Task;

public class TaskData {

    private  String owner;
    private  String name;
    private  String description;
    private  long date;

    public TaskData(Task task) {

        this.name = task.getName();
        this.owner = task.owner().key();
        this.description = task.getDescription();
        this.date = task.getTimestamp();

    }
}
