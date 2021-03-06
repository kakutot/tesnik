package domain;

import java.util.Objects;

public class Car {
    private final static int DEFAULT_INT = -1 ;

    private final long carId;
    private final String brand;
    private final String modelName;
    private final int maxVelocity;
    private final int power;
    private final int ownerId;

    public Car(long carId, String brand, String modelName, int maxVelocity, int power, int ownerId) {
        this.carId = carId;
        this.brand = brand;
        this.modelName = modelName;
        this.maxVelocity = maxVelocity;
        this.power = power;
        this.ownerId = ownerId;
    }

    public static Car proxy(long carId) {
        return new Car(carId, null, null,  DEFAULT_INT, DEFAULT_INT, DEFAULT_INT);
    }

    public static Car proxyByPower(int power) {
        return new Car(DEFAULT_INT, null, null,  DEFAULT_INT, power, DEFAULT_INT);
    }

    public long getCarId() {
        return carId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModelName() {
        return modelName;
    }

    public int getMaxVelocity() {
        return maxVelocity;
    }

    public int getPower() {
        return power;
    }

    public int getOwnerId() {
        return ownerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(carId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return carId == ((Car) o).carId;
    }
}