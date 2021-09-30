package impl;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.Car;
import domain.Owner;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GarageImplTest {

    private GarageImpl garage;
    private final Random random = new Random();

    @BeforeEach
    void setUp() {
       garage = new GarageImpl();
    }

    @Test
    void testAddCar() {
        Owner owner = createOwner();
        Car car = createCar(owner);

        garage.addCar(car, owner);

        assertFalse(garage.getCarTrack().isEmpty());
        assertFalse(garage.getCarOwner().get(owner).isEmpty());
        assertFalse(garage.getCarBrand().get(car.getBrand()).isEmpty());
        assertFalse(garage.getCarsByPower().isEmpty());
        assertFalse(garage.getCarsByVelocity().isEmpty());

        assertTrue(garage.getCarTrack().containsValue(new Pair(car, TRUE)));
        assertTrue(garage.getCarOwner().containsKey(owner));
        assertTrue(garage.getCarBrand().containsKey(car.getBrand()));
        assertEquals(garage.getCarsByPower().pollFirst(), car);
        assertEquals(garage.getCarsByVelocity().pollFirst(), car);
    }

    @Test
    void testRemoveCar() {
        Owner owner = createOwner();
        Car car = createCar(owner);

        garage.addCar(car, owner);

        assertFalse(garage.getCarTrack().isEmpty());
        assertFalse(garage.getCarOwner().get(owner).isEmpty());
        assertFalse(garage.getCarBrand().get(car.getBrand()).isEmpty());
        assertFalse(garage.getCarsByPower().isEmpty());
        assertFalse(garage.getCarsByVelocity().isEmpty());

        garage.removeCar((int) car.getCarId());

        assertFalse(garage.getCarTrack().get((int)car.getCarId()).getValue());
        assertTrue(garage.getCarOwner().get(owner).isEmpty());
        assertTrue(garage.getCarBrand().get(car.getBrand()).isEmpty());
        assertTrue(garage.getCarsByPower().isEmpty());
        assertTrue(garage.getCarsByVelocity().isEmpty());
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
    void testTopThreeCarsByMaxVelocity() {
        Owner owner1 = createOwner();
        Car car1 = new Car(random.nextInt(100000), "A", "B", 100,  50, (int) owner1.getOwnerId());

        garage.addCar(car1, owner1);

        Owner owner2 = createOwner();
        Car car2 = new Car(random.nextInt(100000), "C", "D", 120,  50, (int) owner2.getOwnerId());

        garage.addCar(car2, owner2);

        Owner owner3 = createOwner();
        Car car3 = new Car(random.nextInt(100000), "A", "B", 90,  50, (int) owner3.getOwnerId());

        garage.addCar(car3, owner3);

        Car car4 = new Car(random.nextInt(100000), "K", "V", 200,  50, (int) owner3.getOwnerId());

        garage.addCar(car4, owner1);

        Set<Car> expectedCars = Set.of(car1, car2, car4);
        Collection<Car> actualCars = garage.topThreeCarsByMaxVelocity();

        assertFalse(actualCars.isEmpty());
        assertEquals(3, actualCars.size());

        assertTrue(actualCars.containsAll(expectedCars));
    }

    @Test
    void testAllCarsOfBrand() {
        final String brandA = "A";
        final String brandB = "B";

        Owner owner1 = createOwner();
        Car car1 = new Car(random.nextInt(100000), brandA, "B", 100,  50, (int) owner1.getOwnerId());

        garage.addCar(car1, owner1);

        Owner owner2 = createOwner();
        Car car2 = new Car(random.nextInt(100000), brandB, "D", 120,  50, (int) owner2.getOwnerId());

        garage.addCar(car2, owner2);

        Owner owner3 = createOwner();
        Car car3 = new Car(random.nextInt(100000), brandA, "B", 90,  50, (int) owner3.getOwnerId());

        garage.addCar(car3, owner3);

        Owner owner4 = createOwner();
        Car car4 = new Car(random.nextInt(100000), brandB, "C", 90,  50, (int) owner4.getOwnerId());

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
    void testCarsWithPowerMoreThan() {
        Owner owner1 = createOwner();
        Car car1 = new Car(random.nextInt(100000), "", "B", 1,  20, (int) owner1.getOwnerId());

        garage.addCar(car1, owner1);

        Owner owner2 = createOwner();
        Car car2 = new Car(random.nextInt(100000), "", "D", 1,  50, (int) owner2.getOwnerId());

        garage.addCar(car2, owner2);

        Car car3 = new Car(random.nextInt(100000), "", "B", 1,  50, (int) owner1.getOwnerId());

        garage.addCar(car3, owner1);

        Car car4 = new Car(random.nextInt(100000), "", "C", 1,  60, (int) owner2.getOwnerId());

        garage.addCar(car4, owner2);

        Set<Car> expectedCars = Set.of(car2, car4);
        Collection<Car> actualCars = garage.carsWithPowerMoreThan(45);

        assertFalse(actualCars.isEmpty());
        assertEquals(3, actualCars.size());

        assertTrue(actualCars.containsAll(expectedCars));
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
            new Owner(random.nextInt(100000), "", "", 20),
            new Owner(random.nextInt(100000), "", "", 25),
            new Owner(random.nextInt(100000), "", "", 35),
            new Owner(random.nextInt(100000), "", "", 30)
        };

        String[] brands = {
            "A",
            "B"
        };

        Car[][] cars = {
            { createCar(owners[0], brands[0]), createCar(owners[0], brands[0]) }, // A = 20
            { createCar(owners[1], brands[0]), createCar(owners[1], brands[1]) }, // A = 20 + 25 , B = 25
            { createCar(owners[2], brands[0]), createCar(owners[2], brands[1]) }, // A = 45 + 35, B = 25 + 35
            { createCar(owners[3], brands[1]), createCar(owners[3], brands[1]) }   // A = 80 , B = 90
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
                        Collectors.toList()
                    )
                )
            );

       var expectedMeanAgesBrandA = expectedBrandAges.get(brands[0]);
       int meanExpectedMeanBrandA = expectedMeanAgesBrandA.stream().mapToInt(it -> it).sum() / expectedMeanAgesBrandA.size(); // 80 / 3

       var expectedMeanAgesBrandB = expectedBrandAges.get(brands[1]);
       int meanExpectedMeanBrandB = expectedMeanAgesBrandB.stream().mapToInt(it -> it).sum() / expectedMeanAgesBrandB.size(); // 90 / 3

       assertEquals(meanExpectedMeanBrandA, garage.meanOwnersAgeOfCarBrand(brands[0]));
       assertEquals(meanExpectedMeanBrandB, garage.meanOwnersAgeOfCarBrand(brands[1]));
    }

    private Car createCar(Owner owner) {
        return new Car(random.nextInt(100000), "", "", random.nextInt(100), random.nextInt(100),
            (int) owner.getOwnerId());
    }

    private Car createCar(Owner owner, String brand) {
        return new Car(random.nextInt(100000), brand, "", random.nextInt(100), random.nextInt(100),
            (int) owner.getOwnerId());
    }

    private Owner createOwner() {
        return new Owner(random.nextInt(100000), "", "", random.nextInt(100));
    }
}
