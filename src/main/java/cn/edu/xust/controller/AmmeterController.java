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
