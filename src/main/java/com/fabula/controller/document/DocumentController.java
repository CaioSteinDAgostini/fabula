/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.controller.document;

import com.fabula.model.accounts.Account;
import com.fabula.model.authorization.jwt.InvalidJwtException;
import com.fabula.model.document.Document;
import com.fabula.model.domain.Domain;
import com.fabula.service.accounts.UserAndAccountService;
import com.fabula.service.authorization.AuthorizationService;
import com.fabula.service.documents.DocumentService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fabula.repository.documents.CrudDocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author caio
 */
@RestController
//@CrossOrigin
@RequestMapping("/api")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class.getSimpleName());

    @Autowired
    CrudDocumentRepository documentRepository;
    @Autowired
    DocumentService documentService;
    @Autowired
    AuthorizationService authorizationService;
//    @Autowired
//    AuthorshipRepository authorshipRepository;
    @Autowired
    UserAndAccountService accountsService;

    @CrossOrigin
    @GetMapping("/documents/{documentId}")
    public ResponseEntity<Document> getDocumentById(@RequestHeader(name = "Authorization", required = false) String bearer, @PathVariable("documentId") UUID documentId) {
        log.info("getDocumentById "+documentId);
        Optional<Document> optionalDocument = documentService.get(documentId);
        if (optionalDocument.isPresent()) {
            log.info("document is present");
            Document document = optionalDocument.get();
            if (!document.isRestricted()) {
                log.info("document is not restricted");
                return new ResponseEntity<>(document, HttpStatus.OK);
            } else {
                try {
                    Optional<Account> optionalAccount = accountsService.decodeAccount(bearer);
                    if (optionalAccount.isPresent()) {
                        Account account = optionalAccount.get();
                        if (account.hasDomain()) {
                            Domain domain = account.getDomain();
                            if (authorizationService.verify(account, domain, Document.class, HttpMethod.GET)) {
                                return new ResponseEntity<>(document, HttpStatus.OK);
                            } else {
                                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                            }
                        } else {
                            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                        }
                    } else {
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

                    }
                } catch (InvalidJwtException ex) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } catch (Exception ex) {
                    log.error("error", ex);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @GetMapping("/documents")
    public ResponseEntity<List<Document>> getDocuments(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page, @RequestParam(name = "size", required = false, defaultValue = "5") Integer size, @RequestHeader(name = "Authorization", required = false) String bearer) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            Optional<Account> optionalAccount = accountsService.decodeAccount(bearer);
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                if (account.hasDomain()) {
                    Domain domain = account.getDomain();
                    if (authorizationService.verify(account, domain, Document.class, HttpMethod.GET)) {
                        List<Document> documents = documentService.getAll(domain, pageRequest);
                        return new ResponseEntity<>(documents, HttpStatus.OK);

                    } else {
                        List<Document> documents = documentService.getAllNotRestricted(domain, pageRequest);
                        return new ResponseEntity<>(documents, HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            }
        } catch (InvalidJwtException ex) {
            log.error("error", ex);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PutMapping("/documents")
    public ResponseEntity<Document> putDocuments(@RequestHeader(name = "Authorization", required = false) String bearer, @RequestBody Document document) {
        log.info("PUT documents " + document.getId() + "  bearer "+bearer);
        try {
            Optional<Account> optionalAccount = accountsService.decodeAccount(bearer);
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                if (account.hasDomain()) {
                    Domain domain = account.getDomain();
//                    if (authorizationService.verify(account, domain, Document.class, HttpMethod.PUT)) {
                    Optional<Document> optionalDocument = documentService.get(document.getId());
                    if (optionalDocument.isPresent()) {
                        document.setId(optionalDocument.get().getId());
                        
                        ObjectMapper om = new ObjectMapper();
                        String json = om.writeValueAsString(document);
                        log.info("JSON ="+json);
                        
                        if (optionalDocument.get().getDomain().equals(domain)) {
                            document = documentService.save(document);
                            return new ResponseEntity<>(document, HttpStatus.OK);
                        } else {
                            log.info("UNAUTHORIZED DOMAIN ACCESS");
                            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                        }

                    } else {
                        log.info("NOT FOUND ");
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                } else {
                    log.info("UNAUTHORIZED NO DOMAIN GIVEN");
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
//                } else {
//                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//                }
            } else {
                log.info("UNAUTHORIZED NO ACCOUNT INFO AVAILABLE");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            }
        } catch (InvalidJwtException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            log.warn("PUT documents " + document, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @PostMapping("/documents")
    public ResponseEntity<Document> postDocument(@RequestHeader(name = "Authorization", required = false) String bearer, @RequestBody Document document) {
        try {
            Optional<Account> optionalAccount = accountsService.decodeAccount(bearer);
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                if (account.hasDomain()) {
                    Domain domain = account.getDomain();
                    if (authorizationService.verify(account, domain, Document.class, HttpMethod.POST)) {
                        Document newDocument = documentRepository.save(new Document(document.getTitle(), document.getSubtitle(), document.getContents(), document.isRestricted(), domain));
                        return new ResponseEntity<>(newDocument, HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            }
        } catch (InvalidJwtException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
