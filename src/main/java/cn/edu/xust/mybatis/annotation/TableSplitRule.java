package cn.edu.xust.mybatis.annotation;

/**
 * 分表规则
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/19 21:00
 */
public @interface TableSplitRule {

    public String tableName();

    //暂时只支持单参数
    public String paramName();

    public String targetName();
}
