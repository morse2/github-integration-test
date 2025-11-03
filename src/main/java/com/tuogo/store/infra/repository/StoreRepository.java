package com.tuogo.store.infra.repository;

import com.tuogo.store.domain.model.entities.Store;

import java.util.List;
import java.util.Map;

/**
 * Repository interface for <code>Store</code>
 *
 * @author saadm
 */
public interface StoreRepository {

    /**
     * Finds one of <code>Store</code> by primary key
     *
     * @param pk primary key
     * @return <code>Store</code>
     */
    Store get(String pk);

    /**
     * Deletes one of <code>Store</code> by primary key
     *
     * @param pk primary key
     */
    void delete(String pk);

    /**
     * Saves a <code>Store</code> object to database. <br>
     * This maybe an insert or update operation.
     *
     * @param record <code>Store</code>
     */
    void save(Store record);

    /**
    * Saves a list of <code>Store</code> objects to database. <br>
    * This maybe an insert or update operation.
    *
    * @param records a list of <code>Store</code>
    */
    void saveAll(List<Store> records);

    /**
     * Finds a list of <code>Store</code> by parameters.
     *
     * @param params parameters
     * @return a list of <code>Store</code>
     */
    List<Store> find(Map<String, Object> params);
}

