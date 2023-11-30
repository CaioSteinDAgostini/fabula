/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.model.domain;

import com.fabula.model.document.Document;
import com.fabula.model.accounts.Account;
import com.fabula.model.authorization.IResource;
import com.fabula.model.authorization.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author caio
 */
@Entity
public class Domain implements IResource {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "domain", cascade = CascadeType.ALL)
    Set<Account> accounts;

//    @OneToMany(mappedBy = "domain", cascade = CascadeType.MERGE)
    @OneToMany(mappedBy = "domain", cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}) 
    Set<Document> documents;

    @JsonIgnore
//    @JsonBackReference
    @ManyToOne
    Domain parent;
    
    @JsonIgnore
//    @JsonManagedReference
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    Set<Domain> children;

    @OneToMany(mappedBy = "domain", cascade = CascadeType.ALL)//, cascade = CascadeType.ALL)
    Set<Role> roles;

    private boolean restricted;

    public Domain() {
    }

    public Domain(String name) {
        this.name = name;
    }

    public Domain(String name, Domain parent) {
        this.name = name;
        this.parent = parent;
    }

    public Domain getParent() {
        return parent;
    }
    
    public void setParent(Domain parent){
        this.parent = parent;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.id);
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
        final Domain other = (Domain) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }
    
    public boolean isRoot(){
        return this.parent==null;
    }
    

}
