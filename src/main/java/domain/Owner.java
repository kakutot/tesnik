package domain;

import java.util.Objects;

public class Owner {
    private final long ownerId;
    private final String name;
    private final String lastName;
    private final int age;

    public Owner(long ownerId, String name, String lastName, int age) {
        this.ownerId = ownerId;
        this.name = name;
        this.lastName = lastName;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return ownerId == ((Owner) o).ownerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId);
    }
}