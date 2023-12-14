/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.controller.account;

import com.fabula.model.accounts.Account;
import com.fabula.model.accounts.User;
import com.fabula.model.authorization.jwt.InvalidJwtException;
import com.fabula.model.domain.Domain;
import com.fabula.service.accounts.UserAndAccountService;
import com.fabula.service.accounts.DomainService;
import com.fabula.service.authorization.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin
@RequestMapping("/api")
public class DomainAccountsController {

    @Autowired
    UserAndAccountService accountsService;
    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    DomainService domainService;

    @Operation(summary = "Get the user's account for the domain identified by 'domainId'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
        @ApiResponse(responseCode = "403", description = "The account does not have the necessary permissions", content = @Content),
        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @CrossOrigin
    @GetMapping("/domains/{domainId}/accounts")
    public ResponseEntity<Set<Account>> listAccounts(@RequestHeader("Authorization") String bearer, @PathVariable("domainId") UUID domainId) {
        try {
            Optional<Account> optionalAccount = accountsService.decodeAccount(bearer);
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                if (account.hasDomain()) {
                    Optional<Domain> optionalTargetDomain = domainService.getDomain(domainId);
                    if (optionalTargetDomain.isPresent()) {
                        Domain domain = optionalTargetDomain.get();
                        if (authorizationService.verify(account, domain, Account.class, HttpMethod.GET)) {
                            Set<Account> accounts = accountsService.getAccounts(domain);
                            return new ResponseEntity<>(accounts, HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                        }
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

                    }

                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            }
        } catch (InvalidJwtException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Create an account for the domain identified by 'domainId'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "OK", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
        @ApiResponse(responseCode = "403", description = "The account does not have the necessary permissions", content = @Content),
        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @CrossOrigin
    @PostMapping("/domains/{domainId}/accounts")
    public ResponseEntity<Account> createAccount(@RequestHeader("Authorization") String bearer, @RequestParam String username, @PathVariable("domainId") UUID domainId) {
        try {
            Optional<Account> optionalAccount = accountsService.decodeAccount(bearer);

            if (optionalAccount.isPresent()) {
                Optional<User> optionalNewAccountUser = accountsService.getUser(username);
                if (optionalNewAccountUser.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                Account account = optionalAccount.get();
                if (account.hasDomain()) {

                    Optional<Domain> optionalOrganization = accountsService.getDomain(domainId);
                    if (optionalOrganization.isPresent()) {
                        Domain newAccountDomain = optionalOrganization.get();
                        if (authorizationService.verify(account, newAccountDomain, Account.class, HttpMethod.POST)) {
                            Optional<Account> optionalNewAccount = accountsService.createOrRecoverAccount(optionalNewAccountUser.get(), newAccountDomain);
                            if (optionalNewAccount.isPresent()) {
                                return new ResponseEntity<>(optionalNewAccount.get(), HttpStatus.CREATED);
                            } else {
                                return new ResponseEntity<>(HttpStatus.CONFLICT);
                            }
                        } else {
                            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                        }

                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            }
        } catch (InvalidJwtException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete an account for the domain identified by 'domainId'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
        @ApiResponse(responseCode = "403", description = "The account does not have the necessary permissions", content = @Content),
        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @CrossOrigin
    @DeleteMapping("/domains/{domainId}/accounts")
    public ResponseEntity<Account> deleteAccount(@RequestHeader("Authorization") String bearer, @RequestParam String username, @PathVariable("domainId") UUID domainId) {
        try {
            Optional<Account> optionalAccount = accountsService.decodeAccount(bearer);

            if (optionalAccount.isPresent()) {
                Optional<User> optionalNewAccountUser = accountsService.getUser(username);
                if (optionalNewAccountUser.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                Account account = optionalAccount.get();
                if (account.hasDomain()) {

                    Optional<Domain> optionalOrganization = accountsService.getDomain(domainId);
                    if (optionalOrganization.isPresent()) {
                        Domain newAccountDomain = optionalOrganization.get();
                        if (authorizationService.verify(account, newAccountDomain, Account.class, HttpMethod.POST)) {
                            Optional<Account> optionalNewAccount = accountsService.createOrRecoverAccount(optionalNewAccountUser.get(), newAccountDomain);
                            if (optionalNewAccount.isPresent()) {
                                return new ResponseEntity<>(optionalNewAccount.get(), HttpStatus.CREATED);
                            } else {
                                return new ResponseEntity<>(HttpStatus.CONFLICT);
                            }
                        } else {
                            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                        }

                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            }
        } catch (InvalidJwtException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
