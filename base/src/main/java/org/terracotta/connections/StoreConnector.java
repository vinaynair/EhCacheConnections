package org.terracotta.connections;

import java.util.Properties;

/**
 * Connector interface to a backing store that can
 * read {@link  org.terracotta.connections.StoreReader} or write {@link org.terracotta.connections.StoreWriter}
 *
 * @author vinay
 */
public interface StoreConnector<K, V> extends StoreWriter<K, V>, StoreReader<K, V> {
    void initialize(Properties properties);
    void dispose();
}
