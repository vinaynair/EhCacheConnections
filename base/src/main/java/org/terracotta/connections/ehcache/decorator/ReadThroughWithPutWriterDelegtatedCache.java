package org.terracotta.connections.ehcache.decorator;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.EhcacheDecoratorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.connections.StoreConnector;
import org.terracotta.connections.StoreWriter;
import org.terracotta.connections.ehcache.writer.DelegatingCacheWriter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * Read through Cache decorator with put() calls that call the {@link net.sf.ehcache.writer.CacheWriter} by default
 *
 * @author vinay
 */
public class ReadThroughWithPutWriterDelegtatedCache<K, V> extends EhcacheDecoratorAdapter {
    private static Logger LOG = LoggerFactory.getLogger(ReadThroughWithPutWriterDelegtatedCache.class);
    protected StoreConnector<K, V> storeConnector;
    protected Properties properties;

    public ReadThroughWithPutWriterDelegtatedCache(Ehcache underlyingCache, StoreConnector<K, V> storeConnector,Properties properties) {
        super(underlyingCache);
        this.storeConnector = storeConnector;
        this.properties=properties;
        storeConnector.initialize(this.properties);
        underlyingCache.registerCacheWriter(new DelegatingCacheWriter(storeConnector));
    }

    /**
     * TODO: Not called. why?
     */
    @Override
    public void initialise() {
        super.initialise();
        LOG.debug("Initialized");
    }

    @Override
    public void dispose() throws IllegalStateException {
        super.dispose();
        storeConnector.dispose();
        LOG.debug("disposed");
    }

    public StoreConnector<K, V> getStoreConnector() {
        return storeConnector;
    }

    public void setStoreConnector(StoreConnector<K, V> storeConnector) {
        this.storeConnector = storeConnector;
    }

    /**
     * Force a refresh from the backing store
     *
     * @param k
     * @return
     */
    public V refresh(K k) {
        V v = storeConnector.readFromStore(k);
        Element element = new Element(k, v);
        underlyingCache.put(element);
        return v;
    }

    /**
     * get from {@link org.terracotta.connections.StoreReader} if not present in cache
     *
     * @param key
     * @return
     * @throws IllegalStateException
     * @throws CacheException
     */
    @Override
    public Element get(Object key) throws IllegalStateException, CacheException {
        Element element = super.get(key);
        if (element == null) {
            V v = storeConnector.readFromStore((K) key);
            element = new Element(key, v);
            underlyingCache.put(element);
        }
        return element;
    }


    /**
     * get from {@link org.terracotta.connections.StoreReader} if not present in cache
     *
     * @param key
     * @return
     * @throws IllegalStateException
     * @throws CacheException
     */
    @Override
    public Element get(Serializable key) throws IllegalStateException, CacheException {
        Element element = super.get(key);
        if (element == null) {
            V v = storeConnector.readFromStore((K) key);
            element = new Element(key, v);
            underlyingCache.put(element);
        }
        return element;
    }

    @Override
    public Map<Object, Element> getAll(Collection<?> keys) throws IllegalStateException, CacheException {
        Map<Object, Element> elements = super.getAll(keys);
        for (Object key : keys) {
            if (elements.get(key) == null) {
                Element element = get(key);
                elements.put(key, element);
            }
        }
        return elements;
    }

    /**
     * just check in cache, not in the store
     *
     * @param key
     * @return
     * @throws IllegalStateException
     * @throws CacheException
     */
    @Override
    public Element getQuiet(Object key) throws IllegalStateException, CacheException {
        return super.getQuiet(key);
    }

    /**
     * just check in cache, not in the store
     *
     * @param key
     * @return
     * @throws IllegalStateException
     * @throws CacheException
     */
    @Override
    public Element getQuiet(Serializable key) throws IllegalStateException, CacheException {
        return super.getQuiet(key);
    }

    protected StoreWriter<K, V> storeWriter;


    @Override
    public void put(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
        super.putWithWriter(element);
    }

    @Override
    public void putAll(Collection<Element> elements) throws IllegalArgumentException, IllegalStateException, CacheException {
        for (Element element : elements)
            super.putWithWriter(element);
    }
}
