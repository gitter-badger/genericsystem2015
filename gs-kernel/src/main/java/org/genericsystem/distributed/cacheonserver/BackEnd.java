package org.genericsystem.distributed.cacheonserver;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import javafx.event.ActionEvent;

import org.genericsystem.distributed.AbstractBackEnd;
import org.genericsystem.distributed.GSBuffer;
import org.genericsystem.distributed.GSDeploymentOptions;
import org.genericsystem.distributed.WebSocketsServer;
import org.genericsystem.distributed.cacheonserver.todomvc.HtmlAdmin;
import org.genericsystem.distributed.cacheonserver.todomvc.TodoList;
import org.genericsystem.distributed.ui.HtmlNode;
import org.genericsystem.distributed.ui.HtmlNode.HtmlNodeCheckBox;
import org.genericsystem.kernel.BasicEngine;

public class BackEnd extends AbstractBackEnd<BasicEngine> {

	public static void main(String[] args) {
		new BackEnd(new GSDeploymentOptions()).start();
	}

	public BackEnd(GSDeploymentOptions options) {
		super(options);
	}

	@Override
	protected BasicEngine buildRoot(String value, String persistentDirectoryPath, Class[] userClasses) {
		return new BasicEngine(value, persistentDirectoryPath, userClasses);
	}

	@Override
	protected WebSocketsServer<BasicEngine> buildWebSocketsServer(GSDeploymentOptions options) {
		return new WebSocketsServer<BasicEngine>(this, options.getHost(), options.getPort()) {
			@Override
			public Handler<Buffer> getHandler(BasicEngine root, ServerWebSocket socket) {
				TodoList todolist = new TodoList();
				HtmlNode parent = new HtmlNode(socket);
				HtmlAdmin jsAdmin = new HtmlAdmin(todolist, parent, socket);
				return buffer -> {
					GSBuffer gsBuffer = new GSBuffer(buffer);
					String message = gsBuffer.getString(0, gsBuffer.length());
					JsonObject obj = new JsonObject(message);
					HtmlNode node = jsAdmin.getRootViewContext().getNodeById().get(obj.getString("nodeId"));
					if (node != null) {
						if (obj.getString("msg_type").equals("A"))
							node.getActionProperty().get().handle(new ActionEvent());

						if (obj.getString("msg_type").equals("U")) {
							if (obj.getString("eltType").equals("text"))
								node.getText().setValue(obj.getString("textContent"));

							if ("checkbox".equals(obj.getString("eltType")))
								((HtmlNodeCheckBox) node).getChecked().setValue(obj.getBoolean("checked"));
						}
					}
				};
			};
		};
	}
}