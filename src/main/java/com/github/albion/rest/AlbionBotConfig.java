package com.github.albion.rest;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.net.URI;
import java.util.logging.Level;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories()
public class AlbionBotConfig
{

    @Bean(name = "redisConnectionFactory")
    RedisConnectionFactory connectionFactory()
    {
        try
        {
            URI uri = new URI(System.getenv("REDISCLOUD_URL"));
            java.util.logging.Logger.getLogger(AlbionBotConfig.class.getName()).severe(uri.toString());
            JedisConnectionFactory factory = new JedisConnectionFactory();
            factory.setHostName(uri.getHost());
            factory.setPort(uri.getPort());
            factory.setPassword(uri.getUserInfo().split(":", 2)[1]);
            return factory;
        } catch (Exception ex)
        {
            java.util.logging.Logger.getLogger(AlbionBotConfig.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate()
    {
        final RedisTemplate< String, Object> template = new RedisTemplate< String, Object>();
        template.setConnectionFactory(connectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer< Object>(Object.class));
        template.setValueSerializer(new GenericToStringSerializer< Object>(Object.class));
        return template;
    }

    public @Bean
    MongoClientURI mongoClientURI()
    {
        return new MongoClientURI(System.getenv("MONGODB_URI"));
    }

    public @Bean
    MongoClient mongoClient()
    {
        return new MongoClient(mongoClientURI());
    }

    public @Bean
    MongoTemplate mongoTemplate()
    {
        return new MongoTemplate(mongoClient(), mongoClientURI().getDatabase());
    }
}
