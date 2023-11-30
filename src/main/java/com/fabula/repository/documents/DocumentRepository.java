/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.repository.documents;

import com.fabula.model.document.Document;
import com.fabula.model.domain.Domain;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author caio
 */
@Transactional 
public interface DocumentRepository extends CrudRepository<Document, UUID> {

    List<Document> findByTitle(String title);
    Set<Document> findByDomain(Domain domain);
    Set<Document> findByDomainAndRestrictedFalse(Domain domain);
//    List<Document> findByAuthor(String authorName);
}
