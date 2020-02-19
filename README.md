# EnergyManagementSys_CommunicationSubSys
### 功能演示
在启动SpringBoot项目的时候，此通信系统也会跟随自动启动并默认监听9000端口，端口可以在配置文件中通过`netty.server.port`进行配置，启动成功后会打印Netty Server启动成功的信息
<img src="http://image.easyblog.top/158209708845772bfbdb5-e360-4ed8-9726-8bb9b5e074a7.png">

之后打开测试工具将其作为客户端和Netty服务建立连接
<img src="http://image.easyblog.top/1582097709330767c2fda-d623-4950-aeb9-34c39a4fe6ca.png">

向服务器发送JSON数据，服务器传上来的数据解析出来后再存到数据库中，成功返回"成功"的16进制码，失败返回"失败"的16进制码
<img src="http://image.easyblog.top/15820978844826a74721c-daf9-4f17-9a14-39650607a79c.png">

此过程后台的打印信息：
<img src="http://image.easyblog.top/15820981859403d2305f0-1418-459d-8f05-d472458ad56f.png">