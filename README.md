# 示范用即时消息/离线消息模块

本来我还想做个类 web-weixin 的 GUI 的...不过现在貌似非常没空, 拜托有哪个路过的好心人顺手撸了吧...  

然后配置和部署什么不说了. 喵咕噜框架不需要教程, 直接说集成.

这个只有 API 的东东包括如下功能  

* 点对点发消息
* 接收消息, 长连接或者轮询
* 附带地支持离线消息
* 支持多终端, 或者说多个页面也不会丢消息
* 无认证机制(...嗯, 这个你们自己写
* 历史记录(部分支持)
* 未读消息数量

## 总之先演示下正确的打开方式吧

发个消息:  

    POST http://localhost:8080/pf/msg/send.api?uid=2&t=1&c=blabla HTTP/1.1
    User-Agent: Fiddler
    Host: localhost:8080
    Content-Length: 0

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Content-Type: application/json;charset=utf-8
    Content-Length: 22
    Date: Sun, 14 Sep 2014 18:26:52 GMT

    {"error":"no-error"}

其中,   

* `uid``必需`, 自机的id
* `t``必需`, 对方的id  
* `c``必需`, 消息正文  

发出的消息马上会进入数据库.


然后是接收:

    POST http://localhost:8080/pf/msg/retrieve-unread.api?uid=1&since=1410719234199&wait=30 HTTP/1.1
    User-Agent: Fiddler
    Host: localhost:8080
    Content-Length: 0

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Set-Cookie: JSESSIONID=1853D6199759AA5DB596C02BFFDDDDB3; Path=/pf/; HttpOnly
    Content-Type: application/json;charset=utf-8
    Content-Length: 93
    Date: Sun, 14 Sep 2014 20:30:10 GMT

    [{"c":"blabla","f":2,"m":1410724947000,"t":1},{"c":"blabla","f":2,"m":1410724949000,"t":1}]

其中, 

* `uid``必需`, 自机id
* `since``必需`, 取得该时间戳以后的消息(涉及到时间区间的, 如非指明, 均为左开右闭, 下同)
* `wait``可选`, 表示长连接的等待时间, 单位秒

响应参数含义如上, 多出来的`m`是消息的时间戳, 理论上返回的消息是升序的.  

接口支持阻塞特性. 如果请求时刻没有可用消息, 该请求会一直挂着, 直到有消息可用或者达到最大等待时长, 后者会返回空数组.  
通过该接口检出的都是未读消息, 这些消息可以被重复地检出, 直到被标记已读.  

另外还有:

    POST http://localhost:8080/pf/msg/retrieve.api?uid=1&since=1410719234199&wait=30 HTTP/1.1

参数及返回值同上, 区别在于这个可以取得已读消息


标记已读:

    POST http://localhost:8080/pf/msg/ack.api?uid=1&due=1410719234199 HTTP/1.1
    User-Agent: Fiddler
    Host: localhost:8080
    Content-Length: 0

    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Content-Type: application/json;charset=utf-8
    Content-Length: 22
    Date: Sun, 14 Sep 2014 19:59:46 GMT

    {"error":"no-error"}

其中,

* `uid``必需`, 自机的id
* `due``必需`, 截止时间戳

反正语义上就是将该时间戳之前的消息全部标已读, 不区分是谁发过来的.

未读消息数量:

    POST http://localhost:8080/pf/msg/count-unread.api?uid=1&f=3&since=1410719234199 HTTP/1.1
    User-Agent: Fiddler
    Host: localhost:8080
    Content-Length: 0


    HTTP/1.1 200 OK
    Server: Apache-Coyote/1.1
    Content-Type: application/json;charset=utf-8
    Content-Length: 13
    Date: Sun, 14 Sep 2014 20:45:17 GMT

    {"count":1}

其中,

* `uid``必需`, 自机的id  
* `f``可选`, 按发件人统计, 缺省为所有人.
* `since``必需`, (但其实应该是可选的, 这是个bug)

注意这个不是长连接的.


