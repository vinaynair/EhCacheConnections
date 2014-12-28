package org.terracotta.connections.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.connections.StoreConnector;

import java.util.*;

/**
 * Created by vinay on 12/23/14.
 */
public class HashMapStoreConnector implements StoreConnector<String, String> {
    private static Logger LOG = LoggerFactory.getLogger(HashMapStoreConnector.class);

    private Properties properties;
    // Writer implementation
    //================
    public Map<String, String> INTERNAL_MAP = new HashMap<String, String>();

    @Override
    public void writeToStore(String k, String v) {
        INTERNAL_MAP.put(k, v);
    }

    @Override
    public String readFromStore(String k) {
        return INTERNAL_MAP.get(k);
    }

    @Override
    public void removeFromStore(String k) {
        INTERNAL_MAP.remove(k);
    }


    @Override
    public String updateFromStore(String s) {
        return INTERNAL_MAP.get(s);
    }

    @Override
    public Map<String, String> readFromStore(Collection<String> keys) {
        Map<String, String> strings = Collections.EMPTY_MAP;
        for (String key : keys)
            strings.put(key, INTERNAL_MAP.get(key));
        return strings;
    }

    @Override
    public void initialize(Properties properties) {
        this.properties=properties;
        LOG.debug("Initialized");
    }

    @Override
    public void dispose() {
        LOG.debug("Disposed");

    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
