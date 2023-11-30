/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.model.authorization;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Set;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.http.HttpMethod;

/**
 *
 * @author caio
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"resource", "effect", "action"}))
public class Permission implements IResource {

    @Id
    @GeneratedValue
    Long id;

    Class<? extends IResource> resource;
    Boolean effect;
    HttpMethod action;

    @ManyToMany(mappedBy = "permissions")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Set<Role> roles;

    public Permission() {
    }

    public Permission(Class<? extends IResource> resource, Boolean effect, HttpMethod action) {
        this.resource = resource;
        this.action = action;
        this.effect = effect;
    }

    public Long getId() {
        return id;
    }

    public Set<Role> getRoles() {
        return Set.copyOf(roles);
    }

    public Class<? extends IResource> getResource() {
        return resource;
    }

    public Boolean getEffect() {
        return effect;
    }

    public HttpMethod getAction() {
        return action;
    }
}
