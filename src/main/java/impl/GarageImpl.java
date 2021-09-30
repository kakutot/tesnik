package impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import domain.Car;
import domain.Garage;
import domain.Owner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class GarageImpl implements Garage {

    /*
        HashMap.get :
          MIN - 0(1),
          AVG - 0(1 + LOAD_FACTOR),
          WORST CASE - O(N),
        But hashCode implementation is based on unique columns (ownerId and brand) - well-distributed across buckets, no collisions, MIN complexity
     */

    private final Map<Integer, Pair<Car, Boolean>> carTrack = new HashMap<>();

    private final Map<Owner, Collection<Car>> carOwner = new HashMap<>();
    private final Map<String, Collection<Car>> carBrand = new HashMap<>();

    private final Comparator<Pair<Long, Integer>> comparator =  (a, b) -> a.getKey().equals(b.getKey()) ? 0
        : (a.getValue().equals(b.getValue()) ? 1 : Integer.compare(a.getValue(), b.getValue()));

    private final NavigableSet<Car> carsByVelocity = new TreeSet<>(
        Comparator.comparing(it -> new Pair<>(it.getCarId(), it.getMaxVelocity()), comparator)
    );

    private final NavigableSet<Car> carsByPower = new TreeSet<>(
        Comparator.comparing(it -> new Pair<>(it.getCarId(), it.getPower()), comparator)
    );

    @Override
    public Collection<Owner> allCarsUniqueOwners() {
        return carOwner.keySet().stream().distinct().collect(Collectors.toList());
    }

    @Override
    public Collection<Car> topThreeCarsByMaxVelocity() {
        List<Car> result = new ArrayList<>();

        Iterator<Car> iterator = carsByVelocity.descendingIterator();
        int c = 0;
        while (iterator.hasNext() && c++ < 3) {
            result.add(iterator.next());
        }

        return result;
    }

    @Override
    public Collection<Car> carsWithPowerMoreThan(int power) {
        //tailSet complexity - O(logN)
        return carsByPower.tailSet(Car.proxyByPower(power));
    }

    @Override
    public Collection<Car> allCarsOfBrand(String brand) {
        return carBrand.get(brand);
    }

    @Override
    public Collection<Car> allCarsOfOwner(Owner owner) {
        return carOwner.get(owner);
    }

    @Override
    public int meanOwnersAgeOfCarBrand(String brand) {
        List<Integer> ownerIds = carBrand.get(brand).stream()
            .map(Car::getOwnerId)
            .collect(Collectors.toList());

        Map<Integer, Integer> ownerToAge =
            carOwner.keySet().stream()
                .collect(Collectors.toMap(it -> ((int) it.getOwnerId()), Owner::getAge));

        return ownerIds.stream().mapToInt(ownerToAge::get).sum() / carBrand.get(brand).size();
    }

    @Override
    public int meanCarNumberForEachOwner() {
        return carOwner.values().stream().mapToInt(Collection::size).sum() / carOwner.size();
    }

    @Override
    public void addCar(Car car, Owner owner) {
        if (Objects.nonNull(car) && Objects.nonNull(owner) && !carTrack
            .containsKey((int) car.getCarId())) {
            carTrack.put((int) car.getCarId(), new Pair(car, TRUE));

            Optional.ofNullable(carOwner.get(owner))
                .ifPresentOrElse(

                    // HashSet add complexity - O(1)
                    it -> it.add(car),

                    // HashMap put complexity - O(1)
                    () -> carOwner.put(owner, new HashSet<>(Arrays.asList(car)))
                );

            //TreeSet add complexity - O(logN)
            carsByVelocity.add(car);
            carsByPower.add(car);

            final String brand = car.getBrand();
            if (Objects.nonNull(brand)) {

                // HashMap get complexity - O(1)
                Optional.ofNullable(carBrand.get(brand))
                    .ifPresentOrElse(

                        // HashSet add complexity - O(1)
                        it -> it.add(car),

                        // HashMap put complexity - O(1)
                        () -> carBrand.put(brand, new HashSet<>(Arrays.asList(car)))
                    );
            }
        }
    }

    @Override
    public Car removeCar(int carId) {
        Car car = null;

        if (carTrack.containsKey(carId) && carTrack.get(carId).getValue()) {
            car = carTrack.get(carId).getKey();

            //HashMap put time complexity - O(1)
            carTrack.put(carId, new Pair<>(car, FALSE));

            //HashMap get & HashSet remove complexity - O(1)
            carOwner.get(Owner.proxy(car.getOwnerId())).remove(Car.proxy(carId));

            //Assuming all brands are unique - HashMap get & HashSet remove complexity - O(1)
            carBrand.get(car.getBrand()).remove(Car.proxy(carId));

            //TreeSet remove complexity - O(logN)
            carsByPower.remove(car);
            carsByVelocity.remove(car);
        }

        return car;
    }

    //for testing

    public Map<Integer, Pair<Car, Boolean>> getCarTrack() {
        return carTrack;
    }

    public Map<Owner, Collection<Car>> getCarOwner() {
        return carOwner;
    }

    public Map<String, Collection<Car>> getCarBrand() {
        return carBrand;
    }

    public NavigableSet<Car> getCarsByVelocity() {
        return carsByVelocity;
    }

    public NavigableSet<Car> getCarsByPower() {
        return carsByPower;
    }
}
