package example;


import io.makai.core.Export;
import io.makai.core.Service;

import java.util.HashSet;
import java.util.Set;


@Service("/taskService")
@Export
public class TaskService {

    TaskManager manager = new TaskManager();
    OwnerRepo repo = new OwnerRepo();
    Group group = new Group(repo, "shared");

    public String addTask(String taskName) {
          Task task = new Task(taskName);
          manager.addTask(group, task);
          return task.id();
    }

    public boolean changeDescription(String id, String description) {
        Task task = manager.getTaskBy(id);
        task.setDescription(description);
        return true;
    }


    public Set<TaskData> tasks() {
        Set<TaskData> taskData = new HashSet<>();

        for (Task task : manager.tasks()) {
            taskData.add(new TaskData(task)) ;
        }
        return taskData;
    }

}
