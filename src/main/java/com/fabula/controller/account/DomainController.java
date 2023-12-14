/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.controller.account;

import com.fabula.model.accounts.Account;
import com.fabula.model.accounts.User;
import com.fabula.model.authorization.jwt.InvalidJwtException;
import com.fabula.service.accounts.UserAndAccountService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fabula.model.domain.Domain;
import com.fabula.service.accounts.DomainService;
import com.fabula.service.authorization.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author caio
 */
@RestController
//@CrossOrigin
@RequestMapping("/api")
public class DomainController {

    @Autowired
    UserAndAccountService userAndAccountService;
    @Autowired
    DomainService domainService;
    @Autowired
    AuthorizationService authorizationService;

    @Operation(summary = "Get the domains the account has access to")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retrieved the domains",
                content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Domain.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
        @ApiResponse(responseCode = "403", description = "The account does not have the necessary permissions", content = @Content),
        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @CrossOrigin
    @GetMapping("/domains")
    public ResponseEntity<Set<Domain>> getDomains(@RequestHeader("Authorization") String bearer) {
        try {
            Optional<Account> optionalAccount = userAndAccountService.decodeAccount(bearer);
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                if (!account.hasDomain()) {
                    Set<Domain> domains = userAndAccountService.getAccounts(account.getUser()).stream().map((otherAccount) -> {
                        return otherAccount.getDomain();
                    }).collect(Collectors.toSet());

                    return new ResponseEntity<>(domains, HttpStatus.OK);
                } else {
                    if (authorizationService.verify(account, account.getDomain(), Domain.class, HttpMethod.GET)) {
                        return new ResponseEntity<>(domainService.getDomainAndChildren(account.getDomain()), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
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
    }

    @Operation(summary = "Get the domain identified by 'domainId'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retrieved the domain",
                content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Domain.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
        @ApiResponse(responseCode = "409", description = "There is already a Domain with conflicting unique fields", content = @Content),
        @ApiResponse(responseCode = "403", description = "The account does not have the necessary permissions", content = @Content),
        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @CrossOrigin
    @GetMapping("/domains/{domainId}")
    public ResponseEntity<Domain> getDomainById(@RequestHeader("Authorization") String bearer, @PathVariable("domainId") UUID id) {
        try {
            Optional<Domain> optionalDomain = userAndAccountService.getDomain(id);
            if (optionalDomain.isPresent()) {
                Domain domain = optionalDomain.get();
                if (domain.isRestricted()) {
                    try {
                        Optional<Account> optionalAccount = userAndAccountService.decodeAccount(bearer);
                        if (optionalAccount.isPresent()) {
                            Account account = optionalAccount.get();
                            if (account.hasDomain()) {

                                if (authorizationService.verify(account, domain, Domain.class, HttpMethod.GET)) {
                                    return new ResponseEntity<>(domain, HttpStatus.OK);
                                } else {
                                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);

                                }
                            } else {
                                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                            }
                        } else {
                            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                        }
                    } catch (InvalidJwtException ex) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>(domain, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(summary = "Create a domain that is child of account's domain")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
                content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Domain.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
        @ApiResponse(responseCode = "409", description = "There is already a Domain with conflicting unique fields", content = @Content),
        @ApiResponse(responseCode = "403", description = "The account does not have the necessary permissions", content = @Content),
        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)
    })
    @CrossOrigin
    @PostMapping("/domains")
    public ResponseEntity<Domain> createDomain(@RequestHeader("Authorization") String bearer, @RequestParam String name) {
        try {
            Optional<Account> optionalAccount = userAndAccountService.decodeAccount(bearer);
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                User user = account.getUser();
                if (account.hasDomain()) {
                    Domain accountDomain = account.getDomain();
                    if (authorizationService.verify(account, accountDomain, Domain.class, HttpMethod.POST)) {
                        Optional<Domain> optionalDomain = domainService.createDomain(account.getUser(), name, accountDomain);
                        if (optionalDomain.isPresent()) {
                            userAndAccountService.createOrRecoverAccount(user, accountDomain);
                            return new ResponseEntity<>(optionalDomain.get(), HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>(HttpStatus.CONFLICT);
                        }
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

    @Operation(summary = "Create a domain that is child of 'domainId'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
                content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Domain.class))}),
        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
        @ApiResponse(responseCode = "409", description = "There is already a Domain with conflicting unique fields", content = @Content),
        @ApiResponse(responseCode = "403", description = "The account does not have the necessary permissions", content = @Content),
        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @CrossOrigin
    @PostMapping("/domains/{domainId}")
    public ResponseEntity<Account> createDomain(@RequestHeader("Authorization") String bearer, @PathVariable("domainId") UUID domainId, @RequestParam String name) {
        try {
            Optional<Account> optionalAccount = userAndAccountService.decodeAccount(bearer);
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                User user = account.getUser();
                Optional<Domain> optionalTargetDomain = userAndAccountService.getDomain(domainId);
                if (account.hasDomain() && optionalTargetDomain.isPresent()) {
                    Domain targetDomain = optionalTargetDomain.get();
                    if (authorizationService.verify(account, targetDomain, Domain.class, HttpMethod.POST)) {
                        Optional<Domain> optionalDomain = domainService.createDomain(account.getUser(), name, targetDomain);
                        if (optionalDomain.isPresent()) {
                            Optional<Account> newAccount = userAndAccountService.createOrRecoverAccount(user, targetDomain);
                            return new ResponseEntity<>(newAccount.get(), HttpStatus.CREATED);
                        } else {
                            return new ResponseEntity<>(HttpStatus.CONFLICT);
                        }
                    } else {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);

                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);

            }
        } catch (InvalidJwtException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete the domain identified by 'domainId'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deleted", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
        @ApiResponse(responseCode = "403", description = "The account does not have the necessary permissions", content = @Content),
        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @CrossOrigin
    @DeleteMapping("/domain")
    public ResponseEntity<Void> deleteDomain(@RequestHeader("Authorization") String bearer, @RequestParam(required = false) UUID domainId) {
        try {
            Optional<Account> optionalAccount = userAndAccountService.decodeAccount(bearer);
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                Optional<Domain> optionalDomain = domainService.getDomain(domainId);
                if (optionalDomain.isPresent()) {
                    Domain domain = optionalDomain.get();
                    if (authorizationService.verify(account, domain, Domain.class, HttpMethod.DELETE)) {
                        domainService.recursiveDeleteDomain(domain);
                        return new ResponseEntity<>(HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }

                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);

                }

            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (InvalidJwtException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
