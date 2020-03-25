package cn.edu.xust.communication.util;


import cn.edu.xust.communication.config.ApplicationContextHolder;
import cn.edu.xust.communication.config.redis.ObjectRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Redis工具类，封装了对象和Redis基本数据类型的大部分存,取,删除,设置过期时间等操作. 所有操作可以指定数据库索引.
 * 存,取可以设置过期时间. 没有设置默认过期时间,存值时尽量设置过期时间
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/27 11:56
 */
@Component(value = "redisUtils")
public class RedisUtils {

    @Autowired
    private ObjectRedisTemplate redisTemplate;
    @Autowired
    private ValueOperations<String, Object> redisValueOps;
    @Autowired
    private HashOperations<String, String, Object> redisHashOps;
    @Autowired
    private ListOperations<String, Object> redisListOps;
    @Autowired
    private SetOperations<String, Object> redisSetOps;
    @Autowired
    private ZSetOperations<String, Object> redisZSetOps;

    /**Redis默认的16个数据库索引**/
    public static final int DB_0 = 0;
    public static final int DB_1 = 1;
    public static final int DB_2 = 2;
    public static final int DB_3 = 3;
    public static final int DB_4 = 4;
    public static final int DB_5 = 5;
    public static final int DB_6 = 6;
    public static final int DB_7 = 7;
    public static final int DB_8 = 8;
    public static final int DB_9 = 9;
    public static final int DB_10 = 10;
    public static final int DB_11 = 11;
    public static final int DB_12 = 12;
    public static final int DB_13 = 13;
    public static final int DB_14 = 14;
    public static final int DB_15 = 15;


    /**
     * Redis数据库最大索引
     */
    private static final int MAX_DB_INDEX = 15;
    /**
     * Redis数据库最小索引
     */
    private static final int MIN_DB_INDEX = 0;

    /**
     * redis读写工具类
     */
    private static RedisUtils redisUtils = null;

    public static RedisUtils getRedisUtils() {
        if (Objects.isNull(redisUtils)) {
            synchronized (RedisUtils.class) {
                redisUtils = ApplicationContextHolder.getBean("redisUtils");
            }
        }
        return redisUtils;
    }


    //=============================common============================

    /**
     * 向外暴露RedisTemplate
     *
     * @param indexdb
     * @return
     */
    public RedisTemplate getRedisTemplate(int indexdb) {
        setDbIndex(indexdb);
        return redisTemplate;
    }

    /**
     * 设置数据库索引
     *
     * @param dbIndex
     */
    public void setDbIndex(Integer dbIndex) {
        if (dbIndex == null || dbIndex > MAX_DB_INDEX || dbIndex < MIN_DB_INDEX) {
            dbIndex = 0;
        }
        LettuceConnectionFactory jedisConnectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        assert jedisConnectionFactory != null;
        jedisConnectionFactory.setDatabase(dbIndex);
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key     键
     * @param time    时间(秒)
     * @param indexdb 读写操作的库
     * @return
     */
    public Boolean expire(String key, long time, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            if (time > 0) {
                return redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public Long getExpire(String key, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public Boolean delete(int indexdb, String... key) {
        if (key != null && key.length > 0) {
            this.setDbIndex(indexdb);
            if (key.length == 1) {
                return redisTemplate.delete(key[0]);
            } else {
                Long deleted = redisTemplate.delete(CollectionUtils.arrayToList(key));
                if (Objects.nonNull(deleted) && deleted > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    //============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key, int indexdb) {
        this.setDbIndex(indexdb);
        return key == null ? null : redisValueOps.get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            redisValueOps.set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            if (time > 0) {
                redisValueOps.set(key, value, time, TimeUnit.SECONDS);
            } else {
                redisValueOps.set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public Long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisValueOps.increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public Long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisValueOps.increment(key, -delta);
    }

    //================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item, int indexdb) {
        this.setDbIndex(indexdb);
        return redisHashOps.get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<String, Object> hmget(String key, int indexdb) {
        this.setDbIndex(indexdb);
        return redisHashOps.entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            redisHashOps.putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            redisHashOps.putAll(key, map);
            if (time > 0) {
                expire(key, time, indexdb);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            redisHashOps.put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            redisHashOps.put(key, item, value);
            if (time > 0) {
                expire(key, time, indexdb);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(int indexdb, String key, Object... item) {
        this.setDbIndex(indexdb);
        redisHashOps.delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item, int indexdb) {
        this.setDbIndex(indexdb);
        return redisHashOps.hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key   键
     * @param item  项
     * @param delta 要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double delta, int indexdb) {
        this.setDbIndex(indexdb);
        return redisHashOps.increment(key, item, delta);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by, int indexdb) {
        this.setDbIndex(indexdb);
        return redisHashOps.increment(key, item, -by);
    }

    //============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public Boolean sHasKey(String key, Object value, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sSet(String key, int indexdb, Object... values) {
        try {
            this.setDbIndex(indexdb);
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sSetAndTime(String key, long time, int indexdb, Object... values) {
        try {
            this.setDbIndex(indexdb);
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time, indexdb);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public Long sGetSetSize(String key, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public Long setRemove(String key, int indexdb, Object... values) {
        try {
            this.setDbIndex(indexdb);
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            return redisListOps.range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public Long lGetListSize(String key, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            return redisListOps.size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            return redisListOps.index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            redisListOps.rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time, int indexdb) {
        try {
            redisListOps.rightPush(key, value);
            if (time > 0) {
                expire(key, time, indexdb);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            redisListOps.rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            redisListOps.rightPushAll(key, value);
            if (time > 0) {
                expire(key, time, indexdb);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            redisListOps.set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public Long lRemove(String key, long count, Object value, int indexdb) {
        try {
            this.setDbIndex(indexdb);
            return redisListOps.remove(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }


   /* public static void main(String[] args) {
        RedisUtils redisUtil = new RedisUtils();
        redisUtil.set("user", "age:20", 1);
    }*/

}