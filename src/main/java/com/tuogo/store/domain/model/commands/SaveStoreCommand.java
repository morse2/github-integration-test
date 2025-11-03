package com.tuogo.store.domain.model.commands;


import com.tuogo.store.domain.model.entities.Store;
import lombok.Getter;
import lombok.Setter;

/**
 * SaveCommand object - <code>Store</code>
 *
 * @author saadm
 */
@Setter
@Getter
public class SaveStoreCommand {

    private String uidPk;
    private String sapCode;
    private String taxNr;
    private String title;
    private java.time.LocalDate closeDate;

    /**
     * Convert current command to <code>Store</code> entity object
     *
     * @return <code>Store</code>
     */
    public Store convertToStore() {
        Store entity = new Store();
        entity.setUidPk(uidPk);
        entity.setSapCode(sapCode);
        entity.setTaxNr(taxNr);
        entity.setTitle(title);
        entity.setCloseDate(closeDate);

        return entity;
    }

}

