 package com.demo;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class JettyByServletHandler {
  
    public static void main(String[] args) {
        try {
            startServletHandler();    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void startServletHandler() throws Exception {

		int maxThreads = 100;
		int minThreads = 10;
		int idleTimeout = 120;

		var threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

		Server server = new Server(threadPool);
		var connector = new ServerConnector(server);
		connector.setPort(8090);
		server.setConnectors(new Connector[] { connector });

		// ===== แบบใช้ ServletHandler
		var servletHandler = new ServletHandler();

		server.setHandler(servletHandler);

		// === เพิ่ม servlet
		// แบบธรรมดา , link test =  http://localhost:8090/status
		servletHandler.addServletWithMapping(BlockingServlet.class, "/api/status");
		// แบบ Asyncronouse , link test = http://localhost:8090/heavy/async
		servletHandler.addServletWithMapping(AsyncServlet.class, "/api/async");

		// สร้าง ServletHolder และกำหนดค่าต่าง ๆ
		org.eclipse.jetty.servlet.ServletHolder servletHolder = new org.eclipse.jetty.servlet.ServletHolder(
				BlockingServlet.class);
		servletHolder.setInitParameter("exampleParam", "Hello Jetty");
		// เพิ่ม ServletHolder เข้าไปใน ContextHandler
		// link test = http://localhost:8090/hellostatus
		servletHandler.addServletWithMapping(servletHolder, "/hellostatus");

		// ทดสอบเพิ่ม filter
		servletHandler.addFilterWithMapping(WebFilter01.class, "/api/*", EnumSet.of(DispatcherType.REQUEST));

		server.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				// ใช้เวลาหยุดเซิร์ฟเวอร์
				server.stop();
				System.out.println("Jetty server stopped gracefully");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));

		// join() is blocking until server is ready. It behaves like Thread.join() and
		// indeed calls join() of Jetty's thread pool.
		// Everything works without this because jetty starts very quickly.
		// However if your application is heavy enough the start might take some time.
		// Call of join() guarantees that after it the server is indeed ready.
		server.join();

	}

}
