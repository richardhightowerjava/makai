package example;

import java.util.*;

public class TaskManager {

    private Set<Task> tasks = new HashSet<Task>();
    private transient Map<TaskOwner, Set<Task>> ownerToTaskMap = new HashMap<>();
    private transient Map<String, Task> idToTask = new HashMap<>();

    public void addTask(TaskOwner owner, Task task) {
        task.setOwner(owner);
        tasks.add(task);

        Set<Task> ownerTasks = ownerToTaskMap.get(owner);
        if (ownerTasks == null) {
            ownerTasks = new HashSet<>();
        }
        ownerTasks.add(task);
        idToTask.put(task.id(), task);

    }

    public void removeTask(Task task) {
        tasks.remove(task);
        idToTask.remove(task.id());

        Set<Task> ownerTasks = ownerToTaskMap.get(task.owner());
        if (ownerTasks == null) {
            ownerTasks = new HashSet<>();
        }
        ownerTasks.remove(task);

    }

    public Set<Task> tasks() {
        return tasks;
    }

    public Set<Task> listTasksForOwner(TaskOwner doer) {
        return ownerToTaskMap.get(doer);
    }

    public Task getTaskBy(String id) {
        return idToTask.get(id);
    }
}
