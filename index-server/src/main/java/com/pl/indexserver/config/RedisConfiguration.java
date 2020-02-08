package com.pl.indexserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfiguration {

    @Value("${jedis.pool.maxTotal}")
    private int maxTotal;

    @Value("${jedis.pool.maxIdle}")
    private int maxIdle;

    @Value("${jedis.pool.maxWaitMillis}")
    private int maxWaitMillis;

    @Value("${jedis.pool.host}")
    private String host;

    @Value("${jedis.pool.port}")
    private int port;

    @Value("${jedis.pool.password}")
    private String passwd;

    public RedisConfiguration(){

    }
    //redis 分布式连接池
    /*@Bean
    public ShardedJedisPool shardedJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(this.getMaxTotal());
        jedisPoolConfig.setMaxIdle(this.getMaxIdle());
        jedisPoolConfig.setMaxWaitMillis(this.getMaxWaitMillis());
        List<JedisShardInfo> jedisShardInfos = new ArrayList<>();
        JedisShardInfo jedisShardInfo =new JedisShardInfo(host, port, 3000);
        jedisShardInfo.setPassword(passwd);
        jedisShardInfos.add(jedisShardInfo);
        ShardedJedisPool jedisPool = new ShardedJedisPool(jedisPoolConfig, jedisShardInfos);
        return jedisPool;
    }*/

    //redis 连接池
    @Bean
    public JedisPool jedisPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        JedisPool pool = new JedisPool(config, host, port, 3000, passwd);
        return pool;
    }
}
