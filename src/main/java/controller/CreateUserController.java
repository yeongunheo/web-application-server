package controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.RequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserController extends AbstractController {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	
	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
		User user = new User(
				request.getParameter("userId"),
				request.getParameter("password"),
				request.getParameter("name"),
				request.getParameter("email"));
		log.debug("User : {}", user);
		DataBase.addUser(user);
		response.sendRedirect("/index.html");
	}
}
