package org.genericsystem.distributed.cacheonclient;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import org.genericsystem.common.Generic;
import org.genericsystem.kernel.Statics;
import org.testng.annotations.Test;

@Test
public class ObservableDefaultTest extends AbstractTest {

	public void basicObservableAttributesTest() throws InterruptedException {
		CocClientEngine engine = new CocClientEngine();

		Generic vehicle = engine.addInstance("vehicle");
		ObservableList<Generic> vehicleObservableDependencies = vehicle.getCurrentCache().getObservableDependencies(vehicle);
		vehicleObservableDependencies.addListener((InvalidationListener) l -> System.out.println("vehicleObservableDependencies invalidation"));

		Generic power = vehicle.addAttribute("power");
		vehicle.addInstance("myBmw");

		Thread.sleep(100);
	}

	public void basicObservableAttributesTest2() throws InterruptedException {
		CocClientEngine engine = new CocClientEngine();
		ObservableList<Generic> engineObservableDependencies = engine.getCurrentCache().getObservableDependencies(engine);
		engineObservableDependencies.addListener((InvalidationListener) l -> System.out.println("engineObservableDependencies invalidation"));

		Generic vehicle = engine.addInstance("vehicle");
		ObservableList<Generic> vehicleObservableDependencies = vehicle.getCurrentCache().getObservableDependencies(vehicle);
		vehicleObservableDependencies.addListener((InvalidationListener) l -> System.out.println("vehicleObservableDependencies invalidation"));
		ObservableList<Generic> vehicleObservableAttributes = vehicle.getObservableAttributes();

		Generic car = engine.addInstance(vehicle, "car");
		ObservableList<Generic> carObservableAttributes = car.getObservableAttributes();

		Generic power = vehicle.addAttribute("power");
		Thread.sleep(100);
		assert carObservableAttributes.contains(power);
		assert vehicleObservableAttributes.contains(power);

		Generic test = vehicle.addAttribute("test");
		Thread.sleep(100);
		assert carObservableAttributes.contains(test);
		assert vehicleObservableAttributes.contains(test);
	}

	public void basicObservableAttributesTest3() throws InterruptedException {
		CocClientEngine engine = new CocClientEngine();

		Generic vehicle = engine.addInstance("vehicle");

		Generic power = vehicle.addAttribute("power");
		Generic car = engine.addInstance(vehicle, "car");

		ObservableList<Generic> carObservableAttributes = car.getObservableAttributes();

		Thread.sleep(100);

		assert carObservableAttributes.contains(power);

		Generic color = engine.addAttribute("color");
		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);

		Thread.sleep(100);

