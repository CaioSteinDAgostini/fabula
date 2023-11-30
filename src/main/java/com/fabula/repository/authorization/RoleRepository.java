/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.repository.authorization;

import com.fabula.model.accounts.Account;
import com.fabula.model.authorization.Permission;
import com.fabula.model.authorization.Role;
import com.fabula.model.domain.Domain;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author caio
 */
public interface RoleRepository extends CrudRepository<Role, UUID> {
    
    Set<Role> findByDomain(Domain domain);
    Set<Role> findByDomainAndPermissionsIn(Domain domain, Collection<Permission> permissions);
    Set<Role> findByDomainInAndPermissionsIn(Set<Domain> domains, Collection<Permission> permissions);
    Set<Role> findByAccountsAndPermissionsIn(Account account, Collection<Permission> permissions);
}
