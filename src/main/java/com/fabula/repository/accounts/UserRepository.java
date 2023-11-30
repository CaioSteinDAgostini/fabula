/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.repository.accounts;

import com.fabula.model.accounts.User;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author caio
 */
@Transactional 
public interface UserRepository extends CrudRepository<User, String> {
    
}
