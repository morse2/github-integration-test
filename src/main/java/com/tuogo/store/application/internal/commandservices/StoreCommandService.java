package com.tuogo.store.application.internal.commandservices;

import com.tuogo.store.domain.model.commands.SaveStoreCommand;
import com.tuogo.store.domain.model.commands.DeleteStoreCommand;

/**
 * Command Service interface - <code>Store</code>
 *
 * @author saadm
 */
public interface StoreCommandService {

    /**
     * Save or update a command object - <code>SaveStoreCommand</code>
     *
     * @param command <code>SaveStoreCommand</code>
     */
    void save(SaveStoreCommand command);

    /**
     * Delete a command object - <code>DeleteStoreCommand</code>
     *
     * @param command <code>DeleteStoreCommand</code>
     */
    void delete(DeleteStoreCommand command);
}

