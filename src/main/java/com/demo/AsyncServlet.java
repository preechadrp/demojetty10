package com.demo;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@WebServlet("/api/async")  //ไม่สามารถใช้ได้กับ annotation นี้ต้องเขียน Add เองใน .java , test 21/9/66  
public class AsyncServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6239150838611944574L;

	private static final String HEAVY_RESOURCE = "This is some heavy resource that will be served in an async way";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		System.out.println("call /api/async");

		ByteBuffer content = ByteBuffer.wrap(HEAVY_RESOURCE.getBytes(StandardCharsets.UTF_8));

		AsyncContext async = request.startAsync();

		ServletOutputStream out = response.getOutputStream();
			
		out.setWriteListener(new WriteListener() {
			@Override
			public void onWritePossible() throws IOException {
				while (out.isReady()) {
					if (!content.hasRemaining()) {
						response.setStatus(200);
						async.complete();
						return;
					}
					out.write(content.get());
				}
			}

			@Override
			public void onError(Throwable t) {
				getServletContext().log("Async Error", t);
				async.complete();
			}
		});
		
		//out.close(); //ห้าม close ไม่งั้นจะไม่ส่งออกผลลัพท์

	}
}