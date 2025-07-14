package config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类 - 使用FastJSON2
 */
@Configuration
@ConditionalOnExpression("${default.redis.enabled:true}")
public class RedisConfig {

    @Autowired
    private Environment environment;

    /**
     * 自定义FastJSON2 Redis序列化器
     */
    public static class FastJson2RedisSerializer<T> implements RedisSerializer<T> {

        static final Filter AUTO_TYPE_FILTER = JSONReader.autoTypeFilter(
                // 按需加上需要支持自动类型的类名前缀，范围越小越安全
                "com.hugai.**",
                "com.org.bebas.**",
                "common.**"
        );

        private final Class<T> clazz;

        public FastJson2RedisSerializer(Class<T> clazz) {
            super();
            this.clazz = clazz;
        }

        @Override
        public byte[] serialize(T t) throws SerializationException {
            if (t == null) {
                return new byte[0];
            }
            return JSON.toJSONBytes(t, JSONWriter.Feature.WriteClassName);
        }

        @Override
        public T deserialize(byte[] bytes) throws SerializationException {
            if (ArrayUtils.isEmpty(bytes)) {
                return null;
            }
            return JSON.parseObject(bytes, clazz, AUTO_TYPE_FILTER);
        }
    }

    @Primary
    @Bean("redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 使用自定义的FastJSON2序列化器
        FastJson2RedisSerializer<Object> fastJson2RedisSerializer = new FastJson2RedisSerializer<>(Object.class);

        // key的序列化采用StringRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(fastJson2RedisSerializer);

        // hash的key和value序列化配置
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(fastJson2RedisSerializer);

        // 初始化RedisTemplate
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    @Primary
    public ValueOperations<Object, Object> getValueOperations(RedisTemplate<Object, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    @Primary
    public ListOperations<Object, Object> getListOperations(RedisTemplate<Object, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    @Bean
    @Primary
    public HashOperations<Object, Object, Object> getHashOperations(RedisTemplate<Object, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    @Primary
    public SetOperations<Object, Object> getSetOperations(RedisTemplate<Object, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    @Bean
    @Primary
    public ZSetOperations<Object, Object> getZSetOperations(RedisTemplate<Object, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}