package com.geekcattle.core.j2cache.cache.support.util;

import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;

import net.oschina.j2cache.J2CacheConfig;

public class SpringJ2CacheConfigUtil {

    /**
     * 从spring环境变量中查找j2cache配置
     */
    public final static J2CacheConfig initFromConfig(StandardEnvironment environment){
        J2CacheConfig config = new J2CacheConfig();
        config.setSerialization(environment.getProperty("j2cache.serialization"));
        config.setBroadcast(environment.getProperty("j2cache.broadcast"));
        config.setL1CacheName(environment.getProperty("j2cache.L1.provider_class"));
        config.setL2CacheName(environment.getProperty("j2cache.L2.provider_class"));
        config.setSyncTtlToRedis(!"false".equalsIgnoreCase(environment.getProperty("j2cache.sync_ttl_to_redis")));
        config.setDefaultCacheNullObject("true".equalsIgnoreCase(environment.getProperty("j2cache.default_cache_null_object")));
        String l2ConfigSection = environment.getProperty("j2cache.L2.config_section");
        if (l2ConfigSection == null || "".equals(l2ConfigSection.trim())) {
            l2ConfigSection = config.getL2CacheName();
        }
        final String l2Section = l2ConfigSection;
        environment.getPropertySources().forEach(a -> {
            if(a instanceof ResourcePropertySource) {
                ResourcePropertySource c = (ResourcePropertySource) a;
                c.getSource().forEach((k,v) -> {
                    String key = k;
                    if (key.startsWith(config.getBroadcast() + ".")) {
                        config.getBroadcastProperties().setProperty(key.substring((config.getBroadcast() + ".").length()), (String) v);
                    }
                    if (key.startsWith(config.getL1CacheName() + ".")) {
                        config.getL1CacheProperties().setProperty(key.substring((config.getL1CacheName() + ".").length()), (String) v);
                    }
                    if (key.startsWith(l2Section + ".")) {
                        config.getL2CacheProperties().setProperty(key.substring((l2Section + ".").length()), (String) v);
                    }
                });
            }
        });
        return config;
    }
}