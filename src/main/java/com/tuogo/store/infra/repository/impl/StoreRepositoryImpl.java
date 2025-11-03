package com.tuogo.store.infra.repository.impl;

import com.tuogo.store.domain.model.entities.Store;
import com.tuogo.store.infra.repository.StoreRepository;
import com.tuogo.store.infra.repository.mapper.StoreDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Repository implementation for <code>Store</code>
 *
 * @author saadm
 */
@Slf4j
@Transactional
@Repository("storeRepository")
public class StoreRepositoryImpl implements StoreRepository {

    @Resource
    private StoreDao storeDao;

    @Override
    public Store get(String pk) {
        return storeDao.selectByPrimaryKey(pk);
    }

    @Override
    public void delete(String pk) {
        storeDao.deleteByPrimaryKey(pk);
    }

    @Override
    public void save(Store record) {
        storeDao.insert(record);
    }

    @Override
    public void saveAll(List<Store> records) {
        if (records == null || records.isEmpty()) return;

        records.forEach(this::save);
    }

    @Override
    public List<Store> find(Map<String, Object> params) {
        return storeDao.find(params);
    }
}

