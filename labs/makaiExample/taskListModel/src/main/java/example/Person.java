package example;


public class Person extends TaskOwner {

    private static final String NULL = "NULL";
    private String firstName=NULL;
    private String lastName=NULL;
    private String email=NULL;
    private Group group = Group.NO_GROUP;

    public Person (OwnerRepo repo, String email) {
        this.email = email;
        repo.addOwner(this);
    }

    public Person firstName(String aFirstName) {
        Utils.notEmpty(aFirstName);
        firstName = aFirstName;
        return this;
    }


    public Person lastName(String aLastName) {
        Utils.notEmpty(aLastName);
        lastName = aLastName;
        return this;
    }

    public Person group(Group g) {
        Utils.notNull(g);
        group = g;
        return this;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGroup (Group group) {
        this.group.addPerson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (email != null ? !email.equals(person.email) : person.email != null) return false;
        if (firstName != null ? !firstName.equals(person.firstName) : person.firstName != null) return false;
        if (lastName != null ? !lastName.equals(person.lastName) : person.lastName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", group=" + group +
                '}';
    }

    public String key() {
        return email;
    }
}
