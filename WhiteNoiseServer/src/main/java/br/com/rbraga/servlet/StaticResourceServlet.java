package br.com.rbraga.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/*")
public class StaticResourceServlet extends HttpServlet {
	private static final long serialVersionUID = -6206005160347210624L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String resourcePath = request.getRequestURI().substring(request.getContextPath().length());

		if (resourcePath.isBlank() || resourcePath.equals("/"))
			resourcePath = "/index.html";

		String fullPath = getServletContext().getRealPath("/WEB-INF/app" + resourcePath);

		File file = new File(fullPath);
		if (file.exists() && file.isFile()) {
			String contentType = getServletContext().getMimeType(file.getName());
			response.setContentType(contentType);
			Files.copy(file.toPath(), response.getOutputStream());
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}