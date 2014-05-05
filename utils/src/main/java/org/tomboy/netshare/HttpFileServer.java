package org.tomboy.netshare;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HttpFileServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		server.setHandler(context);

		context.addServlet(DownloadFileServlet.class, "/d");
		ServletHolder holder = context
				.addServlet(UploadFileServlet.class, "/u");
		holder.getRegistration().setMultipartConfig(
				new MultipartConfigElement("C:/temp/upload"));

		server.start();
		server.join();
	}

	@SuppressWarnings("serial")
	public static class DownloadFileServlet extends HttpServlet {

		@Override
		protected void doPost(HttpServletRequest request,
				HttpServletResponse response) throws ServletException,
				IOException {
			String fileName = request.getParameter("f");
			if (fileName == null || fileName.equals("")) {
				throw new ServletException("File Name can't be empty !");
			}
			String password = request.getParameter("p");
			if (password == null || password.equals("")) {
				throw new ServletException("Password can't be empty !");
			}
			File file = new File("c:\\Temp\\upload\\README.md");
			if (!file.exists()) {
				throw new ServletException("File doesn't exists.");
			}

			ServletContext ctx = getServletContext();
			InputStream fis = new FileInputStream(file);
			String mimeType = ctx.getMimeType(file.getAbsolutePath());
			response.setContentType(mimeType != null ? mimeType
					: "application/octet-stream");
			response.setContentLength((int) file.length());
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "\"");

			ServletOutputStream os = response.getOutputStream();
			byte[] buffer = new byte[1024];
			int read = 0;
			while ((read = fis.read(buffer)) != -1) {
				os.write(buffer, 0, read);
			}
			os.flush();
			os.close();
			fis.close();

			System.out.println("File downloaded successfully !");
		}

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			String fileName = req.getParameter("f");
			if (fileName == null || fileName.equals("")) {
				throw new ServletException("File Name can't be null or empty !");
			}

			resp.setContentType("text/html");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println(
					"<form action=\"d\" method=\"post\">"
							+ "<input type=\"hidden\" name=\"f\" value=\""
							+ fileName + "\">"
							+ "Enter password:<input name=\"p\">" + "<br>"
							+ "<input type=\"submit\">" + "</form>");
			// TODO invoke doPost if password is not specified
		}

	}

	@SuppressWarnings("serial")
	// @MultipartConfig(fileSizeThreshold = 1024 * 1024 * 30)
	public static class UploadFileServlet extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			resp.setContentType("text/html");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println(
					"<form action=\"u\" method=\"post\" enctype=\"multipart/form-data\">"
							+ "Select file:<input type=\"file\" name=\"f\">"
							+ "<br>"
							+ "Password (optional):<input name=\"p\">"
							+ "<br>"
							+ "<input type=\"submit\" value=\"Upload\">"
							+ "</form>");
		}

		@Override
		protected void doPost(HttpServletRequest request,
				HttpServletResponse response) throws ServletException,
				IOException {
			String saveTo = "C:\\Temp\\upload";

			// creates the save directory if it does not exists
			File saveToDir = new File(saveTo);
			if (saveToDir.exists() == false) {
				saveToDir.mkdirs();
			}
			System.out.println("Upload File Directory="
					+ saveToDir.getAbsolutePath());

			String fileName = null;
			String resp = " is uploaded successfully !";
			// Get all the parts from request and write it to the file on server
			for (Part part : request.getParts()) {
				fileName = getFileName(part);
				File filePath = new File(saveTo + File.separator + fileName);
				if (filePath.isFile()) {
					resp = " already exists, upload aborted !";
				} else {
					part.write(fileName);
				}
			}

			// TODO compress it if password provided

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().println("<h1>" + fileName + resp + "</h1>");
		}

		/**
		 * Utility method to get file name from HTTP header content-disposition
		 */
		private String getFileName(Part part) {
			String contentDisp = part.getHeader("content-disposition");
			System.out.println("content-disposition header= " + contentDisp);

			String[] tokens = contentDisp.split(";");
			for (String token : tokens) {
				if (token.trim().startsWith("filename")) {
					return token.substring(token.indexOf("=") + 2,
							token.length() - 1);
				}
			}

			return "";
		}
	}
}
