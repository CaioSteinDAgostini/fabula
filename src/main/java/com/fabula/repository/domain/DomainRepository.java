/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.repository.domain;

import com.fabula.model.domain.Domain;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author caio
 */
@Transactional 
public interface DomainRepository extends CrudRepository<Domain, UUID> {

    Optional<Domain> findByName(String name);
    Set<Domain> findByParent(Domain parent);
    Set<Domain> findByRestrictedFalse();
}
