/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.service.authorization;

import com.fabula.model.accounts.Account;
import com.fabula.model.authorization.IResource;
import com.fabula.model.authorization.Permission;
import com.fabula.model.authorization.Role;
import com.fabula.model.domain.Domain;
import com.fabula.repository.authorization.PermissionRepository;
import com.fabula.repository.authorization.RoleRepository;
import com.fabula.service.accounts.UserAndAccountService;
import com.fabula.service.accounts.DomainService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 *
 * @author caio
 */
@Service
public class AuthorizationService {

    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserAndAccountService accountService;
    @Autowired
    DomainService domainService;
    @Autowired
    EntityManager em;

    @Value("${authorization.role.defaultAdminName}")
    private String ADMIN_ROLE;

    @Transactional
    public Role createRole(String name, Domain domain, Set<Permission> permissions) {
        em.refresh(em.merge(domain));
        Role role = new Role(name, domain, permissions);
        return roleRepository.save(em.merge(role));
    }

    public Role createAdminRole(Domain domain) {
        Role role = new Role(ADMIN_ROLE, domain, Set.of());
        return roleRepository.save(role);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public Permission createOrRecoverPermission(boolean effect, Class<? extends IResource> resource, HttpMethod action) {
        Optional<Permission> optionalPermission = permissionRepository.findByResourceAndEffectAndAction(resource, effect, action);
        if (optionalPermission.isPresent()) {
            return optionalPermission.get();
        } else {
            return permissionRepository.save(new Permission(resource, effect, action));
        }
    }

    public Set<Permission> createOrRecoverPermissions(boolean effect, Class<? extends IResource> resource, HttpMethod... actions) {
        return Stream.of(actions).map((action) -> {
            Optional<Permission> optionalPermission = permissionRepository.findByResourceAndEffectAndAction(resource, effect, action);
            if (optionalPermission.isPresent()) {
                return optionalPermission.get();
            } else {
                Permission permission = permissionRepository.save(new Permission(resource, effect, action));
                return permission;
            }
        }).collect(Collectors.toSet());
    }

    public boolean verify(Account account, Domain domain, Class<? extends IResource> resource, HttpMethod action) {
        if (account.hasDomain()) {
            Optional<Account> optionalVerifiedAccount = accountService.getAccount(account.getUser(), account.getDomain().getId());
            if (optionalVerifiedAccount.isPresent()) {
                Set<Domain> domains = domainService.getDomainAndChildren(account.getDomain());
                if (domains.contains(domain)) {
                    Set<Permission> permissions = permissionRepository.findByAction(action);
                    Set<Role> roles = roleRepository.findByAccountsAndPermissionsIn(account, permissions);
                    if (roles.isEmpty()) {
                        return false;
                    }

                    return roles.stream().map((role) -> {
                        return role.getPermissions();
                    }).flatMap(Set::stream).distinct().allMatch((permission) -> {
                        return permission.getEffect();
                    });
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
