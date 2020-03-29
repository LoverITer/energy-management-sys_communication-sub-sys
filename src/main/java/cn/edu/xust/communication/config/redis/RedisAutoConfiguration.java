package cn.edu.xust.communication.config.redis;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/27 16:13
 */
@SpringBootConfiguration
public class RedisAutoConfiguration extends org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration {

    @Bean(name = "redisTemplate")
    public ObjectRedisTemplate objectRedisTemplate(LettuceConnectionFactory factory) {
        ObjectRedisTemplate objectRedisTemplate = new ObjectRedisTemplate();
        factory.setShareNativeConnection(false);
        objectRedisTemplate.setConnectionFactory(factory);
        return objectRedisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    @ConditionalOnMissingBean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    @ConditionalOnMissingBean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    @Bean
    @ConditionalOnMissingBean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    @Bean
    @ConditionalOnMissingBean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

}
