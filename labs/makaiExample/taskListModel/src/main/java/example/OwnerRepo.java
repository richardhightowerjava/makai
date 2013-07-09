package example;

import java.util.HashMap;
import java.util.Map;

public class OwnerRepo {
    Map<String, TaskOwner> owners = new HashMap<>();

    void addOwner(TaskOwner taskDoer) {
        owners.put(taskDoer.key(), taskDoer);
    }

    public TaskOwner findOwner(String key) {
        return owners.get(key);
    }

}
