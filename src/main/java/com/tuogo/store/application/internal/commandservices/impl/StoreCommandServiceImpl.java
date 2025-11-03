package com.tuogo.store.application.internal.commandservices.impl;

import com.tuogo.store.application.internal.commandservices.StoreCommandService;
import com.tuogo.store.application.internal.queryservices.StoreQueryService;
import com.tuogo.store.infra.repository.StoreRepository;
import com.tuogo.store.domain.model.commands.SaveStoreCommand;
import com.tuogo.store.domain.model.commands.DeleteStoreCommand;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * Command Service implementation for - <code>Store</code>
 *
 * @author saadm
 */
@Slf4j
@Service("storeCommandService")
public class StoreCommandServiceImpl implements StoreCommandService {

    @Resource
    private StoreQueryService storeQueryService;
    @Resource
    private StoreRepository storeRepository;

    @Override
    public void save(SaveStoreCommand command) {
        storeRepository.save(command.convertToStore());
    }

    @Override
    public void delete(DeleteStoreCommand command) {
        storeRepository.delete(command.uidPk());
    }
}

