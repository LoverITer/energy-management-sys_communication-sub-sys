package cn.edu.xust.mybatis.interceptor;

import cn.edu.xust.mybatis.annotation.TableSplitRule;
import cn.edu.xust.mybatis.annotation.TableSplitTarget;
import cn.edu.xust.mybatis.strategy.Strategy;
import cn.edu.xust.mybatis.strategy.StrategyManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/19 21:03
 */
@Slf4j(topic = "分表策略拦截器[TableSplitInterceptor]")
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class TableSplitInterceptor implements Interceptor {

    @Autowired
    StrategyManager strategyManager;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.info("进入mybatisSql拦截器：====================");
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler =MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        Object parameterObject = metaStatementHandler.getValue("delegate.boundSql.parameterObject");
        doSplitTable(metaStatementHandler, parameterObject);
        // 传递给下一个拦截器处理
        return invocation.proceed();
    }

    private void doSplitTable(MetaObject metaStatementHandler, Object param) throws ClassNotFoundException {
        String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        if (originalSql != null && !originalSql.equals("")) {
            log.info("分表前的SQL：" + originalSql);
            MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
            String id = mappedStatement.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            Class<?> classObj = Class.forName(className);
            // 根据配置自动生成分表SQL
            TableSplitTarget tableSplit = classObj.getAnnotation(TableSplitTarget.class);
            if (tableSplit == null || !tableSplit.interFale()) {
                return;
            }
            TableSplitRule[] rules = tableSplit.rules();
            if (rules != null && rules.length > 0) {

                String convertedSql = null;
                // StrategyManager可以使用ContextHelper策略帮助类获取，本次使用注入
                for (TableSplitRule rule : rules) {
                    Strategy strategy = null;

                    if (rule.targetName() != null && !rule.targetName().isEmpty()) {
                        strategy = strategyManager.getStrategy(rule.targetName());
                    }
                    if (!rule.paramName().isEmpty() && !rule.tableName().isEmpty()) {

                        String paramValue = getParamValue(param, rule.paramName());

                        //获取 参数
                        String newTableName = strategy.returnTableName(rule.tableName(), paramValue);
                        try {
                            convertedSql = originalSql.replaceAll(rule.tableName(), newTableName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                log.info("新sql是：" + convertedSql);
                metaStatementHandler.setValue("delegate.boundSql.sql", convertedSql);
            }
        }
    }

    public String getParamValue(Object obj, String paramName) {
        if (obj instanceof Map) {
            return (String) ((Map) obj).get(paramName);
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equalsIgnoreCase(paramName)) {
                try {
                    return (String) field.get(obj);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return null;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
