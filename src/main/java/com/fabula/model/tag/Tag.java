/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fabula.model.tag;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 *
 * @author caio
 */
@Entity
public class Tag {
    
    @Id
    String name;
}
