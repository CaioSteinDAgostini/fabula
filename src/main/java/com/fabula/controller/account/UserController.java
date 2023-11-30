/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.controller.account;

import com.fabula.model.accounts.User;
import com.fabula.model.authorization.jwt.InvalidJwtException;
import com.fabula.repository.documents.DocumentRepository;
import com.fabula.service.accounts.UserAndAccountService;
import com.fabula.service.authorization.AuthorizationService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class UserController {

    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    UserAndAccountService accountService;
    @Autowired
    AuthorizationService authorizationService;

//    @Operation(summary = "Create a new user")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "201", description = "OK", content = @Content),
//        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
//        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            Optional<User> newUser = accountService.createUser(user.getUsername());
            if (newUser.isPresent()) {
                return new ResponseEntity<>(newUser.get(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @Operation(summary = "Delete the user itself")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "OK", content = @Content),
//        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
//        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String bearer) {
        try {
            Optional<User> optionalUser = accountService.decodeUser(bearer);
            User user;
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
                accountService.deleteUser(user.getPassword());
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (InvalidJwtException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
