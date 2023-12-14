/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.controller.files;

import com.fabula.model.accounts.Account;
import com.fabula.model.authorization.jwt.InvalidJwtException;
import com.fabula.model.domain.Domain;
import com.fabula.model.file.File;
import com.fabula.repository.files.FileRepository;
import com.fabula.service.accounts.UserAndAccountService;
import com.fabula.service.authorization.AuthorizationService;
import com.fabula.service.files.FileService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author caio
 */
@RestController
//@CrossOrigin
@RequestMapping("/api")
public class FileController {

    @Autowired
    UserAndAccountService accountsService;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    FileService fileService;
    @Autowired
    FileRepository fr;

    @CrossOrigin
    @GetMapping("/files/{fileId}")
    public ResponseEntity<File> getFileById(@RequestHeader(name = "Authorization", required = false) String bearer, @PathVariable("fileId") UUID fileId) {
        Optional<File> optionalFile = fileService.get(fileId);
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            try {
                Optional<Account> optionalAccount = accountsService.decodeAccount(bearer);
                if (optionalAccount.isPresent()) {
                    Account account = optionalAccount.get();
                    if (account.hasDomain()) {
                        Domain domain = account.getDomain();
                        if (authorizationService.verify(account, domain, File.class, HttpMethod.GET)) {
                            return new ResponseEntity<>(file, HttpStatus.OK);
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
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @GetMapping("/files/{fileId}/data")
    public ResponseEntity<byte[]> getFileByIdData(@RequestHeader(name = "Authorization", required = false) String bearer, @PathVariable("fileId") UUID fileId) {
        Optional<File> optionalFile = fileService.get(fileId);
        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            MediaType mediaType = MediaType.parseMediaType(file.getMediaType());
            try {
                Optional<Account> optionalAccount = accountsService.decodeAccount(bearer);
                if (optionalAccount.isPresent()) {
                    Account account = optionalAccount.get();
                    if (account.hasDomain()) {
                        Domain domain = account.getDomain();
//                            if (authorizationService.verify(account, domain, File.class, HttpMethod.GET)) {

                        return ResponseEntity.ok().contentType(mediaType).body(file.getData());
//                        return new ResponseEntity<byte[]>(file.getData(), HttpStatus.OK);
//                            } else {
//                                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//                            }
                    } else {
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } catch (InvalidJwtException ex) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (Exception ex) {
                ex.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
