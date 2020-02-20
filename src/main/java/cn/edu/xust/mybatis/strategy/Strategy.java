package cn.edu.xust.mybatis.strategy;

/**
 * 分表策略服务接口
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/19 21:10
 */
public interface Strategy {
    /**
     * 传入表名 和分表参数
     *
     * @param tableName
     * @param splitParam
     * @return
     */
    String returnTableName(String tableName, String splitParam);
}
