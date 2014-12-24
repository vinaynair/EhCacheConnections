package org.terracotta.connections.ehcache.writer;

import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.operations.SingleOperationType;
import org.terracotta.connections.StoreConnector;

import java.util.Collection;

/**
 * Created by vinay on 12/23/14.
 */
public class DelegatingCacheWriter<K, V> implements CacheWriter {

    StoreConnector<K, V> storeConnector;

    public DelegatingCacheWriter(StoreConnector<K, V> storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Override
    public void writeAll(Collection<Element> collection) throws CacheException {
        for (Element element : collection)
            write(element);
    }

    @Override
    public void deleteAll(Collection<CacheEntry> collection) throws CacheException {
        for (CacheEntry cacheEntry : collection)
            delete(cacheEntry);
    }

    @Override
    public void write(Element element) throws CacheException {
        assert element != null;
        K k = (K) element.getObjectKey();
        V v = (V) element.getObjectValue();
        storeConnector.writeToStore(k, v);
    }

    @Override
    public void delete(CacheEntry cacheEntry) throws CacheException {
        storeConnector.removeFromStore((K) cacheEntry.getElement().getObjectKey());
    }


    // Unimplemented methods
    //=======================
    @Override
    public void throwAway(Element element, SingleOperationType singleOperationType, RuntimeException e) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CacheWriter clone(Ehcache ehcache) throws CloneNotSupportedException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // "blank" implementation
    //=======================
    @Override
    public void init() {

    }

    @Override
    public void dispose() throws CacheException {

    }


}
