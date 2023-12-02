/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula;

import com.fabula.model.document.Document;
import com.fabula.model.accounts.Account;
import com.fabula.model.accounts.User;
import com.fabula.model.authorization.Permission;
import com.fabula.model.authorization.Role;
import com.fabula.model.domain.Domain;
import com.fabula.model.file.File;
import com.fabula.model.file.ImageThumbnail;
import com.fabula.repository.authorization.RoleRepository;
import com.fabula.repository.documents.PageDocumentRepository;
import com.fabula.repository.domain.DomainRepository;
import com.fabula.repository.files.FileRepository;
import com.fabula.service.accounts.UserAndAccountService;
import com.fabula.service.accounts.DomainService;
import com.fabula.service.authorization.AuthorizationService;
import com.fabula.service.documents.DocumentService;
import com.fabula.service.files.FileService;
import com.fabula.service.files.ImageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 *
 * @author caio
 */
@Component
public class Starter implements InitializingBean {

    @Autowired
    UserAndAccountService accountService;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    DomainService domainService;
    @Autowired
    DomainRepository domainRepository;
    @Autowired
    DocumentService documentService;
    @Autowired
    FileService fileService;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    ImageService imageService;
    @Autowired
    PageDocumentRepository pdr;

    @Override
    public void afterPropertiesSet() throws Exception {

        User rootUser = accountService.createUser("root@domain.com").get();
        Domain fabula = domainService.createRootDomain(rootUser, "Fabula").get();
        Domain fabulaBlog = domainService.createDomain(rootUser, "Fabula Blog", fabula).get();
        Domain fabulaProject = domainService.createDomain(rootUser, "Fabula Project", fabula).get();

        User demoUser1 = accountService.createUser("demo1@domain.com").get();
        User demoUser2 = accountService.createUser("demo2@domain.com").get();
        User demoUser3 = accountService.createUser("demo3@domain.com").get();

        byte[] straighSkeletonImageBytes = Files.readAllBytes(Paths.get("/home/caio/Imagens/Screenshot_20230202_180425.png"));
        File straighSkeletonImage = fileService.create("straight skeleton", straighSkeletonImageBytes, "image/png", fabula).get();
        Optional<ImageThumbnail> straighSkeletonImageThumbnail = imageService.makeThumbnail(straighSkeletonImage);

        byte[] sunflowerImageBytes = Files.readAllBytes(Paths.get("/home/caio/Imagens/sunflower.JPG"));
        File sunflowerImage = fileService.create("sunflower", sunflowerImageBytes, "image/jpg", fabula).get();
        Optional<ImageThumbnail> sunflowerImageThumbnail = imageService.makeThumbnail(sunflowerImage);

        Document doc1 = documentService.create("Sunflowers!", "Hello Document", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2\n\n- item 1\n   - subitem\n  - subitem2\n- item2", false, fabula, sunflowerImage).get();
        doc1.setTitleImage(sunflowerImage);
        doc1 = documentService.save(doc1);
        Document doc2 = documentService.create("Broken algorithm ", "Hello Document", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabula, straighSkeletonImage).get();
        doc2.setTitleImage(straighSkeletonImage);
        doc2 = documentService.save(doc2);
        Document doc3 = documentService.create("Document3", "Hello Document 3", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabulaBlog, null).get();
        doc3 = documentService.save(doc3);
        Document doc4 = documentService.create("Document4", "Hello Document 4", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabula, null).get();
        doc4 = documentService.save(doc4);
        Document doc5 = documentService.create("Document5", "Hello Document 5", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabula, null).get();
        doc5 = documentService.save(doc5);
        Document doc6 = documentService.create("Document6", "Hello Document 6", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabula, null).get();
        doc6 = documentService.save(doc6);
        Document doc7 = documentService.create("Document7", "Hello Document 7", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabula, null).get();
        doc7 = documentService.save(doc7);
        Document doc8 = documentService.create("Document8", "Hello Document 8", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabula, null).get();
        doc8 = documentService.save(doc8);
        Document doc9 = documentService.create("Document9", "Hello Document 9", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabula, null).get();
        doc9 = documentService.save(doc9);
        Document doc10 = documentService.create("Document10", "Hello Document 10", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabula, null).get();
        doc10 = documentService.save(doc10);
        Document doc11 = documentService.create("Document11", "Hello Document 11", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabula, null).get();
        doc11 = documentService.save(doc11);
        Document doc12 = documentService.create("Document12", "Hello Document 12", "# Title\n\n## Subtitle\n\ncontent\n\ncontent2", false, fabula, null).get();
        doc12 = documentService.save(doc12);

        Account rootFabulaAccount = accountService.createOrRecoverAccount(rootUser, fabula).get();
        Account rootFabulaBlogAccount = accountService.createOrRecoverAccount(rootUser, fabulaBlog).get();
        Account demoUser1FabulaAccount = accountService.createOrRecoverAccount(demoUser1, fabula).get();
        Account demoUser2FabulaBlogAccount = accountService.createOrRecoverAccount(demoUser2, fabulaBlog).get();
        Account demoUser2FabulaProjectAccount = accountService.createOrRecoverAccount(demoUser2, fabulaProject).get();
        Account demoUser3FabulaProjectAccount = accountService.createOrRecoverAccount(demoUser3, fabulaProject).get();
//

        HttpMethod[] actions = HttpMethod.values();
        Set<Permission> permissions = new HashSet<>();
        permissions.addAll(authorizationService.createOrRecoverPermissions(true, Document.class, actions));
        permissions.addAll(authorizationService.createOrRecoverPermissions(true, Permission.class, actions));
        permissions.addAll(authorizationService.createOrRecoverPermissions(true, User.class, actions));
        permissions.addAll(authorizationService.createOrRecoverPermissions(true, Account.class, actions));
        permissions.addAll(authorizationService.createOrRecoverPermissions(true, Domain.class, actions));
        permissions.addAll(authorizationService.createOrRecoverPermissions(true, File.class, actions));
////        permissions.addAll(authorizationService.createOrRecoverPermissions(true, Authorship.class, actions));
//
        Role fabulaAdmin = authorizationService.createRole("admin", fabula, permissions);
        fabulaAdmin.addAccount(rootFabulaAccount);
        fabulaAdmin.addAccount(rootFabulaBlogAccount);
        fabulaAdmin = authorizationService.saveRole(fabulaAdmin);

        Role fabulaDocumentAll = authorizationService.createRole("fabulaDocumentAll", fabula, authorizationService.createOrRecoverPermissions(true, Document.class, actions));
        fabulaDocumentAll.addAccount(demoUser1FabulaAccount);
        fabulaDocumentAll = authorizationService.saveRole(fabulaDocumentAll);

        Role fabulaDomainAll = authorizationService.createRole("fabulaDomainAll", fabula, authorizationService.createOrRecoverPermissions(true, Domain.class, actions));
        fabulaDomainAll.addAccount(demoUser1FabulaAccount);
        fabulaDomainAll = authorizationService.saveRole(fabulaDomainAll);

        Role fabulaFileAll = authorizationService.createRole("fabulaFileAll", fabula, authorizationService.createOrRecoverPermissions(true, File.class, actions));
        fabulaDomainAll.addAccount(demoUser1FabulaAccount);
        fabulaDomainAll = authorizationService.saveRole(fabulaDomainAll);

        Role fabulaBlogDocumentAll = authorizationService.createRole("fabulaReadAll", fabulaBlog, authorizationService.createOrRecoverPermissions(true, Domain.class, actions));
        fabulaBlogDocumentAll.addAccount(demoUser2FabulaBlogAccount);
        fabulaBlogDocumentAll = authorizationService.saveRole(fabulaBlogDocumentAll);

        Role fabulaProjectAccountAll = authorizationService.createRole("fabulaReadAll", fabulaProject, authorizationService.createOrRecoverPermissions(true, Account.class, actions));
        fabulaProjectAccountAll.addAccount(demoUser2FabulaProjectAccount);
        fabulaProjectAccountAll.addAccount(demoUser3FabulaProjectAccount);
        fabulaProjectAccountAll = authorizationService.saveRole(fabulaProjectAccountAll);

//        documentService.associate(doc1, fabula); 
//        domainService.deleteDomain(fabula);  
//        accountService.deleteAllAccounts(fabula);  
//        domainService.recursiveDeleteDomain(domainService.getDomain(fabula.getId()).get());
//        File file = new File("/home/caio/images/Screenshot_20230202_180425.png");
        System.err.println("\n\nTESTING PAGINATION");
        pdr.findByDomainAndRestrictedFalseWithPagination(fabula, PageRequest.of(0, 2)).getContent().stream().forEach((d) -> {
            System.err.println(d);
        });
        System.err.println("======");
        pdr.findByDomainWithPagination(fabula, PageRequest.of(0, 2)).getContent().stream().forEach((d) -> {
            System.err.println(d);
        });
        System.err.println("--------------");
    }

}
