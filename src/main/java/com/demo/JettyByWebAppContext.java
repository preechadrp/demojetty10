package com.demo;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyByWebAppContext {

	public static void main(String[] args) throws Exception {

		//by preecha.d 29/6/68

		int appPort = 8090;
		int maxThreads = 100;
		int minThreads = 10;
		int idleTimeout = 120;

		var threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

		Server server = new Server(threadPool);

		// ==== add connector
		var connector = new ServerConnector(server);
		connector.setPort(appPort);
		server.addConnector(connector);

		// ==== แบบใช้ WebAppContext ต้องเพิ่ม lib = jetty-webapp
		var webapp = new WebAppContext();
		webapp.setContextPath("/"); // อยู่ใน root เลย

		java.net.URL webResource = JettyByWebAppContext.class.getResource("/webapp/");
		System.out.println("webResource : " + webResource.toString());
		System.out.println("toExternalForm : " + webResource.toExternalForm());
		//รันด้วย .jar 
		//webResource : jar:file:/D:/javaDemo1/demojetty10/target/demojetty10-0.0.1.jar!/webapp/
		//toExternalForm : jar:file:/D:/javaDemo1/demojetty10/target/demojetty10-0.0.1.jar!/webapp/
		//
		//รันด้วย IDE 
		//webResource : file:/D:/javaDemo1/demojetty10/target/classes/webapp/
		//toExternalForm : file:/D:/javaDemo1/demojetty10/target/classes/webapp/
		webapp.setWarResource(Resource.newResource(webResource));
	 
		// เพิ่ม servlet
		webapp.addServlet(BlockingServlet.class, "/api/status");// link : http://localhost:8090/api/status
		webapp.addServlet(AsyncServlet.class, "/api/async");// link : http://localhost:8090/api/async

		// เพิ่ม ServletHolder ของ zk framework แทนการใช้ web.xml
		ServletHolder zkLoaderHolder = new ServletHolder(org.zkoss.zk.ui.http.DHtmlLayoutServlet.class);
		zkLoaderHolder.setInitParameter("update-uri", "/zkau");
		zkLoaderHolder.setInitOrder(1);
		webapp.addServlet(zkLoaderHolder, "*.zul");

		webapp.addServlet(org.zkoss.zk.au.http.DHtmlUpdateServlet.class, "/zkau/*");

		// เพิ่ม filter
		webapp.addFilter(WebFilter01.class, "/api/*", EnumSet.of(DispatcherType.REQUEST));

		// เพิ่ม Listener
		MyContextListener contextListener = new MyContextListener();
		webapp.addEventListener(contextListener);

		// Home Page
		webapp.addServlet(IndexServlet.class, "");

		// เพิ่มเข้า handlers
		HandlerList handlers = new HandlerList();
		handlers.addHandler(webapp);

		server.setHandler(handlers);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				// ใช้เวลาหยุดเซิร์ฟเวอร์
				server.stop();
				System.out.println("Jetty server stopped gracefully");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));

		server.start();
		server.join();

	}

}
