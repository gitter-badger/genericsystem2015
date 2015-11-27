package org.genericsystem.todoApp;

import java.io.File;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.genericsystem.common.Generic;
import org.genericsystem.distributed.GSDeploymentOptions;
import org.genericsystem.distributed.cacheonclient.CocClientEngine;
import org.genericsystem.distributed.cacheonclient.CocServer;
import org.genericsystem.kernel.Statics;
import org.genericsystem.todoApp.binding.Binder;
import org.genericsystem.todoApp.binding.Binding;

public class GenericList {

	private static final String path = System.getenv("HOME") + "/test/ObservableListChain";

	private static CocServer server;
	private static CocClientEngine engine;
	private static ObservableList<Generic> dependenciesObservableList;

	public StringProperty name = new SimpleStringProperty();

	private ObservableList<GenericWrapper> genericList;

	private void cleanDirectory() {
		File file = new File(path);
		if (file.exists())
			for (File f : file.listFiles()) {
				if (!".lock".equals(f.getName()))
					f.delete();
			}
	}

	public GenericList() throws InterruptedException {

		// cleanDirectory();

		server = new CocServer(new GSDeploymentOptions(Statics.ENGINE_VALUE, Statics.DEFAULT_PORT, path));
		server.start();

		engine = new CocClientEngine(Statics.ENGINE_VALUE, null, Statics.DEFAULT_PORT);

		dependenciesObservableList = engine.getCurrentCache().getDependenciesObservableList(engine);

		Thread.sleep(1000);
		System.out.println(dependenciesObservableList);
		genericList = FXCollections.observableArrayList();
		genericList.addAll(dependenciesObservableList.stream().map(dep -> new GenericWrapper(dep)).collect(Collectors.toList()));

		System.out.println(genericList);
	}

	private void initGenericList() {
		genericList.clear();
		genericList.addAll(dependenciesObservableList.stream().map(dep -> new GenericWrapper(dep)).collect(Collectors.toList()));

	}

	public void flush() {
		engine.getCurrentCache().flush();
	}

	public void clear() {
		engine.getCurrentCache().clear();
		try {
			Thread.sleep(100);
			initGenericList();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// genericList.addAll(dependenciesObservableList.stream().map(dep -> new GenericWrapper(dep)).collect(Collectors.toList()));

	}

	public void mount() {
		engine.getCurrentCache().mount();
		initGenericList();
	}

	public void unmount() {
		engine.getCurrentCache().unmount();
		initGenericList();
	}

	public void create() {
		engine.addInstance(name.getValue());
		initGenericList();
	}

	public void remove(GenericWrapper genericWrapper) {
		genericWrapper.remove();
		initGenericList();
	}

	public ObservableList<GenericWrapper> getGenericList() {
		return genericList;
	}

	protected static class GenericWrapper {

		private Generic generic;
		public StringProperty name = new SimpleStringProperty();

		public GenericWrapper(Generic generic) {
			this.generic = generic;
			name.set(Objects.toString(this.generic.getValue()));
		}

		public void remove() {
			generic.remove();
		}

		public StringProperty getString() {
			return name;
		}
	}

	@SuppressWarnings("unused")
	public Node init() {
		Element genericsVBox = new Element(null, VBox.class, "");

		Element genericsHBox = new Element(genericsVBox, VBox.class, "", Binding.bindToMethod(GenericList.class, GenericList::getGenericList, Binder.foreach()));
		Element genericsLabel = new Element(genericsHBox, Label.class, "", Binding.bindToMethod(GenericWrapper.class, GenericWrapper::getString, Binder.textBind()));
		Element genericsRemoveButton = new Element(genericsHBox, Button.class, "remove", Binding.bindToMethod(GenericList.class, "remove", Binder.methodBind(), GenericWrapper.class));
		Element genericsCreatLabel = new Element(genericsVBox, TextField.class, "", Binding.bindToField(GenericList.class, "name", Binder.inputTextBind()));

		Element genericsHB = new Element(genericsVBox, HBox.class, "");
		Element genericsCreateButton = new Element(genericsHB, Button.class, "create", Binding.bindToMethod(GenericList.class, "create", Binder.methodBind()));
		Element genericsFlushButton = new Element(genericsHB, Button.class, "flush", Binding.bindToMethod(GenericList.class, "flush", Binder.methodBind()));
		Element genericsClearButton = new Element(genericsHB, Button.class, "clear", Binding.bindToMethod(GenericList.class, "clear", Binder.methodBind()));
		Element genericsMountButton = new Element(genericsHB, Button.class, "mount", Binding.bindToMethod(GenericList.class, "mount", Binder.methodBind()));
		Element genericsUnmountButton = new Element(genericsHB, Button.class, "unmount", Binding.bindToMethod(GenericList.class, "unmount", Binder.methodBind()));

		return genericsVBox.apply(this).getNode();
	}
}
