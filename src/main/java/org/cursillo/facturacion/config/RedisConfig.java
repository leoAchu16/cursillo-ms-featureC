package org.cursillo.facturacion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            @Value("${cache.ttl.default}") Duration ttlDefault,
            @Value("${cache.ttl.conceptos}") Duration ttlConceptos,
            @Value("${cache.ttl.facturas}") Duration ttlFacturas,
            @Value("${cache.ttl.detalles-facturas}") Duration ttlDetalles) {

        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(base.entryTtl(ttlDefault))
                .withInitialCacheConfigurations(Map.of(
                        "conceptos", base.entryTtl(ttlConceptos),
                        "facturas", base.entryTtl(ttlFacturas),
                        "detalles-facturas", base.entryTtl(ttlDetalles)))
                .build();
    }
}