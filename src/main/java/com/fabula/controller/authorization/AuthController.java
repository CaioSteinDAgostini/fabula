/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.controller.authorization;

import com.fabula.JwtUserRequest;
import com.fabula.JwtResponse;
import com.fabula.model.accounts.Account;
import com.fabula.model.accounts.User;
import com.fabula.model.authorization.jwt.InvalidJwtException;
import com.fabula.service.accounts.UserAndAccountService;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserAndAccountService accountsService;

//    @Operation(summary = "Get an authentication token (Authentication : Bearer) for the user or refresh an existing one")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "OK", content = @Content),
//        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
//        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @PostMapping("/authentication")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtUserRequest authenticationRequest) {
        try {
            final Optional<User> optionalUser = accountsService.getUser(authenticationRequest.getUsername());
            if (optionalUser.isPresent()) {
                String token = accountsService.generateUserJwt(optionalUser.get());
                return ResponseEntity.ok(new JwtResponse(token));
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (SecurityException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @Operation(summary = "Get an authentication token (Authentication : Bearer) for the the user's account for the domain identified by 'domainId'")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "OK", content = @Content),
//        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
//        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @PostMapping("/authorization")
    public ResponseEntity<JwtResponse> authorize(@RequestHeader("Authorization") String bearer,
            @RequestParam UUID domainId,
            @RequestParam(required = false) String grantType
    ) {
        try {
            System.err.println("authorize " + bearer);
            Optional<User> optionalUser = accountsService.decodeUser(bearer);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                Optional<Account> optionalAccount = accountsService.getAccount(user, domainId);
                if (optionalAccount.isPresent()) {
                    String token = accountsService.generateAccountJwt(optionalAccount.get());
                    return ResponseEntity.ok(new JwtResponse(token));
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (InvalidJwtException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
