/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.service.accounts;

import com.fabula.model.accounts.User;
import com.fabula.model.domain.Domain;
import com.fabula.repository.domain.DomainRepository;
import jakarta.persistence.EntityManager;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author caio
 */
@Service
public class DomainService {

    @Autowired
    DomainRepository domainRepository;
    @Autowired
    EntityManager em;
    Domain main;

    public Domain getRootDomain(){
        Set<Domain> parent = domainRepository.findByParent(null);
        if(parent.isEmpty()){
            throw new RuntimeException();
        }
        else{
            if(parent.size()>1){
                throw new RuntimeException();
            }
            else{
                return parent.iterator().next();
            }
        }
    }
    
    public Set<Domain> getPublicDomainIds() {
        return domainRepository.findByRestrictedFalse();
    }

    public Optional<Domain> getDomain(UUID domainId) {
        return this.domainRepository.findById(domainId);
    }

    public Domain saveDomain(Domain domain) {
//        if (em.contains(em)) {
//            Domain newDomain = em.merge(domain);
//            return this.domainRepository.save(newDomain);
//        } else {
        return this.domainRepository.save(domain);
//        }
    }

    public void deleteDomain(Domain domain) {
        this.domainRepository.deleteById(domain.getId());
    }

    public Set<Domain> getDomainAndChildren(Domain domain) {
        Set<Domain> answer = getChildren(domain);
        answer.add(domain);
        return answer;
    }

    public Set<Domain> getChildren(Domain domain) {
        Set<Domain> answer = new HashSet<>();
        LinkedList<Domain> open = new LinkedList<>();
        open.add(domain);
        while (!open.isEmpty()) {
            Domain current = open.pop();
            Set<Domain> children = domainRepository.findByParent(current);
            answer.addAll(children);
            open.addAll(children);
        }
        return answer;
    }

    public Set<Domain> getImediateChildren(Domain domain) {
        return domainRepository.findByParent(domain);
    }

    public Optional<Domain> createRootDomain(User responsible, String organizationName) {
        if (domainRepository.findByParent(null).isEmpty()) {
            return this.createDomain(responsible, organizationName, null);
        }
        else{
            return Optional.empty();
        }
    }

//    @Transactional
    public Optional<Domain> createDomain(User responsible, String organizationName, Domain parent) {
        Optional<Domain> optionalDomain = domainRepository.findByName(organizationName);
        if (optionalDomain.isPresent()) {
            return Optional.empty();
        } else {
            if (parent != null) {
                Optional<Domain> optionalParentDomain = domainRepository.findById(parent.getId());
                if (optionalParentDomain.isPresent()) {
                    optionalDomain = Optional.of(domainRepository.save(new Domain(organizationName, optionalParentDomain.get())));
                } else {
                    Set<Domain> mainSet = domainRepository.findByParent(null);
                    if (mainSet.isEmpty()) {
                        this.main = this.createDomain(responsible, organizationName, null).get();
                        return Optional.of(main);
                    } else {
                        return Optional.empty();
                    }
                }
            } else {
                optionalDomain = Optional.of(domainRepository.save(new Domain(organizationName)));
            }
        }

        return optionalDomain;

    }

    public void recursiveDeleteDomain(Domain domain) {

//        this.getImediateChildren(domain).forEach((child) -> {
//            recursiveDeleteDomain(child);
//        });
        domainRepository.delete(domain);
    }

    public void setMain(Domain domain) {
        if (this.main == null) {
            this.main = domain;
        }
    }

    public Domain getMain(Domain domain) {
        return this.main;
    }
}
