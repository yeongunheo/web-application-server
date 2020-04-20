package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;

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
        	String url = tokens[1];
        	while (!"".equals(line)) {
        		line = br.readLine();
        		log.debug("header: {}", line);
        	}

        	// 요구사항 2번
        	String data;
        	String[] datas;
        	
        	if (url.contains("/user/create?")) {
        		data = url.substring(13, url.length());
        		log.debug("String: {}", data);
        		
        		datas = data.split("&"); 
        		for(int i=0;i<datas.length;i++) {
        			log.debug("Datas: {}", datas[i]);
        		}
        		
        		User u = new User(
        				datas[0].split("=")[1],
        				datas[1].split("=")[1],
        				datas[2].split("=")[1],
        				datas[3].split("=")[1]
        			);
        		
        		log.debug("UserId: {}", u.getUserId());
        		log.debug("Password: {}", u.getPassword());
        		log.debug("Name: {}", u.getName());
        		log.debug("Email: {}", u.getEmail());
        		
        	}
        	else {
        	
        	DataOutputStream dos = new DataOutputStream(out);
        	File file = new File("./webapp" + url);
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
