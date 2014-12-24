package org.terracotta.connections;

import java.util.Collection;
import java.util.Map;

/**
 * Read from backing store
 */
public interface StoreReader<K, V> {
    /**
     * read the value from the backend data source given the key
     *
     * @param k
     * @return
     */
    V readFromStore(K k);

    /**
     * read the collection from the backend data source given the collection of keys
     * @param keys
     * @return
     */
    Map<K,V> readFromStore(Collection<K> keys);

    /**
     * update the value
     * @param k
     */
    V updateFromStore(K k);

}
