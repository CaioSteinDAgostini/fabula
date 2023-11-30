/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.service.documents;

import com.fabula.model.document.Document;
import com.fabula.model.domain.Domain;
import com.fabula.model.file.File;
import com.fabula.model.file.ImageThumbnail;
import com.fabula.repository.documents.DocumentRepository;
import com.fabula.service.files.FileService;
import com.fabula.service.files.ImageService;
import jakarta.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author caio
 */
@Component
@Service
public class DocumentService {

    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    ImageService imageService;
    @Autowired
    FileService fileService;
//    @Autowired
//    EntityManager em;

    
    
    @Transactional
    public Optional<Document> create(String title, String subtitle, String content, boolean isPrivate, Domain domain, File titleImage) {
        if(domain==null){
            throw new InvalidParameterException();
        }
        Document document = new Document(title, subtitle, content, isPrivate, domain); 
        Optional<Document> optionalDocument = Optional.of(documentRepository.save(document));
        return optionalDocument;
    }
    
    public Optional<Document> get(UUID documentId){
        return documentRepository.findById(documentId);
    }
    
    public Set<Document> getAll(Domain domain){
        return this.documentRepository.findByDomain(domain);
    }

        public Set<Document> getAllNotRestricted(Domain domain){
        return this.documentRepository.findByDomainAndRestrictedFalse(domain);
    }
        
    public Document save(Document document){
        return documentRepository.save(document);
    }
    
    public void parseImageReferences(Document document){
        
    }
    
    public void parseDocumentReferences(Document document){
        
    }
}
