/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fabula.controller.image;

import com.fabula.model.accounts.Account;
import com.fabula.model.authorization.jwt.InvalidJwtException;
import com.fabula.model.domain.Domain;
import com.fabula.model.file.ImageThumbnail;
import com.fabula.repository.files.FileRepository;
import com.fabula.service.accounts.UserAndAccountService;
import com.fabula.service.authorization.AuthorizationService;
import com.fabula.service.files.FileService;
import com.fabula.service.files.ImageService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;
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
@CrossOrigin
@RequestMapping("/api")
public class ImageController {

    @Autowired
    UserAndAccountService accountsService;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    FileService fileService;
    @Autowired
    FileRepository fr;
    @Autowired
    ImageService imageService;

    @GetMapping("/thumbnails/{imageId}")
    public ResponseEntity<ImageThumbnail> getThumbnailById(@RequestHeader(name = "Authorization", required = false) String bearer, @PathVariable("imageId") UUID imageId) {
        Optional<ImageThumbnail> optionalFile = imageService.getThumbnail(imageId);
        if (optionalFile.isPresent()) {
            ImageThumbnail imageThumbnail = optionalFile.get();
            try {
                Optional<Account> optionalAccount = accountsService.decodeAccount(bearer);
                if (optionalAccount.isPresent()) {
                    Account account = optionalAccount.get();
                    if (account.hasDomain()) {
                        Domain domain = account.getDomain();
                        if (authorizationService.verify(account, domain, ImageThumbnail.class, HttpMethod.GET)) {
                            return new ResponseEntity<>(imageThumbnail, HttpStatus.OK);
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

    @GetMapping("/thumbnails/{imageId}/data")
    public ResponseEntity<byte[]> getThumbnailByIdData(@RequestHeader(name = "Authorization", required = false) String bearer, @PathVariable("imageId") UUID imageId) {
        Optional<ImageThumbnail> optionalFile = imageService.getThumbnail(imageId);
        if (optionalFile.isPresent()) {
            ImageThumbnail imageThumbnail = optionalFile.get();
            MediaType mediaType = MediaType.parseMediaType(imageThumbnail.getMediaType());
            try {
                Optional<Account> optionalAccount = accountsService.decodeAccount(bearer);
                if (optionalAccount.isPresent()) {
                    Account account = optionalAccount.get();
                    if (account.hasDomain()) {
                        Domain domain = account.getDomain();
//                        if (authorizationService.verify(account, domain, ImageThumbnail.class, HttpMethod.GET)) {
                        return ResponseEntity.ok().contentType(mediaType).body(imageThumbnail.getData());
//                        } else {
//                            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//                        }
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

    @GetMapping("/thumbnails")
    public ResponseEntity<List<ImageThumbnail>> listThumbnails(@RequestHeader(name = "Authorization", required = false) String bearer) {
        Iterable<ImageThumbnail> iterable = imageService.findAllTumbnails();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(StreamSupport.stream(iterable.spliterator(), false).toList());
    }
}
