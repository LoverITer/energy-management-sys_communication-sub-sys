package cn.edu.xust.mybatis.strategy;

import java.util.Calendar;

/**
 * 按月分表策略
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/19 21:11
 */
public class CreateTableMonthlyStrategy implements Strategy{

    @Override
    public String returnTableName(String tableName, String splitParam) {
        // 结果类似 20190601
        return tableName+"_"+ Calendar.getInstance().getTime();
    }
}
