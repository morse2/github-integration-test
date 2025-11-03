package com.tuogo.store.application.internal.queryservices;

import com.tuogo.store.domain.model.entities.Store;


/**
 * Query Service interface - <code>Store</code>
 *
 * @author saadm
 */
public interface StoreQueryService {

    /**
     * Find a record of <code>Store</code> by primary key
     *
     * @param pk primary key
     * @return entity object
     */
    Store findByPK(String pk);
}

