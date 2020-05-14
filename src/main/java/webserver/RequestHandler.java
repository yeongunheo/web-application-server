package webserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.Controller;
import db.DataBase;
import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        	HttpRequest request = new HttpRequest(in);
        	HttpResponse response = new HttpResponse(out);
        	
        	if (request.getCookies().getCookie("JSESSIONID") == null) {
        		response.addHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
        	}
        	
        	if (getSessionId(request.getHeader("Cookie")) == null) {
        		response.addHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
        	}
        	
        	Controller controller = RequestMapping.getController(request.getPath());
        	if (controller == null) {
        		String path = getDefaultPath(request.getPath());
        		response.forward(path);
        	} else {
        		controller.service(request, response);
        	}
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private String getSessionId(String cookieValue) {
    	Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
    	return cookies.get("JSESSIONID");
	}

	private String getDefaultPath(String path) {
    	if (path.equals("/")) {
    		return "/index.html";
    	}
    	return path;
    }
    
}