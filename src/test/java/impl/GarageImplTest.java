package impl;

import domain.Car;
import domain.Owner;
import org.assertj.core.api.Condition;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static impl.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GarageImplTest {

    private GarageImpl garage;

    private static final String CAR_TRACK = "carTrack";
    private static final String CAR_OWNER = "carOwner";
    private static final String CAR_BRAND = "carBrand";
    private static final String CARS_BY_VELOCITY = "carsByVelocity";
    private static final String CARS_BY_POWER = "carsByPower";


    @BeforeEach
    void setUp() {
       garage = new GarageImpl();
    }

    @Test
    void testAddCar() {
        Owner owner = createOwner();
        Car car = createCar(owner);

        assertThat(garage).extracting(CAR_TRACK).asInstanceOf(InstanceOfAssertFactories.MAP).isEmpty();
        assertThat(garage).extracting(CAR_OWNER).asInstanceOf(InstanceOfAssertFactories.MAP).doesNotContainKey(owner);
        assertThat(garage).extracting(CAR_BRAND).asInstanceOf(InstanceOfAssertFactories.MAP).doesNotContainKey(car.getBrand());
        assertThat(garage).extracting(CARS_BY_POWER).asInstanceOf(InstanceOfAssertFactories.COLLECTION).isEmpty();
        assertThat(garage).extracting(CARS_BY_VELOCITY).asInstanceOf(InstanceOfAssertFactories.COLLECTION).isEmpty();

        garage.addCar(car, owner);

        assertThat(garage).extracting(CAR_TRACK).asInstanceOf(InstanceOfAssertFactories.MAP).isNotEmpty();
        assertThat(garage).extracting(CAR_OWNER).asInstanceOf(InstanceOfAssertFactories.MAP).containsKey(owner);
        assertThat(garage).extracting(CAR_BRAND).asInstanceOf(InstanceOfAssertFactories.MAP).containsKey(car.getBrand());
        assertThat(garage).extracting(CARS_BY_POWER).asInstanceOf(InstanceOfAssertFactories.COLLECTION).isNotEmpty();
        assertThat(garage).extracting(CARS_BY_VELOCITY).asInstanceOf(InstanceOfAssertFactories.COLLECTION).isNotEmpty();
    }

    @Test
    void testRemoveCar() {
        Owner owner = createOwner();
        Car car = createCar(owner);

        garage.addCar(car, owner);

        assertThat(garage).extracting(CAR_TRACK).asInstanceOf(InstanceOfAssertFactories.MAP).isNotEmpty();
        assertThat(garage).extracting(CAR_OWNER).asInstanceOf(InstanceOfAssertFactories.MAP).has(mapOfCollectionContainsCondition(car));
        assertThat(garage).extracting(CAR_BRAND).asInstanceOf(InstanceOfAssertFactories.MAP).has(mapOfCollectionContainsCondition(car));
        assertThat(garage).extracting(CARS_BY_POWER).asInstanceOf(InstanceOfAssertFactories.COLLECTION).isNotEmpty();
        assertThat(garage).extracting(CARS_BY_VELOCITY).asInstanceOf(InstanceOfAssertFactories.COLLECTION).isNotEmpty();

        garage.removeCar((int) car.getCarId());

        assertThat(garage).extracting(CAR_TRACK).asInstanceOf(InstanceOfAssertFactories.MAP).isEmpty();
        assertThat(garage).extracting(CAR_OWNER).asInstanceOf(InstanceOfAssertFactories.MAP).doesNotHave(mapOfCollectionContainsCondition(car));
        assertThat(garage).extracting(CAR_BRAND).asInstanceOf(InstanceOfAssertFactories.MAP).doesNotHave(mapOfCollectionContainsCondition(car));
        assertThat(garage).extracting(CARS_BY_POWER).asInstanceOf(InstanceOfAssertFactories.COLLECTION).isEmpty();
        assertThat(garage).extracting(CARS_BY_VELOCITY).asInstanceOf(InstanceOfAssertFactories.COLLECTION).isEmpty();
    }


    @Test
    void testAllCarsUniqueOwners() {
        Owner owner1 = createOwner();
        Car car1 = createCar(owner1);

        garage.addCar(car1, owner1);

        Owner owner2 = createOwner();
        Car car2 = createCar(owner2);

        garage.addCar(car2, owner2);

        Owner owner3 = createOwner();
        Car car3 = createCar(owner3);

        garage.addCar(car3, owner3);

        Car car4 = createCar(owner1);

        garage.addCar(car4, owner1);

        Set<Owner> expectedOwners = Set.of(owner1, owner2, owner3);
        Collection<Owner> actualOwners = garage.allCarsUniqueOwners();

        assertFalse(actualOwners.isEmpty());
        assertEquals(3, actualOwners.size());

        assertTrue(actualOwners.containsAll(expectedOwners));
    }

    @Test
    void testAllCarsUniqueOwnersNoCars() {
        Owner owner1 = createOwner();
        Car car1 = createCar(owner1);

        garage.addCar(car1, owner1);

        garage.removeCar((int) car1.getCarId());
        Collection<Owner> actualOwners = garage.allCarsUniqueOwners();

        assertTrue(actualOwners.contains(owner1));
    }

    @Test
    void testTopThreeCarsByMaxVelocity() {
        Owner owner1 = createOwner();
        Car car1 = new Car(getRandomInt(null), "A", "B", 100,  50, (int) owner1.getOwnerId());

        garage.addCar(car1, owner1);

        Owner owner2 = createOwner();
        Car car2 = new Car(getRandomInt(null), "C", "D", 120,  50, (int) owner2.getOwnerId());

        garage.addCar(car2, owner2);

        Owner owner3 = createOwner();
        Car car3 = new Car(getRandomInt(null), "A", "B", 90,  50, (int) owner3.getOwnerId());

        garage.addCar(car3, owner3);

        Car car4 = new Car(getRandomInt(null), "K", "V", 200,  50, (int) owner3.getOwnerId());

        garage.addCar(car4, owner1);

        Set<Car> expectedCars = Set.of(car1, car2, car4);
        Collection<Car> actualCars = garage.topThreeCarsByMaxVelocity();

        assertFalse(actualCars.isEmpty());
        assertEquals(3, actualCars.size());

        assertTrue(actualCars.containsAll(expectedCars));
    }

    @Test
    void testTopThreeCarsByMaxVelocityTwoCarsOnlyPresent() {
        Owner owner1 = createOwner();
        Car car1 = new Car(getRandomInt(null), "A", "B", 100,  50, (int) owner1.getOwnerId());

        garage.addCar(car1, owner1);

        Owner owner2 = createOwner();
        Car car2 = new Car(getRandomInt(null), "C", "D", 120,  50, (int) owner2.getOwnerId());

        garage.addCar(car2, owner2);

        Set<Car> expectedCars = Set.of(car1, car2);
        Collection<Car> actualCars = garage.topThreeCarsByMaxVelocity();

        assertFalse(actualCars.isEmpty());
        assertEquals(2, actualCars.size());

        assertTrue(actualCars.containsAll(expectedCars));
    }

    @Test
    void testAllCarsOfBrand() {
        final String brandA = "A";
        final String brandB = "B";

        Owner owner1 = createOwner();
        Car car1 = new Car(getRandomInt(null), brandA, "B", 100,  50, (int) owner1.getOwnerId());

        garage.addCar(car1, owner1);

        Owner owner2 = createOwner();
        Car car2 = new Car(getRandomInt(null), brandB, "D", 120,  50, (int) owner2.getOwnerId());

        garage.addCar(car2, owner2);

        Owner owner3 = createOwner();
        Car car3 = new Car(getRandomInt(null), brandA, "B", 90,  50, (int) owner3.getOwnerId());

        garage.addCar(car3, owner3);

        Owner owner4 = createOwner();
        Car car4 = new Car(getRandomInt(null), brandB, "C", 90,  50, (int) owner4.getOwnerId());

        garage.addCar(car4, owner4);

        Set<Car> expectedCarsBrandA = Set.of(car1, car3);
        Set<Car> expectedCarsBrandB = Set.of(car2, car4);

        Collection<Car> actualCarsBrandA = garage.allCarsOfBrand(brandA);
        Collection<Car> actualCarsBrandB = garage.allCarsOfBrand(brandB);

        assertFalse(actualCarsBrandA.isEmpty());
        assertFalse(actualCarsBrandB.isEmpty());

        assertEquals(2, actualCarsBrandA.size());
        assertEquals(2, actualCarsBrandB.size());

        assertTrue(actualCarsBrandA.containsAll(expectedCarsBrandA));
        assertTrue(actualCarsBrandB.containsAll(expectedCarsBrandB));
    }

    @Test
    void testAllCarsOfBrandNoCars() {
        final String brandA = "A";

        Owner owner1 = createOwner();
        Car car1 = new Car(getRandomInt(null), brandA, "B", 100,  50, (int) owner1.getOwnerId());

        garage.addCar(car1, owner1);

        garage.removeCar((int) car1.getCarId());
        Collection<Car> actualCarsBrandA = garage.allCarsOfBrand(brandA);

        assertTrue(actualCarsBrandA.isEmpty());
    }

    @Test
    void testCarsWithPowerMoreThan() {
        Owner owner1 = createOwner();
        Car car1 = new Car(getRandomInt(null), "", "B", 1,  20, (int) owner1.getOwnerId());

        garage.addCar(car1, owner1);

        Owner owner2 = createOwner();
        Car car2 = new Car(getRandomInt(null), "", "D", 1,  50, (int) owner2.getOwnerId());

        garage.addCar(car2, owner2);

        Car car3 = new Car(getRandomInt(null), "", "B", 1,  50, (int) owner1.getOwnerId());

        garage.addCar(car3, owner1);

        Car car4 = new Car(getRandomInt(null), "", "C", 1,  60, (int) owner2.getOwnerId());

        garage.addCar(car4, owner2);

        Set<Car> expectedCars = Set.of(car2, car4);
        Collection<Car> actualCars = garage.carsWithPowerMoreThan(45);

        assertFalse(actualCars.isEmpty());
        assertEquals(3, actualCars.size());

        assertTrue(actualCars.containsAll(expectedCars));
    }

    @Test
    void testCarsWithPowerMoreThanNoCars() {
        Owner owner1 = createOwner();
        Car car1 = new Car(getRandomInt(null), "", "B", 1,  20, (int) owner1.getOwnerId());

        garage.addCar(car1, owner1);

        Owner owner2 = createOwner();
        Car car2 = new Car(getRandomInt(null), "", "D", 1,  50, (int) owner2.getOwnerId());

        garage.addCar(car2, owner2);

        Car car3 = new Car(getRandomInt(null), "", "B", 1,  50, (int) owner1.getOwnerId());

        garage.addCar(car3, owner1);

        Car car4 = new Car(getRandomInt(null), "", "C", 1,  60, (int) owner2.getOwnerId());

        garage.addCar(car4, owner2);

        Collection<Car> actualCars = garage.carsWithPowerMoreThan(100);

        assertTrue(actualCars.isEmpty());
    }

    @Test
    void testAllCarsOfOwner() {
        Owner owner1 = createOwner();
        Car car1 = createCar(owner1);

        garage.addCar(car1, owner1);

        Owner owner2 = createOwner();
        Car car2 = createCar(owner2);

        garage.addCar(car2, owner2);

        Car car3 = createCar(owner1);
        garage.addCar(car3, owner1);

        Car car4 = createCar(owner2);
        garage.addCar(car4, owner2);

        Car car5 = createCar(owner2);
        garage.addCar(car5, owner2);

        Set<Car> expectedCarsOwner1 = Set.of(car1, car3);
        Set<Car> expectedCarsOwner2 = Set.of(car2, car4, car5);

        Collection<Car> actualCarsOwner1 = garage.allCarsOfOwner(owner1);
        Collection<Car> actualCarsOwner2 = garage.allCarsOfOwner(owner2);

        assertFalse(actualCarsOwner1.isEmpty());
        assertFalse(actualCarsOwner2.isEmpty());

        assertEquals(2, actualCarsOwner1.size());
        assertEquals(3, actualCarsOwner2.size());

        assertTrue(actualCarsOwner1.containsAll(expectedCarsOwner1));
        assertTrue(actualCarsOwner2.containsAll(expectedCarsOwner2));
    }

    @Test
    void testAllCarsOfOwnerNoCars() {
        Owner owner1 = createOwner();
        Car car1 = createCar(owner1);

        garage.addCar(car1, owner1);

        garage.removeCar((int) car1.getCarId());

        assertTrue(garage.allCarsOfOwner(owner1).isEmpty());
    }

    @Test
    void meanCarNumberForEachOwnerTest() {
        Owner[] owners = {
            createOwner(),
            createOwner(),
            createOwner()
        };

        Car[][] cars = {
            { createCar(owners[0]), createCar(owners[0]) },
            { createCar(owners[1]), createCar(owners[1]), createCar(owners[1]) },
            { createCar(owners[2]), createCar(owners[2]), createCar(owners[2]), createCar(owners[2]) }
        };

        Arrays.stream(cars).flatMap(Stream::of).forEach(it -> {
                garage.addCar(it,
                    Arrays.stream(owners)
                        .filter(o -> o.getOwnerId() == it.getOwnerId())
                        .findFirst()
                        .orElse(null));
            }
        );

        int expectedMean = Arrays.stream(cars).mapToInt(it -> it.length).sum() / cars.length;
        int actualMean = garage.meanCarNumberForEachOwner();

        assertEquals(expectedMean, actualMean);
    }

    @Test
    void meanOwnersAgeOfCarBrand() {
        Owner[] owners = {
            new Owner(getRandomInt(null), "", "", 20),
            new Owner(getRandomInt(null), "", "", 25),
            new Owner(getRandomInt(null), "", "", 35),
            new Owner(getRandomInt(null), "", "", 30)
        };

        String[] brands = {
            "A",
            "B"
        };

        Car[][] cars = {
            { createCar(owners[0], brands[0]), createCar(owners[0], brands[0]) },
            { createCar(owners[1], brands[0]), createCar(owners[1], brands[1]) },
            { createCar(owners[2], brands[0]), createCar(owners[2], brands[1]) },
            { createCar(owners[3], brands[1]), createCar(owners[3], brands[1]) }
        };

        Arrays.stream(cars).flatMap(Stream::of).forEach(it -> {
                garage.addCar(it,
                    Arrays.stream(owners)
                        .filter(o -> o.getOwnerId() == it.getOwnerId())
                        .findFirst()
                        .orElse(null));
            }
        );

       var expectedBrandAges = Arrays.stream(cars).flatMap(Stream::of)
            .collect(
                Collectors.groupingBy(Car::getBrand,
                    Collectors.mapping(
                        it -> owners[Arrays.asList(owners).indexOf(Owner.proxy(it.getOwnerId()))].getAge(),
                        Collectors.toSet()
                    )
                )
            );

       var expectedMeanAgesBrandA = expectedBrandAges.get(brands[0]);
       int meanExpectedMeanBrandA = expectedMeanAgesBrandA.stream().distinct().mapToInt(it -> it).sum() / expectedMeanAgesBrandA.size(); // 80 / 3

       var expectedMeanAgesBrandB = expectedBrandAges.get(brands[1]);
       int meanExpectedMeanBrandB = expectedMeanAgesBrandB.stream().distinct().mapToInt(it -> it).sum() / expectedMeanAgesBrandB.size(); // 90 / 3

       assertEquals(meanExpectedMeanBrandA, garage.meanOwnersAgeOfCarBrand(brands[0]));
       assertEquals(meanExpectedMeanBrandB, garage.meanOwnersAgeOfCarBrand(brands[1]));
    }


    private <R, T extends Map<Object, ? extends Collection<? extends R>>> Condition<? super Object> mapOfCollectionContainsCondition(R item) {
        return new Condition<>(it -> ((T) it).values().stream()
                .flatMap(Collection::stream).anyMatch(i -> i.equals(item)), "temp"
        );
    }
}
