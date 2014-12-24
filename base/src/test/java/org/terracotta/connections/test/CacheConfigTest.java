package org.terracotta.connections.test;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.terracotta.connections.ehcache.decorator.ReadThroughWithPutWriterDelegtatedCache;

/**
 * Created by vinay on 12/24/14.
 */
public class CacheConfigTest {

    protected CacheManager cacheManager;
    protected Ehcache cache;
    protected ReadThroughWithPutWriterDelegtatedCache readThroughWithPutWriterDelegtatedCache;
    protected HashMapStoreConnector hashMapStoreConnector;


    @Before
    public void createCacheAndPerformChecks() {
        cacheManager = CacheManager.create(CacheConfigTest.class.getResourceAsStream("/ehcache-decorator-writer.xml"));
        Assert.assertNotNull(cacheManager);
        cache = cacheManager.getEhcache("testDecoratedCacheOne");
        Assert.assertNotNull(cache);
        Assert.assertEquals(ReadThroughWithPutWriterDelegtatedCache.class, cache.getClass());
        readThroughWithPutWriterDelegtatedCache = (ReadThroughWithPutWriterDelegtatedCache) cache;
        hashMapStoreConnector = (HashMapStoreConnector) readThroughWithPutWriterDelegtatedCache.getStoreConnector();
        hashMapStoreConnector.INTERNAL_MAP.clear();

    }

    @Test
    public void testThatPutCallsWriter() {
        //using cache decorator that calls putWithWriter by default
        Assert.assertNull(hashMapStoreConnector.INTERNAL_MAP.get("2"));
        cache.put(new Element("2", "3"));
        Assert.assertNotNull(hashMapStoreConnector.INTERNAL_MAP.get("2"));
    }

    @Test
    public void testReadThroughWhenDataIsNotInCache() {
        cache.removeAll();
        hashMapStoreConnector.INTERNAL_MAP.put("1", "2");

        Assert.assertNull(cache.getQuiet("1"));
        Assert.assertNotNull(cache.get("1"));
        Assert.assertNotNull(cache.getQuiet("1"));
    }

    @After
    public void destroyCache() {
        cache.dispose();
        cacheManager.removeCache("testDecoratedCacheOne");
        cacheManager.shutdown();
    }

}
