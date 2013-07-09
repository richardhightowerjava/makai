package example;

import java.util.HashSet;
import java.util.Set;

public class Group extends TaskOwner {
    public static final Group NO_GROUP = new Group("NO GROUP");
    private static final String NULL = "";

    private Set<Person> people = new HashSet<>();
    private String name=NULL;


    private Group (String name) {
        this.name = name;
    }
    public Group (OwnerRepo repo, String name) {
        this.name = name;
        repo.addOwner(this);
    }

    public Set<Person> getPeople() {
        return people;
    }

    public void addPerson(Person person) {
        this.people.add(person);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;

        Group group = (Group) o;

        if (!name.equals(group.name)) return false;
        if (!people.equals(group.people)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = people.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String key() {
        return name;
    }

    @Override
    public String toString() {
        return "Group{" +
                "people=" + people +
                ", name='" + name + '\'' +
                '}';
    }
}
