package impl;

import domain.Car;
import domain.Garage;
import domain.Owner;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GarageImpl implements Garage {

    /*
        HashMap.get :
          MIN - 0(1),
          AVG - 0(1 + LOAD_FACTOR),
          WORST CASE - O(N),
        But hashCode implementation is based on unique columns (ownerId and brand) - well-distributed across buckets, no collisions, MIN complexity
     */

    private final Map<Long, Car> carTrack = new HashMap();

    private final Map<Owner, Collection<Car>> carOwner = new HashMap<>();
    private final Map<String, Collection<Car>> carBrand = new HashMap<>();

    private final Comparator<Car> comparator = Comparator.comparing(Car::getCarId);

    private final NavigableSet<Car> carsByVelocity = new TreeSet<>(
            Comparator.comparing(Function.identity(), Comparator.comparingInt(Car::getMaxVelocity))
                    .thenComparing(Function.identity(), comparator));

    private final NavigableSet<Car> carsByPower = new TreeSet<>(
            Comparator.comparing(Function.identity(), Comparator.comparingInt(Car::getPower))
                    .thenComparing(Function.identity(), comparator));

    @Override
    public Collection<Owner> allCarsUniqueOwners() {
        return new ArrayList<>(carOwner.keySet());
    }

    @Override
    public Collection<Car> topThreeCarsByMaxVelocity() {
        List<Car> result = new ArrayList<>();

        Iterator<Car> iterator = carsByVelocity.descendingIterator();
        int counter = 0;
        while (iterator.hasNext() && counter++ < 3) {
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
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, Integer> ownerToAge =
            carOwner.keySet().stream()
                .collect(Collectors.toMap(it -> ((int) it.getOwnerId()), Owner::getAge));

        return ownerIds.stream().mapToInt(ownerToAge::get).sum() / ownerIds.size();
    }

    @Override
    public int meanCarNumberForEachOwner() {
        return carOwner.values().stream().mapToInt(Collection::size).sum() / carOwner.size();
    }

    @Override
    public void addCar(Car car, Owner owner) {
        if (Objects.nonNull(car) && Objects.nonNull(owner) && !carTrack.containsKey(car.getCarId())) {
            carTrack.put(car.getCarId(), car);

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

        if (carTrack.containsKey((long)carId)) {
            car = carTrack.get((long)carId);

            carTrack.remove((long)carId);

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
}
