/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.repository.authorization;

import com.fabula.model.authorization.IResource;
import com.fabula.model.authorization.Permission;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpMethod;

/**
 *
 * @author caio
 */
public interface PermissionRepository extends CrudRepository<Permission, Long> {
    
    Set<Permission> findByAction(HttpMethod action);
    Set<Permission> findByResourceAndAction(Class<? extends IResource> resource, HttpMethod action);
//    Set<Permission> findByResourceAndEfffectAndAction(Class<? extends IResource> resource, boolean effect, HttpMethod action);
    Optional<Permission> findByResourceAndEffectAndAction(Class<? extends IResource> resource, boolean effect, HttpMethod action);
}
