/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.albion.rest;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author matth
 */
public interface ServerRepository extends MongoRepository<Server, String> {

}