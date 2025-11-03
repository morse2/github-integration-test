package com.tuogo.store.infra.repository.mapper;

import com.tuogo.store.domain.model.entities.Store;

import java.util.List;
import java.util.Map;

/**
 * DAO for entity <code>Store</code>.
 *
 * @author saadm
 */
public interface StoreDao {

    /**
     * Insert a new <code>Store</code>.
     *
     * @param record a object of <code>Store</code>.
     * @return effected rows
     */
    int insert(Store record);

    /**
     * Delete a <code>Store</code> by its primary key.
     *
     * @param id the uId(primary key) to use.
     * @return the number of <code>Store</code> were deleted.
     */
    int deleteByPrimaryKey(String id);

    /**
     * Finds one of <code>Store</code> by its primary key.
     *
     * @param id the primary key.
     * @return one of <code>Store</code>, or null if not found.
     */
    Store selectByPrimaryKey(String id);

    /**
     * Updates one of <code>Store</code>.
     *
     * @param record - a object of <code>Store</code>.
     * @return effected rows
     */
    int updateByPrimaryKey(Store record);

    /**
     * Finds <code>Store</code> by the given parameters.
     *
     * @param params search condition.
     * @return a list of <code>Store</code>.
     */
    List<Store> find(Map<String, Object> params);
}

