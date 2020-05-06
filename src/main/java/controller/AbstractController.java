package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.RequestLine.HttpMethod;

public abstract class AbstractController implements Controller {
	@Override
	public void service(HttpRequest request, HttpResponse response) {
		HttpMethod method = request.getMethod();
		
		if(method.isPost()) {
			doPost(request, response);
		} else {
			doGet(request, response);
		}
	}
	
	public void doPost(HttpRequest request, HttpResponse response) {
	}

	public void doGet(HttpRequest request, HttpResponse response) {
	}
}
