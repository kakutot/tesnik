package impl;

import domain.Car;
import domain.Owner;

import java.util.Objects;
import java.util.Random;

public final class TestDataFactory {
    private final static int DEFAULT_CEIL = 100_000;
    private final static Random random = new Random();

    public static Car createCar(Owner owner) {
        return new Car(getRandomInt(null), "", "", random.nextInt(100), random.nextInt(100),
                (int) owner.getOwnerId());
    }

    public static Car createCar(Owner owner, String brand) {
        return new Car(getRandomInt(null), brand, "", random.nextInt(100), random.nextInt(100),
                (int) owner.getOwnerId());
    }

    public static Owner createOwner() {
        return new Owner(getRandomInt(null), "", "", random.nextInt(100));
    }
    
    public static int getRandomInt(Integer ceil) {
        return random.nextInt(Objects.nonNull(ceil) ? ceil : DEFAULT_CEIL);
    }

}
