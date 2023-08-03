package br.com.rbraga.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import br.com.rbraga.service.AudioPlayerClip;

@WebServlet("/Actions/*")
public class Actions extends HttpServlet {

	private static final long serialVersionUID = -5927508004561607498L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		boolean isDevelopment = AudioPlayerClip.isDevelopmentEnvironment()
				&& isOriginAllowed(request.getHeader("Origin"));

		String pathInfo = request.getPathInfo();

		if (isDevelopment)
			System.out.println("- Init doPost -");

		// Verifica se existe um caminho de contexto
		if (pathInfo != null && pathInfo.length() > 1) {
			String acao = pathInfo.substring(1); // Remove a barra inicial para obter a variável

			if (isDevelopment)
				System.out.println(acao);

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			if ("volumeUp".equalsIgnoreCase(acao)) {
				AudioPlayerClip.turnUpVolume();
			} else if ("volumeDown".equalsIgnoreCase(acao)) {
				AudioPlayerClip.turnDownVolume();
			} else if ("volume".equalsIgnoreCase(acao)) {
				String volume = request.getParameter("volume");
				if (volume != null) {
					AudioPlayerClip.setVolume((Double.parseDouble(volume)));
					if (isDevelopment)
						System.out.println(volume);
				}
			} else if ("play".equalsIgnoreCase(acao)) {
				AudioPlayerClip.play();
			} else if ("stop".equalsIgnoreCase(acao)) {
				AudioPlayerClip.pause();
			} else if ("timer".equalsIgnoreCase(acao)) {
				boolean on = request.getParameter("on").equals("true");
				String timer = request.getParameter("timer");
				if (on)
					AudioPlayerClip.setTimer(Integer.parseInt(timer));
				else
					AudioPlayerClip.stopTimer();
				if (isDevelopment)
					System.out.println(on + " " + timer);
			} else if ("fadeOut".equalsIgnoreCase(acao)) {
				String fadeOut = request.getParameter("fadeOut");
				AudioPlayerClip.setFadeOut(fadeOut.equals("true"));
				if (isDevelopment)
					System.out.println(fadeOut);
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

			JSONObject timerJson = new JSONObject();
			timerJson.put("on", AudioPlayerClip.isTimerRunning());
			timerJson.put("remainingSeconds", AudioPlayerClip.getTimerRemainingSeconds());

			json.put("timer", timerJson);
			json.put("volume", AudioPlayerClip.getGain());
			json.put("fadeOut", AudioPlayerClip.isFadeOut());
			response.getWriter().write(json.toString());

		} else {
			// Caso nenhum caminho de contexto seja especificado
			response.getWriter().println("Nenhum caminho de contexto foi fornecido.");
		}

		if (isDevelopment) {
			response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
			response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
			response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		}

		if (isDevelopment)
			System.out.println("- End doPost -");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	private boolean isOriginAllowed(String header) {
		return header != null && "http://localhost:3000".contains(header);
	}
}
