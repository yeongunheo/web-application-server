package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
	private DataOutputStream dos;
	private Map<String, String> headers = new HashMap<String, String>();
	
	// 생성자 구현
	// 이말은 즉 HttpResponse 클래스를 실행하면 먼저 해야하는 작업들이 무엇인지 정의내리는 것이다.
	// *** 혹시 header를 호출할 때 Map객체에 담긴 모든 헤더를 추출하는 방식으로 구현하는 것은 아닐까?
	// 그렇다면 dos.write로 바로 내보내지말고 일단 Map에만 담아보자!!
	
	public HttpResponse(OutputStream out) {
		dos = new DataOutputStream(out);
	}
	
	// 메소드
	public void forward(String url) throws Exception { // 파일을 직접 읽은 뒤 내보내는 메소드
		try {
			File file = new File("./webapp" + url);
	    	byte[] body = Files.readAllBytes(file.toPath());
	    	if (url.endsWith(".css")) {
	    		headers.put("Content-Type", "text/css");
	    	} else if (url.endsWith(".js")) {
	    		headers.put("Content-Type", "application/javascript");
	    	} else {
	    		headers.put("Content-Type", "text/html;charset=utf-8");
	    	}
	    	headers.put("Content-Length", body.length + "");
	    	response200Header(body.length);
	    	responseBody(body);
    	} catch (IOException e) {
    		log.error(e.getMessage());
    	}
	}
	
	public void forwardBody(String body) {
		try {
			byte[] contents = body.getBytes();
			headers.put("Content-Type", "text/html;charset=utf-8");
			headers.put("Content-Length", contents.length + "");
			response200Header(contents.length);
	    	responseBody(contents);
		} catch (Exception e) {
    		log.error(e.getMessage());
    	}
	}
	
	public void sendRedirect(String redirectUrl){
		try {
    		dos.writeBytes("HTTP/1.1 302 Found \r\n");
    		processHeaders();
    		dos.writeBytes("Location: " + redirectUrl + " \r\n");
    		dos.writeBytes("\r\n");
    	} catch (IOException e) {
    		log.error(e.getMessage());
    	}
	}
	
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}
	
	private void response200Header(int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			processHeaders();
			dos.writeBytes("\r\n");		
		} catch (IOException e) {
            log.error(e.getMessage());
        }
	}
	
	private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");	
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

	private void processHeaders() {
		try {
			Set<String> keys = headers.keySet();
			for (String key : keys) {
				dos.writeBytes(key + ": " + headers.get(key) + " \r\n");
			}
		} catch (IOException e) {
    		log.error(e.getMessage());
    	}
	}
}
