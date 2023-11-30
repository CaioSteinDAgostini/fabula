/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.model.accounts;

import com.fabula.model.authorization.IResource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.Set;

/**
 *
 * @author caio
 */
@Entity
public class User implements IResource {

    @Id
    private String username;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDateTime;

//    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
//    Set<Authorship> autorships;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    Set<Account> accounts;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public LocalDateTime getCreationDateTime() {
        return this.creationDateTime;
    }

    public String getUsername() {
        return this.username;
    }

    @JsonIgnore
    public String getPassword() {
        return "senha";
    }

}
