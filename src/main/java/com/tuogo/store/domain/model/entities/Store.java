package com.tuogo.store.domain.model.entities;

import lombok.Data;

import java.io.Serializable;

/**
 * Entity object - <code>Store</code>
 *
 * @author saadm
 */
@Data
public class Store implements Serializable {

    private String uidPk;
    private String sapCode;
    private String taxNr;
    private String title;
    private java.time.LocalDate closeDate;
}

