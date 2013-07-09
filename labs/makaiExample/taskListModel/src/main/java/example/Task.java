package example;


import java.util.Random;

public class Task {

    private static final String NULL = "NULL";

    private static int count =0;
    private long timestamp;
    private String objectId=NULL;
    private String name=NULL;
    private String description=NULL;
    private TaskOwner owner = TaskOwner.NO_OWNER;

    public Task task(String name) {
        Task task = new Task(name);
        return task;
    }
    public Task(String name) {
         this.timestamp = System.currentTimeMillis();
         this.name = name;
         this.objectId = String.format("%s-%s-%s-%s",
                 name, new Random().nextLong(), System.nanoTime(), count++);
    }

    public TaskOwner owner() {return owner;}
    public String id() {return objectId;}

    public void setOwner(TaskOwner owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        Task task = (Task) o;
        if (objectId != null ? !objectId.equals(task.objectId) : task.objectId != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return objectId.hashCode();
    }

    @Override
    public String toString() {
        return "Task{" +
                "timestamp=" + timestamp +
                ", objectId='" + objectId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", owner=" + owner +
                '}';
    }

    public long getTimestamp() {
        return timestamp;
    }
}
