# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

## 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
### 1.1 HTTP 프로토콜
> HTTP(Hypertext Transfer Protocol)는 인터넷상에서 데이터를 주고 받기 위한 *서버/클라이언트 모델* 을 따르는 프로토콜이다. 프로토콜이란 정해진 규약을 의미한다.

### 1.2 웹 브라우저(클라이언트)와 웹 서버와의 관계

 웹 콘텐츠는 웹 서버에 존재한다. 웹 서버는 클라이언트와 HTTP 프로토콜로 의사소통하기 때문에 HTTP 서버라고도 불린다.  
 클라이언트는 서버에게 HTTP 요청을 보내고 서버는 요청된 데이터를 HTTP 응답으로 돌려준다.  
 가장 흔한 클라이언트는 웹 브라우저이다. 웹 브라우저는 서버에게 HTTP 객체를 **요청** 하고 사용자의 화면에 보여준다.  

 위 요구사항에서 "http://localhost:8080/index.html" 페이지를 열어볼 때, 웹 브라우저는 HTTP 요청을  localhost:8080 서버로  
 보낸다. 서버는 요청받은 객체("/index.html")를 찾고, 성공했다면 그것의 타입, 길이 등의 정보와  함께 HTTP 응답에 실어서  
 클라이언트에게 보낸다.

### 1.3 HTTP 메시지

 >HTTP가 인터넷의 배달원이라면, HTTP 메시지는 무언가를 담아 보내는 소포와 같다.<br/> HTTP 메시지는 단순한 데이터의 구조화된 블록이다. 
   
 - HTTP 메시지 예시 (응답 메시지)   
 <br/>   
 
 |HTTP/1.0 200 OK|
 |---------------|
 |Content-type: text/plain<br/> Content-length: 19|
 |Hi, I'm a message!|
 
메세지는 크게 세 부분으로 구성된다.
 - 시작줄 : 요청 또는 응답 메시지가 어떤 메시지인지 나타낸다.
 - 헤더 블록: 메시지의 속성을 나타낸다. 각 헤더 필드는 쉬운 구문분석을 위해 쌍점(:)으로 구분되어 있는 하나의 이름과 값으로 구성된다. 헤더는 빈줄로 끝난다.
 - 본문 : 빈줄 다음에 오는 본문은 데이터를 담고 있다. 텍스트나 이진 데이터를 포함할 수 있고 생략 가능하다.  

시작줄과 헤더는 단순히 줄 단위로 분리된 아스키 문자열이다. 각 줄은 캐리지 리턴(ASCII 13)과 개행 문자(ASCII 10)로 구성된 두 글자의 줄바꿈 문자열(=CRLF)로 끝난다.

### 1.4 InputStreamReader, BufferedReader와 DataOutputStream

- InputStreamReader (문자 변환 보조 스트림)

소스 스트림이 바이트 기반 스트림(InputStream, OutputStream, FileInputStream, FileOutputStream)이면서 입출력 데이터가 문자라면 Reader와 Writer로 변환해서 사용하는 것을 고려해야 한다. 왜냐하면 Reader와 Writer는 문자 단위로 입출력하기 때문에 바이트 기반 스트림보다는 편리하고, 문자셋의 종류를 지정할 수 있기 때문이다.

- BufferedReader (성능 향상 보조 스트림)

프로그램이 입출력 소스와 직접 작업하지 않고 중간에 메모리 버퍼(buffer)와 작업함으로써 실행 성능을 향상시킬 수 있다. 버퍼는 데이터가 쌓이기를 기다렸다가 꽉 차게 되면 데이터를 한꺼번에 하드 디스크로 보냄으로써 출력 횟수를 줄여준다. 성능향상 보조스트림에는 BufferedInputStream, BufferedReader, BufferedOutputStream, BufferedWriter 가 있다.  

``` java
BufferedInputStream bis = new BufferedInputStream(바이트입력스트림);
BufferedReader br = new BufferedReader(문자입력스트림);
```

- DataOutputStream (기본 타입 입출력 보조 스트림)

바이트 스트림은 바이트 단위로 입출력하기 때문에 자바의 기본 데이터 타입인 boolean, char, short, int, long, float, double 단위로 입출력할 수 없다. 그러나 DataInputStream과 DataOutputStream 보조 스트림을 연결하면 기본 데이터 타입으로 입출력이 가능하다.

``` java
DataInputStream dis = new DataInputStream(바이트입력스트림);
DataOutputStream dos = new DataOutputStream(바이트출력스트림);
```
