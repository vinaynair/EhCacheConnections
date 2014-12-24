package org.terracotta.connections.test;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.terracotta.connections.ehcache.writer.DelegatingCacheWriter;
import org.terracotta.connections.ehcache.decorator.ReadThroughWithPutWriterDelegtatedCache;

/**
 * Created by vinay on 12/24/14.
 */
public class ProgramticCacheTest {

    protected Cache cache;


    @Before
    public void createCache() {
        CacheManager.getInstance().addCache("test");
        cache = CacheManager.getInstance().getCache("test");
    }

    @Test
    public void testWriterByRegisteringWriterTocache() {
        //re
        HashMapStoreConnector hashMapStoreConnector = new HashMapStoreConnector();
        cache.registerCacheWriter(new DelegatingCacheWriter<String, String>(hashMapStoreConnector));

        //call putWithWriter
        Assert.assertNull(hashMapStoreConnector.INTERNAL_MAP.get("1"));
        cache.putWithWriter(new Element("1", "2"));
        Assert.assertNotNull(hashMapStoreConnector.INTERNAL_MAP.get("1"));
    }

    @Test
    public void testWriterWithCacheDecoratorThatCallsPutWithWriter() {
        HashMapStoreConnector hashMapStoreConnector = new HashMapStoreConnector();
        //using cache decorator that calls putWithWriter by default
        ReadThroughWithPutWriterDelegtatedCache readThroughCache = new ReadThroughWithPutWriterDelegtatedCache(cache, hashMapStoreConnector,null);
        Assert.assertNull(hashMapStoreConnector.INTERNAL_MAP.get("2"));
        cache.putWithWriter(new Element("2", "3"));
        Assert.assertNotNull(hashMapStoreConnector.INTERNAL_MAP.get("2"));
    }

    @Test
    public void testReadThrough() {
        HashMapStoreConnector hashMapStoreConnector = new HashMapStoreConnector();
        hashMapStoreConnector.INTERNAL_MAP.put("1", "2");

        ReadThroughWithPutWriterDelegtatedCache readThroughCache = new ReadThroughWithPutWriterDelegtatedCache(cache, hashMapStoreConnector,null);
        Assert.assertNull(readThroughCache.getQuiet("1"));
        Assert.assertNotNull(readThroughCache.get("1"));
        Assert.assertNotNull(readThroughCache.getQuiet("1"));
    }

    @After
    public void destroyCache() {
        cache.dispose();
        CacheManager.getInstance().removeCache("test");
    }

}
