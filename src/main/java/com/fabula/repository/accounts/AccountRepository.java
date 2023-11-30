/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.repository.accounts;

import com.fabula.model.accounts.Account;
import com.fabula.model.accounts.AccountId;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author caio
 */
@Transactional 
public interface AccountRepository extends CrudRepository<Account,AccountId> {
    
    public Set<Account> findByIdDomainId(UUID domainId);
    public Set<Account> findByIdUsername(String username);
    public Optional<Account> findByIdUsernameAndIdDomainId(String username, UUID domainId);
    public void deleteAllByIdUsername(String username);
    public void deleteAllByIdDomainId(UUID domainId);
}
