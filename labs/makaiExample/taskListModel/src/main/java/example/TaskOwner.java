package example;


public abstract class TaskOwner {

    public static final TaskOwner NO_OWNER = new TaskOwner() {
        @Override
        public String key() {
            return toString();
        }
    };


    public abstract String key();

    public String toString() {
        return "NO_OWNER";
    }
}
