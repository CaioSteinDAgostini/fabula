/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.model.authorization;

import com.fabula.model.accounts.Account;
import com.fabula.model.domain.Domain;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author caio
 */
@Entity
public class Role implements IResource {

    String name;
    @Id
    @GeneratedValue
    UUID id;
    @ManyToOne
    Domain domain;
    Boolean admin;

    @ManyToMany
    @JoinTable(
            name = "rolePermissions",
            joinColumns = @JoinColumn(name = "role"),
            inverseJoinColumns = @JoinColumn(name = "permission"))
    Set<Permission> permissions;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "roleAccounts",
            joinColumns = @JoinColumn(name = "role"),
            inverseJoinColumns = {
                @JoinColumn(name = "domain"),
                @JoinColumn(name = "username")})
    Set<Account> accounts;

    public Role() {
    }

    public Role(String name, Domain domain, Set<Permission> permissions) {
        this.name = name;
        this.domain = domain;
        this.permissions = permissions;
        this.accounts = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public Domain getDomain() {
        return domain;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public boolean addAccount(Account account) {
        if ((!this.accounts.contains(account)) && account.hasDomain() && account.getDomain().getId().equals(this.domain.getId())) {
            this.accounts.add(account);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeAccount(Account account) {
        return this.accounts.remove(account);
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

}
