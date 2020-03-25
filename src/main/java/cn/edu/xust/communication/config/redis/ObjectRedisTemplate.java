package cn.edu.xust.communication.config.redis;


import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Huanxin
 */
public class ObjectRedisTemplate extends RedisTemplate<String, Object> {


    public ObjectRedisTemplate() {
        this.setKeySerializer(new StringRedisSerializer());
        this.setHashKeySerializer(new StringRedisSerializer());
        this.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        this.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
    }

    public ObjectRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

    @Override
    protected RedisConnection preProcessConnection(RedisConnection connection, boolean existingConnection) {
        return super.preProcessConnection(connection, existingConnection);
    }

}
