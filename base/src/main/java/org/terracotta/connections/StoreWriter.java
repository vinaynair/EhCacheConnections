package org.terracotta.connections;

/**
 * Write to backing store
 */
public interface StoreWriter<K, V> {
    /**
     * Write the key value to the backend data source
     *
     * @param k
     * @param v
     */
    void writeToStore(K k, V v);

    /**
     * Remove the key from the backend data source
     *
     * @param k
     */
    void removeFromStore(K k);
}
