/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.model.accounts;

import com.fabula.model.authorization.IResource;
import com.fabula.model.authorization.Role;
import com.fabula.model.domain.Domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author caio
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"username", "domain"}))
public class Account implements IResource {

    @JsonIgnore
    @EmbeddedId
    AccountId id;

    @ManyToOne
    @MapsId("username")
    @JoinColumn(name = "username")
    @NotNull
    User user;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDateTime;

    @ManyToOne//(cascade = CascadeType.ALL)
    @MapsId("domain")
    @JoinColumn(name = "domain")
    Domain domain;

    @ManyToMany(mappedBy = "accounts", cascade = CascadeType.ALL)
    Set<Role> roles;

    public Account() {
    }

    public Account(User user, Domain domain) {
        this.user = user;
        this.domain = domain;
        if(domain!=null){
        this.id = new AccountId(domain.getId(), user.getUsername());
        }
        else{
            this.id = new AccountId(null, user.getUsername());
        }
        this.creationDateTime = LocalDateTime.now();
    }

    public AccountId getId(){
        return this.id;
    }
    
    public User getUser() {
        return user;
    }

    public Domain getDomain() {
        return this.domain;
    }
    
    public void setDomain(Domain domain){
        this.domain = domain;
    }

    public boolean hasDomain() {
        return this.domain != null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
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
        final Account other = (Account) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
