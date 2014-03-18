package com.matchmaking.service;

import java.util.HashMap;
import java.util.Map;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;


/**
 * @author Aaron
 *
 *	MatchmakingCacheController - Class used for handling interactions with the Memcache cache.
 */

public class MatchmakingCacheController {
    private static Cache cache;
    static {
        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            Map<String, Integer> props = createPolicyMap();
            cache = cacheFactory.createCache(props);
        } catch (CacheException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Integer> createPolicyMap() {
        Map<String, Integer> props = new HashMap<String, Integer>();
        props.put(GCacheFactory.EXPIRATION_DELTA, Settings.CACHE_TTL);
        return props;
    }

    public static void put(final String key, final Object value) {
        cache.put(key, value);
    }

    public static Object get(final String key) {
        return cache.get(key);
    }
}