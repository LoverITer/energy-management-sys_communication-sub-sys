package cn.edu.xust.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分表策略拦截
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/19 21:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TableSplitTarget {

    boolean interFale() default true;

    //分表规则
    public TableSplitRule[] rules();
}
