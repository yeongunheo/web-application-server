# web-application-server

- 자바 웹 프로그래밍 Next Step 스터디(2020. 03 ~) - 웹서버 구현 실습
- Forked from [https://github.com/slipp/web-application-server](https://github.com/slipp/web-application-server)

### Reference
- [자바 웹 프로그래밍 Next Step 하나씩 벗겨가는 양파껍질 학습법(박재성 저. 로드북. 2016)](http://book.naver.com/bookdb/book_detail.nhn?bid=11037465)
- [HTTP 완벽 가이드(데이빗고울리 외 5명 저, 이응준 외 1명 역, 인사이트, 2014)](http://book.naver.com/bookdb/book_detail.nhn?bid=8509980)

# 각 요구사항별 학습 내용 정리
| 구분     | 내용                                                                                         |
| :----- | ---------------------------------------- |
| 요구사항 1 | http://localhost:8080/index.html로 접속시 응답 |
| 요구사항 2 | GET 방식으로 회원가입                            |
| 요구사항 3 | POST 방식으로 회원가입                           |
| 요구사항 4 | 302 status code 적용                         |
| 요구사항 5 | 로그인하기                                   |
| 요구사항 6 | 사용자 목록 출력                            |
| 요구사항 7 | CSS 지원하기                            |

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

## 요구사항 2 - GET 방식으로 회원가입

### 2.1 HTML 폼(form)

폼은 웹 브라우저에서 입력된 데이터를 한 번에 서버로 전송한다. 전송한 데이터는 웹 서버가 처리하고, 결과에 따라 또 다른 웹 페이지를 보여준다. 폼 태그 속성에는 action, method 등이 있다.

- action : 폼 데이터(form data)를 서버로 보낼 때 해당 데이터가 도착할 URL을 명시한다.
- method : 폼을 서버에 전송할 http 메서드를 정한다. (GET 또는 POST)

### 2.2 GET 메서드

GET은 가장 흔한 메서드다. 주로 서버에게 리소스를 달라고 요청하기 위해 쓰인다.  
전송할 http 메서드 종류인 GET과 POST는 브라우저에서 폼 데이터를 가져와 서버로 보내는 똑같은 기능을 수행하지만, 방식은 다르다. GET은 폼 데이터를 URL 끝에 붙여서 눈에 보이게 보내지만 POST 방식은 내부적으로 보이지 않게 보냅니다(= http 메시지 본문에 담아 보낸다).  

> http://localhost:8080/user/create?userId=yeongunheo&password=password&name=YeongUn&email=gjduddns@gmail.com

### 2.3 util.HttpRequestUtils 클래스의 parseQueryString() 메소드

GET 방식을 통해 URL 형태로 넘어온 사용자 정보는 이름=값 형태로 되어있어 Map 객체로 받을 수 있다. 이때 활용되는 것이 바로 util.HttpRequestUtils 클래스의 parseQueryString() 메소드이다.

``` java
Map<String, String> datas = HttpRequestUtils.parseQueryString(params);
//params로 들어오는 queryString은 URL에서 ? 이후에 전달되는 field1=value1&field2=value2 형식임
```

## 요구사항 3 - POST 방식으로 회원가입

### 3.1 POST 메서드

POST로 데이터를 전달할 경우 전달하는 데이터는 HTTP 본문에 담긴다. 이 때 본문에 전달되는 데이터는 GET 방식으로 데이터를 전달할 때(이름=값 형태)와 같다.

---

### Issue timeline

#### 1. 본문 메시지를 처리할 때 `br.readline()`을 사용했더니 데이터를 전달하는 과정에서 무한 루프가 발생하였다.
=> HTTP 프로토콜에서 본문의 메시지 끝부분에는 /r/n가 없다. 그렇기 때문에 문장 끝의 /r/n를 인식해서 한 줄씩 입력을 받는 `br.readline()`은 사용할 수 없다. 대신 util.IOUtils 클래스의 readData() 메소드를 활용해 HTTP 헤더의 Content-Length의 값을 매개값으로 전달하여 처리할 수 있다.
```java
String httpEntity = IOUtils.readData(br, contentLength);
```

## 요구사항 4 - 302 status code 적용

### 4.1 HTTP 302 status code (리다이렉션 상태 코드)

리다이렉션 상태 코드는 클라이언트가 관심있어 하는 리소스에 대해 다른 위치를 사용하라고 말해주거나 그 리소스의 내용 대신 다른 대안 응답을 제공한다. 만약 리소스가 옮겨졌다면, 클라이언트에게 리소스가 옮겨졌으며 어디서 찾을 수 있는지 알려주기 위해 리다이렉션 상태 코드와 Location 헤더를 보낼 수 있다. 이는 브라우저가 사용자를 귀찮게 하지 않고 알아서 새 위치로 이동할 수 있게 해준다.

 - HTTP 302 리다이렉션 코드 예시 (응답 메시지)   
 
|HTTP/1.0 302 Found|
|---------------|
|Location: /index.html|

리다이렉션 코드를 응답받은 웹 브라우저는 Location 헤더의 URL을 읽어 GET 방식으로 다시 웹서버로 요청한다. 301 상태 코드와의 차이점은 클라이언트가 Location 헤더로 주어진 URL을 리소스를 임시로 가리키기 위한 목적으로 사용해야 한다는 것이다. 이후의 요청에서는 원래의 URL을 사용해야 한다.

## 요구사항 5 - 로그인하기

### 5.1 HTTP 쿠키

쿠키는 사용자를 식별하고 세션을 유지하는 방식 중에서 현재까지 가장 널리 사용하는 방식이다. 쿠키는 HTTP 응답 헤더에 Set-Cookie를 추가하여 사용할 수 있다. 전달하는 값은 이름=값 형태로 만든다.

- HTTP 쿠키 예시 (응답 메시지)   
 
|HTTP/1.0 200 OK|
|---------------|
|Content-type: text/html|
|Set-Cookie: logined=true|

## 요구사항 6 - 사용자 목록 출력

## 요구사항 7 - CSS 지원하기

응답 헤더의 Content-Type을 text/html로 보내면 브라우저는 HTML 파일로 인식하기 때문에 CSS가 정상적으로 동작하지 않는다.

- HTTP 응답 메시지 (css 파일일 경우)   
 
|HTTP/1.0 200 OK|
|---------------|
|Content-type: text/css|
