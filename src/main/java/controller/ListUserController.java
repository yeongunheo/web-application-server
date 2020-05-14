package controller;

import java.util.Collection;
import java.util.Map;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.HttpSession;

public class ListUserController extends AbstractController {
	
	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
		if (!isLogined(request.getSession())) {
			response.sendRedirect("/user/login.html");
			return;
		}
		
		Collection<User> users = DataBase.findAll();
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		for (User user : users) {
			sb.append("<tr>");
			sb.append("<td>" + user.getUserId() + "</td>");
			sb.append("<td>" + user.getName() + "</td>");
			sb.append("<td>" + user.getEmail() + "</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		response.forwardBody(sb.toString());
	}
	
	private static boolean isLogined(HttpSession session) {
		Object user = session.getAttribute("user");
		if (user == null) {
			return false;
		}
		return true;
	}
}
