/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.model.accounts;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author caio
 */
    @Embeddable
    public class AccountId implements Serializable{
        @Column(name = "domain")
        UUID domainId;
        @Column(name = "username")
        String username;

    public AccountId(UUID domainId, String username) {
        this.domainId = domainId;
        this.username = username;
    }

    public AccountId() {
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.domainId);
        hash = 29 * hash + Objects.hashCode(this.username);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AccountId other = (AccountId) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.domainId, other.domainId)) {
            return false;
        }
        return true;
    }

    
    
}
