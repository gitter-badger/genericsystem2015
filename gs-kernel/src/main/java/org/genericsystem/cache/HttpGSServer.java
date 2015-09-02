package org.genericsystem.cache;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.genericsystem.api.core.exceptions.ConcurrencyControlException;
import org.genericsystem.api.core.exceptions.OptimisticLockConstraintViolationException;
import org.genericsystem.kernel.Root;

public abstract class HttpGSServer extends AbstractGSServer {

	private List<HttpServer> httpServers = new ArrayList<>();

	@Override
	public void start() {
		super.start();
		Vertx vertx = Vertx.vertx();
		for (int i = 0; i < 2 * Runtime.getRuntime().availableProcessors(); i++) {
			HttpServer httpServer = vertx.createHttpServer(new HttpServerOptions().setPort(config().getInteger("port")));
			httpServer.requestHandler(request -> {
				String path = request.path();
				Root root = getRoots().get(path);
				if (root == null)
					throw new IllegalStateException("Unable to find database :" + path);
				request.exceptionHandler(e -> {
					e.printStackTrace();
					throw new IllegalStateException(e);
				});
				request.handler(getHandler(root, buffer -> {
					request.response().end(buffer);
					request.response().close();

				}, exception -> {
					int statusCode = 0;
					if (exception instanceof ConcurrencyControlException)
						statusCode = 400;
					else if (exception instanceof OptimisticLockConstraintViolationException)
						statusCode = 401;
					request.response().setStatusCode(statusCode).end();
					request.response().close();
				}));
			});

			BlockingQueue<AsyncResult<HttpServer>> blockingQueue = new ArrayBlockingQueue<>(1);
			httpServer.listen(res -> {
				try {
					blockingQueue.put(res);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			AsyncResult<HttpServer> res = null;
			try {
				res = blockingQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (res.failed())
				throw new IllegalStateException(res.cause());
			httpServers.add(httpServer);
		}
		System.out.println("Generic System server ready!");
	}

	@Override
	public void stop() {
		httpServers.forEach(httpServer -> {
			BlockingQueue<AsyncResult<Void>> blockingQueue = new ArrayBlockingQueue<>(1);
			httpServer.close(res -> {
				try {
					blockingQueue.put(res);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			AsyncResult<Void> res = null;
			try {
				res = blockingQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (res.failed())
				throw new IllegalStateException(res.cause());
		});
		// TODO Wait transaction timeout here
		super.stop();
		System.out.println("Generic System server stopped!");
	}
}
