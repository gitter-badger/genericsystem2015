"Run the module `gsmodule`."

shared void run() {
		
	Vertex metaType = Vertex(null,[],"Engine",[]);
	Vertex metaAttribute = metaType.addInheriting(null,metaType.content,[metaType]);
	Vertex metaRelation = metaAttribute.addInheriting(null,metaType.content,[metaType,metaType]);
	Vertex vehicle = metaType.addInstance( "Vehicle");
	Vertex myVehicle = vehicle.addInstance("myVehicle");
	Vertex car = metaType.addInheritingInstance([vehicle],"Car");
	Vertex myCar = car.addInstance( "myCar");
	Vertex yourCar = car.addInstance( "yourCar");
	Vertex color = metaType.addInstance("Color");
	Vertex red = color.addInstance("red");
	Vertex blue = color.addInstance( "blue");
	Vertex yellow = color.addInstance( "yellow");
	Vertex power = metaAttribute.addInstance( "Power", [vehicle]);
	Vertex v233 = power.addInstance( 233, [vehicle]);
	Vertex v250 = power.addInheritingInstance([v233], 250, [car]);
	Vertex v333 = power.addInheritingInstance([v250], 333, [myCar]);
	Vertex carColor = metaRelation.addInstance( "CarColor", [car,color]);
	Vertex carYellow = carColor.addInstance( "CarYellow", [car,yellow]);
	Vertex myCarYellow = carColor.addInheritingInstance([carYellow],"myCarYellow", [myCar,yellow]);
	Vertex myCarRed = carColor.addInstance("myCarRed", [myCar,red]);
	Vertex myCarBlue = carColor.addInstance( "myCarBlue", [myCar,blue]);
	Vertex bike = metaType.addInheritingInstance([vehicle], "Bike", []);
	Vertex myBike = bike.addInstance("myBike");
	Vertex robot = metaType.addInstance( "Robot");
	Vertex transformer = metaType.addInheritingInstance([car,robot], "Transformer");
	Vertex myTransformer = transformer.addInstance("myTransformer");
	Vertex tree=metaType.addInstance( "Tree", [null]);
	Vertex rootNode=tree.addInstance( "rootNode", [null]);
	Vertex node1=tree.addInstance("node1", [rootNode]);
	Vertex node2=tree.addInstance( "node2", [rootNode]);
	Vertex node3=tree.addInheritingInstance([node2], "inheritingNode3", [node2]);
	
	print(metaType.detailedInfo);
	print(metaAttribute.detailedInfo);
	print(metaRelation.detailedInfo);
	print(vehicle.detailedInfo);
	print(myVehicle.detailedInfo);
	print(car.detailedInfo);
	print(myCar.detailedInfo);
	print(bike.detailedInfo);
	print(myBike.detailedInfo);
	print(power.detailedInfo);
	print(v233.detailedInfo);
	print(v333.detailedInfo);
	print(color.detailedInfo);
	print(red.detailedInfo);
	print(yellow.detailedInfo);
	print(blue.detailedInfo);
	print(carColor.detailedInfo);
	print(myCarRed.detailedInfo);
	print(myCarBlue.detailedInfo);
	print(robot.detailedInfo);
	print(transformer.detailedInfo);
	print(myTransformer.detailedInfo);
	print(tree.detailedInfo);
	print(rootNode.detailedInfo);
	print(node1.detailedInfo);
	print(node2.detailedInfo);
	print(node3.detailedInfo);
	
	print(car.getInheritingComposites(power));
	print(vehicle.getInheritingComposites(power));
	print(myCar.getInheritingComposites(power));
	print(myVehicle.getInheritingComposites(power));
	print(transformer.getInheritingComposites(power));
	print(transformer.getInheritingComposites(metaType));
	
	print("*************************************************************");
	print("myCar "+myCar.getDesignatings(carColor).string);
	print("yourCar : "+yourCar.getDesignatings(carColor).string);
	print("yourCar : "+yourCar.getDesignatings(carColor).string);
	print("blue : "+blue.getDesignatings(carColor).string);
	print("red : "+red.getDesignatings(carColor).string);
	print("yellow : "+yellow.getDesignatings(carColor).string);
	print("*************************************************************");
	print(myCar.getInheritingComposites(power));
	print(myCar.getDesignatings(power));
	print(yourCar.getDesignatings(power));
	
	print("*************************************************************");
	print("myCar "+myCar.getInheritingComposites(carColor).string);
	print("yourCar : "+yourCar.getInheritingComposites(carColor).string);
	print("yourCar : "+yourCar.getInheritingComposites(carColor).string);
	print("blue : "+blue.getInheritingComposites(carColor).string);
	print("red : "+red.getInheritingComposites(carColor).string);
	print("yellow : "+yellow.getInheritingComposites(carColor).string);
	print("*************************************************************");
	
}