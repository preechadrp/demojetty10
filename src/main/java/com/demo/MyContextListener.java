package com.demo;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class MyContextListener implements ServletContextListener {

	public MyContextListener() {
		System.out.println("MyContextListener -- constructor");
	}
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.err.printf("MyContextListener.contextInitialized(%s)%n", sce);
		// sce.getServletContext().addListener(new MyRequestListener());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.err.printf("MyContextListener.contextDestroyed(%s)%n", sce);
	}
}