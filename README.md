# EnergyManagementSys_CommunicationSubSys

本系统是一个基于Netty开发的远程抄表系统，系统的主要功能有：远程抄表和数据持久化到DB（目前仅支持MySQL），目前的主要开发服务对象是智能电表，智能电表在行业内的远程抄表使用的是DLT/645-1997协议或DLT/645-2007协议，本系统面向后者开发。


### 如何使用？
调用`cn.edu.xust.communication.remote.AmmeterRemoteReader`或者`cn.edu.xust.communication.AmmeterAutoReader`类中的方法即可，下面是一个使用示例：
```java
package cn.edu.xust.controller;

import cn.edu.xust.bean.AmmeterParameter;
import cn.edu.xust.communication.remote.AmmeterRemoteReader;
import cn.edu.xust.mapper.AmmeterParameterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/28 17:25
 */
@Controller
public class AmmeterController {

    @Autowired
    private AmmeterParameterMapper ammeterParameterMapper;
    @Autowired
    private AmmeterRemoteReader ammeterRemoteReader;

    @ResponseBody
    @RequestMapping(value = "/test/{ammeterId}")
    public List<AmmeterParameter> controller(@PathVariable(value = "ammeterId") String ammeterId){
        List<String> list = new ArrayList<>();
        list.add(ammeterId);
        return ammeterRemoteReader.realTelemetry(list);
    }


}
```


### 功能演示
在启动SpringBoot项目的时候，此通信系统也会跟随自动启动并默认监听9000端口，端口可以在配置文件中通过`netty.server.port`进行配置，启动成功后会打印Netty Server启动成功的信息
<img src="http://image.easyblog.top/158209708845772bfbdb5-e360-4ed8-9726-8bb9b5e074a7.png">


基于示例中的代码，在浏览器地址栏[http://127.0.0.1:8080/test/398192000337](http://127.0.0.1:8080/test/398192000337)


（1）可以正常解析DLT/645-2007协议数据(下面是解析了A相电压数据)：
![](http://image.easyblog.top/15854716625363e14c773-df3f-4ece-a5d6-b787d91e1e23.png)

（2）解析完成后自动将数据更新到数据库，如下是更新过程打印的日志：
![](http://image.easyblog.top/158547155211439a25014-b848-4a3b-8c99-4b7192213be6.png)


（3）浏览器最终响应结果：
![](http://image.easyblog.top/15854709914596790d6c9-9cfd-4bf6-9e8f-081777d61d57.png)


