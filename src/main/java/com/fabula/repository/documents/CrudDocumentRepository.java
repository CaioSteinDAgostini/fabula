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
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author caio
 */
@Transactional
public interface CrudDocumentRepository extends CrudRepository<Document, UUID> {

    List<Document> findByTitle(String title);

    List<Document> findByDomain(Domain domain);

    List<Document> findByDomainAndRestrictedFalse(Domain domain);
}
