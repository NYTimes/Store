package com.nytimes.android.external.store3.base.impl;


import com.nytimes.android.external.store3.storecache.StoreCache;
import com.nytimes.android.external.store3.storecache.StoreCacheBuilder;

import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public final class CacheFactory {
    private CacheFactory() {

    }

    static <Key, Parsed> StoreCache<Key, Maybe<Parsed>> createCache(MemoryPolicy memoryPolicy) {
       return createBaseCache(memoryPolicy);
    }

    static <Key, Parsed> StoreCache<Key, Single<Parsed>> createInflighter(MemoryPolicy memoryPolicy) {
       return createBaseInFlighter(memoryPolicy);
    }

    public static <Key, Parsed> StoreCache<Key, Observable<Parsed>> createRoomCache(MemoryPolicy memoryPolicy) {
     return createBaseCache(memoryPolicy);
    }



    public static <Key, Parsed> StoreCache<Key, Observable<Parsed>> createRoomInflighter(MemoryPolicy memoryPolicy) {
        return createBaseInFlighter(memoryPolicy);
    }


    private static <Key, Value> StoreCache<Key, Value> createBaseInFlighter(MemoryPolicy memoryPolicy) {
        long expireAfterToSeconds = memoryPolicy == null ? StoreDefaults.getCacheTTLTimeUnit()
                .toSeconds(StoreDefaults.getCacheTTL())
                : memoryPolicy.getExpireAfterTimeUnit().toSeconds(memoryPolicy.getExpireAfterWrite());
        long maximumInFlightRequestsDuration = TimeUnit.MINUTES.toSeconds(1);

        if (expireAfterToSeconds > maximumInFlightRequestsDuration) {
            return StoreCacheBuilder
                    .newBuilder()
                    .expireAfterWrite(maximumInFlightRequestsDuration, TimeUnit.SECONDS)
                    .build();
        } else {
            long expireAfter = memoryPolicy == null ? StoreDefaults.getCacheTTL() :
                    memoryPolicy.getExpireAfterWrite();
            TimeUnit expireAfterUnit = memoryPolicy == null ? StoreDefaults.getCacheTTLTimeUnit() :
                    memoryPolicy.getExpireAfterTimeUnit();
            return StoreCacheBuilder.newBuilder()
                    .expireAfterWrite(expireAfter, expireAfterUnit)
                    .build();
        }
    }


    private static <Key, Value> StoreCache<Key, Value> createBaseCache(MemoryPolicy memoryPolicy){
        if (memoryPolicy == null) {
            return StoreCacheBuilder
                    .newBuilder()
                    .maximumSize(StoreDefaults.getCacheSize())
                    .expireAfterWrite(StoreDefaults.getCacheTTL(), StoreDefaults.getCacheTTLTimeUnit())
                    .build();
        } else {
            if (memoryPolicy.getExpireAfterAccess() == memoryPolicy.DEFAULT_POLICY) {
                return StoreCacheBuilder
                        .newBuilder()
                        .maximumSize(memoryPolicy.getMaxSize())
                        .expireAfterWrite(memoryPolicy.getExpireAfterWrite(), memoryPolicy.getExpireAfterTimeUnit())
                        .build();
            } else {
                return StoreCacheBuilder
                        .newBuilder()
                        .maximumSize(memoryPolicy.getMaxSize())
                        .expireAfterAccess(memoryPolicy.getExpireAfterAccess(), memoryPolicy.getExpireAfterTimeUnit())
                        .build();
            }
        }
    }

}
