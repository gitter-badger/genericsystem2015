package org.genericsystem.servercache;

import org.genericsystem.api.core.exceptions.AliveConstraintViolationException;
import org.genericsystem.api.core.exceptions.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.kernel.Generic;
import org.genericsystem.kernel.ServerEngine;
import org.testng.annotations.Test;

@Test
public class NotRemovableTest extends AbstractTest {

	public void test001_aliveEx() {
		ServerEngine engine = new ServerEngine();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");

		myBmwRed.remove();
		catchAndCheckCause(() -> myBmwRed.remove(), AliveConstraintViolationException.class);

	}

	public void test002_referenceEx() {
		ServerEngine engine = new ServerEngine();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");

		catchAndCheckCause(() -> car.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test003_referenceEx() {
		ServerEngine engine = new ServerEngine();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");

		catchAndCheckCause(() -> color.remove(), ReferentialIntegrityConstraintViolationException.class);
	}
}