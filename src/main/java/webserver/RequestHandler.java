package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.CookieHandler;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

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
        	Reader reader = new InputStreamReader(in);
        	BufferedReader br = new BufferedReader(reader);
        	
        	String line = br.readLine();
        	if (line == null) { return; }
        	log.debug("request: {}", line);

        	String[] tokens = line.split(" ");
        	String httpMethod = tokens[0];
        	String httpURL = tokens[1];
        	String cookies = null;
        	int contentLength = 0;
        	
        	while (!"".equals(line)) {
        		line = br.readLine();
        		if(line.contains("Content-Length:")) contentLength = Integer.parseInt(line.split(" ")[1]);
        		if(line.contains("Cookie:")) cookies = line.split(" ")[1]; 
        		log.debug("header: {}", line);
        	}

        	// 요구사항 2번
        	if (httpURL.contains("/user/create?")) {
        		int idx = httpURL.indexOf("?");
        		String params = httpURL.substring(idx+1);
        		log.debug("params: {}", params);
        		
        		Map<String, String> datas = HttpRequestUtils.parseQueryString(params);
        		User u = new User(datas.get("userId"), datas.get("password"), datas.get("name"), datas.get("email"));
        		
        		log.debug("userId: {}", u.getUserId());
        		log.debug("password: {}", u.getPassword());
        		log.debug("name: {}", u.getName());
        		log.debug("email: {}", u.getEmail());
        		
        	}
        	// 요구사항 3번
        	if(httpURL.equals("/user/create")) {
            	String httpEntity = IOUtils.readData(br, contentLength);
        		log.debug("POST: {}", httpEntity);

        		Map<String, String> datas = HttpRequestUtils.parseQueryString(httpEntity);
        		User u = new User(datas.get("userId"), datas.get("password"), datas.get("name"), datas.get("email"));
        		
        		log.debug("userId: {}", u.getUserId());
        		log.debug("password: {}", u.getPassword());
        		log.debug("name: {}", u.getName());
        		log.debug("email: {}", u.getEmail());
        		
        		// 요구사항 4번
        		DataOutputStream dos = new DataOutputStream(out);
        		response302Header(dos, "/index.html");
        		
        		// 요구사항 5번
        		DataBase.addUser(u);
        	}
        	// 요구사항 5번
        	if(httpURL.equals("/user/login")) {
        		String httpEntity = IOUtils.readData(br, contentLength);
        		Map<String, String> datas = HttpRequestUtils.parseQueryString(httpEntity);
        		String userId = datas.get("userId");
        		String password = datas.get("password");
        		
        		User tempUser = DataBase.findUserById(userId);
        		if(tempUser == null) {
            		DataOutputStream dos = new DataOutputStream(out);
            		String url = "/user/login_failed.html";
            		responseLoginFailedHeader(dos, url);
        		}
        		if(tempUser != null) {
        			if (userId.equals(tempUser.getUserId()) && password.equals(tempUser.getPassword())) {
            			DataOutputStream dos = new DataOutputStream(out);
            			String url = "/index.html";
            			responseLoginSuccessHeader(dos, url);	
            		}
            		if (!(userId.equals(tempUser.getUserId()) && password.equals(tempUser.getPassword()))) {
            			DataOutputStream dos = new DataOutputStream(out);
            			String url = "/user/login_failed.html";
            			responseLoginFailedHeader(dos, url);
            		}
        		}
        	}
        	
        	if (!httpURL.contains("/user/create")) {
	        	DataOutputStream dos = new DataOutputStream(out);
	        	File file = new File("./webapp" + httpURL);
	        	byte[] body = Files.readAllBytes(file.toPath());
	            response200Header(dos, body.length);
	            responseBody(dos, body);
        	}
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response302Header(DataOutputStream dos, String url) {
    	try {
    		dos.writeBytes("HTTP/1.1 302 Found \r\n");
    		dos.writeBytes("Location: " + url + "\r\n");
    		dos.writeBytes("\r\n");
    	} catch (IOException e) {
    		log.error(e.getMessage());
    	}
    }

    private void responseLoginSuccessHeader(DataOutputStream dos, String url) {
    	try {
    		dos.writeBytes("HTTP/1.1 302 Found \r\n");
    		dos.writeBytes("Location: " + url + "\r\n");
    		dos.writeBytes("Set-Cookie: logined=true\r\n");
    		dos.writeBytes("\r\n");
    	} catch (IOException e) {
    		log.error(e.getMessage());
    	}
    }

    private void responseLoginFailedHeader(DataOutputStream dos, String url) {
    	try {
    		dos.writeBytes("HTTP/1.1 302 Found \r\n");
    		dos.writeBytes("Location: " + url + "\r\n");
    		dos.writeBytes("Set-Cookie: logined=false\r\n");
    		dos.writeBytes("\r\n");
    	} catch (IOException e) {
    		log.error(e.getMessage());
    	}
    }
    
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
}