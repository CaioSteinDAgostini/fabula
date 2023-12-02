/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.repository.documents;

import com.fabula.model.document.Document;
import com.fabula.model.domain.Domain;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author caio
 */
@Transactional
public interface PageDocumentRepository extends PagingAndSortingRepository<Document, UUID>, JpaRepository<Document, UUID> {

//    @Query("SELECT t FROM Tutorial t")
//Page<Tutorial> findAllWithPagination(Pageable pageable);
//
//@Query("SELECT t FROM Tutorial t WHERE t.published=?1")
//Page<Tutorial> findByPublishedWithPagination(boolean isPublished, Pageable pageable);
//  
//@Query("SELECT t FROM Tutorial t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', ?1,'%'))")
//Page<Tutorial> findByTitleWithPagination(String title, Pageable pageable);
//    List<Document> findAllByTitle(String title, Pageable pageable);
    @Query(value = "SELECT document FROM Document document WHERE document.domain = :domain")
    Page<Document> findAllByDomainWithPagination(Domain domain, Pageable pageable);

    @Query(value = "SELECT document FROM Document document WHERE document.domain = :domain AND not document.restricted" )
    Page<Document> findAllByDomainAndRestrictedFalseWithPagination(Domain domain, Pageable pageable);
//    List<Document> findByAuthor(String authorName);
}
