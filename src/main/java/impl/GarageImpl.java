package impl;

import domain.Car;
import domain.Garage;
import domain.Owner;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GarageImpl implements Garage {

    /*
        HashMap.get :
          MIN - 0(1),
          AVG - 0(1 + LOAD_FACTOR),
          WORST CASE - O(N),
        But Owner.hashCode is computed based on ownerId, assuming it is always unique - no collisions, MIN complexity

       ----------------------------------------------------

       HashMap.put :
          MIN,AVG,WORST CASE - O(1)
     */

    private final Map<Owner, Collection<Car>> ownerCars = new HashMap<>();
    private final Map<String, Collection<Car>> brandCars = new HashMap<>();

    @Override
    public Collection<Owner> allCarsUniqueOwners() {
        return ownerCars.keySet();
    }

    @Override
    public Collection<Car> topThreeCarsByMaxVelocity() {
        return null;
    }

    @Override
    public Collection<Car> allCarsOfBrand(String brand) {
        /* asStream()
            .flatMap(it -> it.getValue().stream())
            .filter(it -> it.getBrand().equals(brand))
            .collect(Collectors.toList()); */

    }

    @Override
    public Collection<Car> carsWithPowerMoreThan(int power) {
        return null;
    }

    @Override
    public Collection<Car> allCarsOfOwner(Owner owner) {
        return ownerCars.get(owner);
    }

    @Override
    public int meanOwnersAgeOfCarBrand(String brand) {
        return 0;
    }

    @Override
    public int meanCarNumberForEachOwner() {
        return asStream().mapToInt(it -> it.getValue().size()).sum() / ownerCars.size();
    }

    @Override
    public void addCar(Car car, Owner owner) {
        if (Objects.nonNull(car) && Objects.nonNull(owner)) {
        /*
         Arrays.asList(ArrayList).add :
            MIN,AVG,WORST CASE - O(1)
         */

            Optional.of(ownerCars.get(owner))
                .ifPresentOrElse(
                    it -> it.add(car), () -> ownerCars.put(owner, Arrays.asList(car))
                );

            final String brand = car.getBrand();
            if (Objects.nonNull())

            Optional.of(brandCars.get(brand))
                .ifPresentOrElse(
                    it -> it.add(car), () -> brandCars.put(brand, Arrays.asList(car))
                );
        }
    }

    @Override
    public Car removeCar(int carId) {
        var cr = asStream()
            .flatMap(owner -> owner.getValue().stream()
                .map(car -> new Pair<>(owner.getKey(), car)))
            .filter(it -> carId == it.getValue().getCarId())
            .findFirst()
            .orElse(null);

        if (Objects.nonNull(cr)) {
            ownerCars.get(cr.getKey()).remove(cr.getValue());
        }

        return Objects.isNull(cr) ? null : cr.getValue();
    }

    private Stream<Entry<Owner, Collection<Car>>> asStream() {
        return ownerCars.entrySet().stream();
    }

    static class Pair<L, R> {

        private final L key;
        private final R value;

        public Pair(L key, R value) {
            this.key = key;
            this.value = value;
        }

        public L getKey() {
            return key;
        }

        public R getValue() {
            return value;
        }
    }
}
