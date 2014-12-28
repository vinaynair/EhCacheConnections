package org.terracotta.connections.ehcache.decorator;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.CacheDecoratorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.connections.StoreConnector;

import java.io.IOException;
import java.util.Properties;

/**
 * {@link net.sf.ehcache.constructs.CacheDecoratorFactory} to be used in ehcache xml config
 */
public class ReadThroughWithPutWriterDelegtatedCacheFactory extends CacheDecoratorFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ReadThroughWithPutWriterDelegtatedCacheFactory.class);

    @Override
    public Ehcache createDecoratedEhcache(Ehcache ehcache, Properties properties) {
        LOG.debug("Creating decorate cache for {}", ehcache.getName());
        String config = (String) properties.get("config");
        Properties prop = new Properties();
        try {
            prop.load(getClass().getResourceAsStream(config));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Connector config file " + config + " not found");
        }
        try {
            String strStoreConnectorClass = prop.getProperty("connector.class");
            Class storeConnectorClass = Class.forName(strStoreConnectorClass);
            StoreConnector storeConnector=(StoreConnector) storeConnectorClass.newInstance();
            Ehcache cache = new ReadThroughWithPutWriterDelegtatedCache(ehcache, storeConnector, prop);
            //ehcache.getCacheManager().replaceCacheWithDecoratedCache(ehcache, cache);
            return cache;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
        throw new UnsupportedOperationException("Could not create a decorated cache");
    }

    /**
     * @param ehcache
     * @param properties
     * @return
     */
    @Override
    public Ehcache createDefaultDecoratedEhcache(Ehcache ehcache, Properties properties) {
        LOG.debug("Creating default decorate cache for {}", ehcache.getName());
        return createDecoratedEhcache(ehcache, properties);
    }
}
