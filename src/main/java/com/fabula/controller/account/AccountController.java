/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.controller.account;

import com.fabula.model.accounts.Account;
import com.fabula.model.authorization.jwt.InvalidJwtException;
import com.fabula.service.accounts.UserAndAccountService;
import com.fabula.service.authorization.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
public class AccountController {

    @Autowired
    UserAndAccountService accountsService;
    @Autowired
    AuthorizationService authorizationService;

    @Operation(summary = "Get the accounts available for the user for the given account token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid headers or parameters", content = @Content),
        @ApiResponse(responseCode = "403", description = "The account does not have the necessary permissions", content = @Content),
        @ApiResponse(responseCode = "501", description = "Internal server error", content = @Content)})
    @CrossOrigin
    @GetMapping("/accounts")
    public ResponseEntity<Account> getAccount(@RequestHeader("Authorization") String bearer) {
        try {
            Optional<Account> optionalAccount = accountsService.decodeAccount(bearer);
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                return new ResponseEntity<>(account, HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            }
        } catch (InvalidJwtException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
