package org.hansung.oddeco;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.spi.PersistenceProvider;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.community.dialect.SQLiteDialect;
import org.hibernate.engine.transaction.jta.platform.internal.BitronixJtaPlatform;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.util.Map;

public class OddecoEntityManagerBuilder implements EntityManagerFactory {
    @SuppressWarnings("rawtypes")
    private static final Map PROPERTIES = Map.ofEntries(
            Map.entry(JdbcSettings.DIALECT, SQLiteDialect.class),
            Map.entry("hibernate.transaction.jta.platform", new BitronixJtaPlatform()),
            Map.entry("hibernate.hbm2ddl.auto", "update")
    );

    private final EntityManagerFactory entityManagerFactory;

    public OddecoEntityManagerBuilder(String jdbcUrl, Class<?>... entityClasses) {
        this(new HibernatePersistenceProvider(), jdbcUrl, entityClasses);
    }

    public OddecoEntityManagerBuilder(PersistenceProvider provider, String jdbcUrl, Class<?>... entityClasses) {
        this.entityManagerFactory = provider.createContainerEntityManagerFactory(new PersistenceUnit(jdbcUrl, entityClasses), PROPERTIES);
    }

    @Override
    public EntityManager createEntityManager() {
        return this.entityManagerFactory.createEntityManager();
    }

    @Override
    public EntityManager createEntityManager(Map map) {
        return this.entityManagerFactory.createEntityManager(map);
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType) {
        return this.entityManagerFactory.createEntityManager(synchronizationType);
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
        return this.entityManagerFactory.createEntityManager(synchronizationType, map);
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return this.entityManagerFactory.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return this.entityManagerFactory.getMetamodel();
    }

    @Override
    public boolean isOpen() {
        return this.entityManagerFactory.isOpen();
    }

    @Override
    public void close() {
        this.entityManagerFactory.close();
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.entityManagerFactory.getProperties();
    }

    @Override
    public Cache getCache() {
        return this.entityManagerFactory.getCache();
    }

    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return this.entityManagerFactory.getPersistenceUnitUtil();
    }

    @Override
    public void addNamedQuery(String name, Query query) {
        this.entityManagerFactory.addNamedQuery(name, query);
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return this.entityManagerFactory.unwrap(cls);
    }

    @Override
    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
        this.entityManagerFactory.addNamedEntityGraph(graphName, entityGraph);
    }
}
