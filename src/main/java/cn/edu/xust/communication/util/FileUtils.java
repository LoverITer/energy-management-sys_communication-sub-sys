package cn.edu.xust.communication.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/25 09:02
 */
public class FileUtils {


    private FileUtils() {

    }

    /**属性文件Map*/
    private static final  Map<String,String> PROPERTIES;

    /**
     * 将属性文件中的内容加载到内存中
     */
    static {
        PROPERTIES=new HashMap<>();
        Properties config = getLoadedProperties("src/main/java/META-INF/config.properties", Charset.forName("GBK"));
        Properties error = getLoadedProperties("src/main/java/META-INF/error.properties", Charset.forName("GBK"));
        config.forEach((k,v)-> PROPERTIES.put(String.valueOf(k),String.valueOf(v)));
        error.forEach((k,v)->PROPERTIES.put(String.valueOf(k),String.valueOf(v)));
    }


    public static Map<String,String> getPropertiesMap(){
        return PROPERTIES;
    }

    /**
     * 得到加载过属性配置文件的Properties
     *
     * @param path    文件路径
     * @param charset   编码集
     * @return  加载属性文件之后的Properties
     */
    public static Properties getLoadedProperties(String path, Charset charset) {
        Properties properties = new Properties();
        try {
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(path));
            InputStreamReader reader = new InputStreamReader(is, charset);
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void main(String[] args) {
        /*getPropertiesMap().forEach((k,v)->{
            System.out.println(k+"="+v);
        });*/
        System.out.println(getPropertiesMap().get("ERR_44"));
    }


}
