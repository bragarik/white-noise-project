package br.com.rbraga.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.rbraga.service.AudioPlayerClip;

@WebServlet("/Actions/*")
public class Actions extends HttpServlet {

	private static final long serialVersionUID = -5927508004561607498L;
	private final Logger logger = LoggerFactory.getLogger(Actions.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String pathInfo = request.getPathInfo();

		// Verifica se existe um caminho de contexto
		if (pathInfo != null && pathInfo.length() > 1) {
			String acao = pathInfo.substring(1); // Remove a barra inicial para obter a variável

			logger.info(pathInfo);

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			if ("volumeUp".equalsIgnoreCase(acao)) {
				AudioPlayerClip.turnUpVolume();
			} else if ("volumeDown".equalsIgnoreCase(acao)) {
				AudioPlayerClip.turnDownVolume();
			} else if ("volume".equalsIgnoreCase(acao)) {
				String volume = request.getParameter("volume");
				if (volume != null)
					AudioPlayerClip.setVolume((Double.parseDouble(volume)));
			} else if ("play".equalsIgnoreCase(acao)) {
				AudioPlayerClip.play();
			} else if ("stop".equalsIgnoreCase(acao)) {
				AudioPlayerClip.pause();
			} else if ("status".equalsIgnoreCase(acao)) {
				// Não precissa fazer nada
			} else {
				// Caso nenhum caminho de contexto seja especificado
				response.getWriter().println("Nenhum caminho de contexto foi fornecido.");
			}

			JSONObject json = new JSONObject();
			if (AudioPlayerClip.status()) {
				json.put("statusMessage", "Reproduzindo");
				json.put("status", true);
			} else {
				json.put("statusMessage", "Parado");
				json.put("status", false);
			}
			json.put("volume", AudioPlayerClip.getGain());
			response.getWriter().write(json.toString());

		} else {
			// Caso nenhum caminho de contexto seja especificado
			response.getWriter().println("Nenhum caminho de contexto foi fornecido.");
		}

		boolean isDevelopment = AudioPlayerClip.isDevelopmentEnvironment()
				&& isOriginAllowed(request.getHeader("Origin"));

		if (isDevelopment) {
			response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
			response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
			response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	private boolean isOriginAllowed(String header) {
		return header != null && "http://localhost:3000".contains(header);
	}
}