		assert carObservableAttributes.contains(color);
		assert carObservableAttributes.contains(vehicleColor);
	}

	public void test_relationTest10() throws InterruptedException, ExecutionException, TimeoutException {
		CocClientEngine engine = new CocClientEngine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color);

		Generic car = engine.addInstance(vehicle, "Car");
		Generic colorMat = engine.addInstance(color, "ColorMat");
		Generic carColorMat = car.addAttribute(vehicleColor, "carColorMat", colorMat);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		myVehicle.addHolder(vehicleColor, "myVehicleRed", red);

		Generic myCar = car.addInstance("myCar");
		Generic redMat = colorMat.addInstance("redMat");
		myCar.addHolder(carColorMat, "myCarRedMat", redMat);

		Generic vehiclePower = vehicle.addAttribute("power");
		ObservableList<Generic> myVehicleObservableHolders = myVehicle.getObservableHolders(vehiclePower);
		myVehicleObservableHolders.contains(null);// force compute
		Generic myVehicle125 = myVehicle.addHolder(vehiclePower, "125");

		Thread.sleep(100);

		assert myVehicleObservableHolders.contains(myVehicle125) : myVehicle.getHolders(vehiclePower).info();
		assert myVehicle125.getValue().equals("125");
		assert myVehicle.getAsyncValues(vehiclePower).get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT).contains("125") : myVehicle.getHolders(vehiclePower).info();
		assert myVehicleObservableHolders.size() == 1;

		assert myCar.getAsyncHolders(vehiclePower).get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT).size() == 0;

	}

	// --------------------------

	public void test_holderTest1() throws InterruptedException, ExecutionException, TimeoutException {
		CocClientEngine root = new CocClientEngine();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic car = root.addInstance(Arrays.asList(vehicle), "Car");

		ObservableList<Generic> vehicleObservableHolders = vehicle.getObservableHolders(power);
		vehicleObservableHolders.size();
		ObservableList<Generic> powerObservableInstances = power.getObservableInstances();
		powerObservableInstances.size();
		ObservableValue<Generic> powerObservableInstance = power.getObservableInstance();
		powerObservableInstance.getValue();
		ObservableList<Generic> carObservableHolders = car.getObservableHolders(power);
		carObservableHolders.size();

		Thread.sleep(100);

		int powerValue = 1;
		Generic v1 = power.addInstance(powerValue, vehicle);
		assert v1.isInstanceOf(power);
		assert vehicleObservableHolders.size() == 1 : vehicle.getHolders(power);
		assert vehicleObservableHolders.contains(v1) : vehicle.getHolders(power);
		assert carObservableHolders.size() == 1 : vehicle.getHolders(power);
		assert carObservableHolders.contains(v1) : vehicle.getHolders(power);
		assert powerObservableInstances.size() == 1;
		assert powerObservableInstances.contains(v1);
		assert powerObservableInstance.getValue().equals(v1);
		assert power.isAsyncAlive().get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT);
		assert v1.isAsyncAlive().get(Statics.SERVER_TIMEOUT, Statics.SERVER_TIMEOUT_UNIT);
	}

	public void test_IteratorAndRemoveTest1() throws InterruptedException, ExecutionException, TimeoutException {
		Generic root = new CocClientEngine();
		Generic car = root.addInstance("Car");
		ObservableList<Generic> myCars = car.getObservableInstances();

		myCars.size();

		car.addInstance("myFirstCar");
		car.addInstance("mySecondCar");
		Generic myThirdCar = car.addInstance("myThirdCar");
		car.addInstance("myFourthCar");

		Iterator<Generic> iterator = myCars.iterator();
		int cpt = 0;
		myThirdCar.remove();
		Thread.sleep(100);
		while (iterator.hasNext()) {
			iterator.next();
			cpt++;
		}
		assert cpt == 3;
	}

	public void test_getInstanceTest13() throws InterruptedException, ExecutionException, TimeoutException {
		CocClientEngine root = new CocClientEngine();
		Generic vehicle = root.addInstance("Vehicle");
		Generic vehiclePower = vehicle.addAttribute("power");

		ObservableList<Generic> vehiclePowerObservableInstances = vehiclePower.getObservableInstances(116);
		vehiclePowerObservableInstances.size();

		Generic car = root.addInstance(vehicle, "Car");

		Generic myBmw = car.addInstance("myBmw");
		Generic myAudi = car.addInstance("myAudi");
		vehiclePower.addInstance(115, myBmw);
		vehiclePower.addInstance(116, myBmw);
		vehiclePower.addInstance(116, myAudi);

		assert vehiclePowerObservableInstances.size() == 2;
	}

	public void test_getInstanceTest14() throws InterruptedException, ExecutionException, TimeoutException {
		CocClientEngine root = new CocClientEngine();
		Generic vehicle = root.addInstance("Vehicle");
		Generic color = root.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);
		Generic car = root.addInstance(vehicle, "Car");

		ObservableList<Generic> vehicleColorObservableInstances = vehicleColor.getObservableInstances("");
		vehicleColorObservableInstances.size();

		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		Generic myBmw = car.addInstance("myBmw");
		Generic myAudi = car.addInstance("myAudi");
		vehicleColor.addInstance("", myBmw, red);
		vehicleColor.addInstance("", myAudi, red);
		vehicleColor.addInstance("", myBmw, blue);

		Thread.sleep(100);

		assert vehicleColorObservableInstances.size() == 3;
	}

	public void test_getInstanceTest15() throws InterruptedException, ExecutionException, TimeoutException {
		CocClientEngine root = new CocClientEngine();
		Generic tree = root.addInstance("Tree");

		ObservableList<Generic> children2ObservableInstances = tree.getObservableInstances("children2");
		children2ObservableInstances.size();

		Generic father = tree.addInstance("father");
		Generic mother = tree.addInstance("mother");
		Generic children1 = tree.addInstance(Arrays.asList(father, mother), "children1");
		Generic children2 = tree.addInstance(Arrays.asList(father, mother), "children2");
		Generic children3 = tree.addInstance(children1, "children2");

		Thread.sleep(100);

		assert children2ObservableInstances.size() == 2;
		assert children2ObservableInstances.containsAll(Arrays.asList(children2, children3));
	}

	public void test_getInstanceTest16() throws InterruptedException, ExecutionException, TimeoutException {
		CocClientEngine root = new CocClientEngine();
		Generic vehicle = root.addInstance("Vehicle");
		Generic color = root.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);
		Generic car = root.addInstance(vehicle, "Car");

		Generic myBmw = car.addInstance("myBmw");
		Generic myAudi = car.addInstance("myAudi");
		ObservableList<Generic> myBmwObservableInstances = vehicleColor.getObservableInstances("", myBmw);
		myBmwObservableInstances.size();

		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		ObservableList<Generic> myBmwRedObservableInstances = vehicleColor.getObservableInstances("", myBmw, red);
		myBmwObservableInstances.size();

		vehicleColor.addInstance("", myBmw, red);
		vehicleColor.addInstance("", myAudi, red);
		vehicleColor.addInstance("", myBmw, blue);

		Thread.sleep(100);

		assert myBmwObservableInstances.size() == 2;
		assert myBmwRedObservableInstances.size() == 1;
	}

	public void test_getInstanceTest17() throws InterruptedException, ExecutionException, TimeoutException {
		CocClientEngine root = new CocClientEngine();
		Generic vehicle = root.addInstance("Vehicle");
		Generic color = root.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);
		Generic car = root.addInstance(vehicle, "Car");

		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		Generic myBmw = car.addInstance("myBmw");
		Generic myAudi = car.addInstance("myAudi");

		ObservableList<Generic> blueObservableInstances = vehicleColor.getObservableInstances(blue);
		blueObservableInstances.size();
		ObservableList<Generic> myBmwObservableInstances = vehicleColor.getObservableInstances(myBmw);
		myBmwObservableInstances.size();

		vehicleColor.addInstance("", myBmw, red);
		vehicleColor.addInstance("", myAudi, red);
		vehicleColor.addInstance("", myBmw, blue);

		Thread.sleep(100);

		assert myBmwObservableInstances.size() == 2;
		assert blueObservableInstances.size() == 1;
	}

	// public void test_getInstanceTest20() throws InterruptedException {
	// CocClientEngine root = new CocClientEngine();
	// Generic vehicle = root.addInstance("Vehicle");
	// Generic myBmw = vehicle.addInstance("myBmw");
	// ObservableList<Generic> myBmwObservableSubInstancesByVehicle = vehicle.getObservableSubInstances("myBmw");
	// myBmwObservableSubInstancesByVehicle.size();
	//
	// vehicle.addInstance("myAudi");
	// Generic car = root.addInstance(vehicle, "Car");
	// ObservableList<Generic> myBmwObservableSubInstancesByCar = car.getObservableSubInstances("myBmw");
	// myBmwObservableSubInstancesByCar.size();
	//
	// Generic myBmwCar = car.addInstance("myBmw");
	// car.addInstance("myAudi");
	//
	// Thread.sleep(100);
	//
	// myBmwObservableSubInstancesByVehicle.size();
	// myBmwObservableSubInstancesByCar.size();
	//
	// System.out.println("myBmwObservableSubInstancesByVehicle " + myBmwObservableSubInstancesByVehicle);
	// System.out.println("myBmwObservableSubInstancesByCar " + myBmwObservableSubInstancesByCar);
	//
	// assert myBmwObservableSubInstancesByVehicle.containsAll(Arrays.asList(myBmw, myBmwCar)) : vehicle.getSubInstances("myBmw").info();
	// assert myBmwObservableSubInstancesByCar.get(0) == myBmwCar;
	// }

	//

	public void test_BindingServiceTest3() throws InterruptedException, ExecutionException, TimeoutException {
		Generic engine = new CocClientEngine();
		ObservableList<Generic> engineSubInheritings = engine.getObservableSubInheritings();
		engineSubInheritings.size();

		// Generic animal = engine.addInstance("Animal");// Alone type
		// ObservableList<Generic> animalSubInheritings = animal.getObservableSubInheritings();
		// animalSubInheritings.size();
		//
		// Generic machine = engine.addInstance("Machine");
		// ObservableList<Generic> machineSubInheritings = machine.getObservableSubInheritings();
		// machineSubInheritings.size();
		//
		// Generic vehicle = engine.addInstance(machine, "Vehicle");
		// ObservableList<Generic> vehicleSubInheritings = vehicle.getObservableSubInheritings();
		// vehicleSubInheritings.size();
		//
		// Generic robot = engine.addInstance(machine, "Robot");
		// ObservableList<Generic> robotSubInheritings = robot.getObservableSubInheritings();
		// robotSubInheritings.size();
		//
		// Generic car = engine.addInstance(vehicle, "Car");
		// ObservableList<Generic> carSubInheritings = car.getObservableSubInheritings();
		// carSubInheritings.size();
		//
		// Generic bike = engine.addInstance(vehicle, "Bike");
		// ObservableList<Generic> bikeSubInheritings = bike.getObservableSubInheritings();
		// bikeSubInheritings.size();
		//
		// Generic transformer = engine.addInstance(Arrays.asList(robot, car), "Transformer");
		// ObservableList<Generic> transformerSubInheritings = transformer.getObservableSubInheritings();
		// transformerSubInheritings.size();
		//
		// Generic plasticTransformer = engine.addInstance(transformer, "PlasticTransformer");
		// ObservableList<Generic> plasticTransformerSubInheritings = plasticTransformer.getObservableSubInheritings();
		// plasticTransformerSubInheritings.size();

		Thread.sleep(500);

		engineSubInheritings.size();

		System.out.println("-> engineObservableSubInheritings " + engineSubInheritings);
		System.out.println("-> engineInheritings " + engine.getSubInheritings().toList());

		// System.out.println("engineSubInheritings " + engineSubInheritings);
		// System.out.println("engine.getSubInheritings() " + engine.getSubInheritings().toList());

		assert engine.getSubInheritings().toList().equals(engineSubInheritings);
		// assert animal.getSubInheritings().toList().equals(animalSubInheritings);
		// assert machine.getSubInheritings().toList().equals(machineSubInheritings);
		// assert vehicle.getSubInheritings().toList().equals(vehicleSubInheritings);
		// assert robot.getSubInheritings().toList().equals(robotSubInheritings);
		// assert car.getSubInheritings().toList().equals(carSubInheritings);
		// assert bike.getSubInheritings().toList().equals(bikeSubInheritings);
		// assert transformer.getSubInheritings().toList().equals(transformerSubInheritings);
		// assert plasticTransformer.getSubInheritings().toList().equals(plasticTransformerSubInheritings);
		//
		// assert !machineSubInheritings.contains(animal) : machine.getSubInheritings().info();
		// assert machineSubInheritings.containsAll(Arrays.asList(machine, vehicle, robot, car, bike, transformer, plasticTransformer)) : machine.getSubInheritings().info();
		// assert machineSubInheritings.size() == 7 : machine.getSubInheritings().info();
	}
}
