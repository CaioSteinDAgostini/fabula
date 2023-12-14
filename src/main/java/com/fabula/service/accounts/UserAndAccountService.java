/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.service.accounts;

import com.fabula.controller.document.DocumentController;
import com.fabula.model.accounts.Account;
import com.fabula.model.domain.Domain;
import com.fabula.model.accounts.User;
import com.fabula.model.authorization.jwt.InvalidJwtException;
import com.fabula.model.authorization.jwt.Jwt;
import com.fabula.repository.accounts.AccountRepository;
import com.fabula.repository.domain.DomainRepository;
import com.fabula.repository.accounts.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author caio
 */
@Component
@Service
public class UserAndAccountService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    DomainRepository domainRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    EntityManager em;

    private static final Logger log = LoggerFactory.getLogger(UserAndAccountService.class.getSimpleName());

    public static final long JWT_VALIDITY = 5 * 60 * 60;
    @Value("${jwt.secret}")
    private String secret;// = "secret";

    private static String DOMAINS = "domains";
    private static String DOMAIN = "domain";
    private static String ID = "id";

    public UserAndAccountService() {
    }

    public Optional<Jwt> convert(String bearer) {
        if (bearer != null && bearer.startsWith("Bearer ")) {
            try {
                return Optional.of(new Jwt(bearer.substring(7)));
            } catch (InvalidJwtException ex) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public Optional<User> decodeUser(String bearer) throws InvalidJwtException {
        Optional<Jwt> optionalJwt = convert(bearer);
        if (optionalJwt.isPresent()) {
            if (optionalJwt.get().getExpiration() > System.currentTimeMillis()) {// && optionalJwt.get().verifySignature(secret)) {
                String username = optionalJwt.get().getSubject();
                Optional<User> optionalUser = this.getUser(username);
                return optionalUser;
            } else {
                throw new InvalidJwtException();
            }
        } else {
            return Optional.empty();
        }
    }

    public Optional<Account> decodeAccount(String bearer) throws InvalidJwtException {
        log.info("decodeAccount");
        Optional<Jwt> optionalJwt = convert(bearer);
        if (optionalJwt.isPresent()) {
            if (optionalJwt.get().getExpiration() > System.currentTimeMillis()) {
                if (optionalJwt.get().has(DOMAIN)) {
                    Map domainMap = optionalJwt.get().getPayload().get(DOMAIN, LinkedHashMap.class);
                    UUID domainId = UUID.fromString(domainMap.get(ID).toString());
                    String username = optionalJwt.get().getSubject();
                    Optional<User> optionalUser = this.getUser(username);
                    if (optionalUser.isPresent()) {
                        log.info("decode account Fetching account for user "+ optionalUser.get().getUsername() +  " for domain "+domainId);
                        return this.getAccount(optionalUser.get(), domainId);
                    } else {
                        log.error("NO USERNAME AVAILABLE");
                        return Optional.empty();
                    }
                } else {
                    log.warn("JWT HAS NO DOMAIN");
                    throw new RuntimeException();
                }
            } else {
                log.warn("EXPIRED JWT");
                throw new InvalidJwtException();
            }
        } else {
            log.warn("JWT not present");
            return Optional.empty();
        }
    }

    public Optional<User> createUser(String username) {
        if (userRepository.existsById(username)) {
            return Optional.empty();
        } else {
            return Optional.of(userRepository.save(new User(username)));
        }
    }

    public Optional<User> getUser(String username) {
        return userRepository.findById(username);
    }

    public void deleteUser(String username) {
        this.deleteAllAccounts(username);
        userRepository.deleteById(username);
    }

    public void deleteAllAccounts(String username) {
        accountRepository.deleteAllByIdUsername(username);
    }

    public void deleteAllAccounts(Domain domain) {
        accountRepository.deleteAllByIdDomainId(domain.getId());
    }

    @Transactional
    public Optional<Account> createOrRecoverAccount(User user, Domain domain) {
        Optional<Account> optionalAccount = this.getAccount(user, domain.getId());
        if (optionalAccount.isPresent()) {
            return optionalAccount;
        } else {
            Account a = new Account(user, domain);
            optionalAccount = Optional.of(accountRepository.save(a));
        }
        return optionalAccount;
    }

    public Set<Account> getAccounts(User user) {
        return accountRepository.findByIdUsername(user.getUsername());
    }

    public Set<Account> getAccounts(Domain domain) {
        return accountRepository.findByIdDomainId(domain.getId());
    }

    public Optional<Account> getAccount(User user, UUID domainId) {
        Optional<Account> optional = accountRepository.findByIdUsernameAndIdDomainId(user.getUsername(), domainId);
        log.info("getAccount("+user.getUsername() + ", "+domainId+") returned "+optional);
        return optional;
    }

    public Optional<Domain> getDomain(UUID domainId) {
        return domainRepository.findById(domainId);
    }

    public String generateUserJwt(User user) {
        Map<String, Object> claims = new HashMap<>();
        Set<Domain> domains = this.getAccounts(user).stream().map((account) -> {
            return account.getDomain();
        }).collect(Collectors.toSet());
        claims.put(DOMAINS, domains);
        return jwt(claims, user.getUsername());
    }

    public String generateAccountJwt(Account account) throws InvalidJwtException {
        Map<String, Object> claims = new HashMap<>();
        Domain domain = account.getDomain();
        claims.put(DOMAIN, domain);
        return jwt(claims, account.getUser().getUsername());
    }

    private String jwt(Map<String, Object> claims, String subject) {
        Jwt jwt = new Jwt();
        claims.entrySet().forEach((entry) -> {
            jwt.setClaim(entry.getKey(), entry.getValue());
        });
        jwt.setSubject(subject);
        Long now = System.currentTimeMillis();
        jwt.setIssuedAt(now);
        jwt.setExpiration(now + JWT_VALIDITY * 1000);
        jwt.sign(secret);//.getBytes("UTF-8")
        return jwt.toString();
    }

    public void deleteAccount(Account acc) {
        accountRepository.deleteById(acc.getId());
    }
}
