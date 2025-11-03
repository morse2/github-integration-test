package com.tuogo.store.application.internal.queryservices.impl;

import com.tuogo.store.application.internal.queryservices.StoreQueryService;
import com.tuogo.store.domain.model.entities.Store;
import com.tuogo.store.infra.repository.StoreRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Query Service implementation - <code>Store</code>
 *
 * @author saadm
 */
@Slf4j
@Service("storeQueryService")
public class StoreQueryServiceImpl implements StoreQueryService {

    @Resource
    private StoreRepository storeRepository;

    @Override
    public Store findByPK(String pk) {
        return Optional.ofNullable(pk).map(storeRepository::get).orElse(null);
    }
}

