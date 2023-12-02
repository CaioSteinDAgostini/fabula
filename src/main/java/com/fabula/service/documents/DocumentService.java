/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.service.documents;

import com.fabula.model.document.Document;
import com.fabula.model.domain.Domain;
import com.fabula.model.file.File;
import com.fabula.service.files.FileService;
import com.fabula.service.files.ImageService;
import jakarta.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.fabula.repository.documents.CrudDocumentRepository;
import com.fabula.repository.documents.PageDocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author caio
 */
@Component
@Service
public class DocumentService {

    @Autowired
    CrudDocumentRepository crudDocumentRepository;
    @Autowired
    PageDocumentRepository pageDocumentRepository;
    @Autowired
    ImageService imageService;
    @Autowired
    FileService fileService;
//    @Autowired
//    EntityManager em;

    @Transactional
    public Optional<Document> create(String title, String subtitle, String content, boolean isPrivate, Domain domain, File titleImage) {
        if (domain == null) {
            throw new InvalidParameterException();
        }
        Document document = new Document(title, subtitle, content, isPrivate, domain);
        Optional<Document> optionalDocument = Optional.of(crudDocumentRepository.save(document));
        return optionalDocument;
    }

    public Optional<Document> get(UUID documentId) {
        return crudDocumentRepository.findById(documentId);
    }

    public Page<Document> getAll(Domain domain, Pageable page) {

        return this.pageDocumentRepository.findAllByDomainAndRestrictedFalseWithPagination(domain, page);
    }

    public List<Document> getAll(Domain domain) {
        return this.crudDocumentRepository.findByDomain(domain);
    }

    public Page<Document> getAllNotRestricted(Domain domain, Pageable page) {
        return this.pageDocumentRepository.findAllByDomainAndRestrictedFalseWithPagination(domain, page);
    }

    public List<Document> getAllNotRestricted(Domain domain) {
        return this.crudDocumentRepository.findByDomainAndRestrictedFalse(domain);
    }

    public Document save(Document document) {
        return crudDocumentRepository.save(document);
    }

    public void parseImageReferences(Document document) {

    }

    public void parseDocumentReferences(Document document) {

    }
}
