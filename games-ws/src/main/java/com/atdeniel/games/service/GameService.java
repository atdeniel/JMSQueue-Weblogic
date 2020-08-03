package com.atdeniel.games.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atdeniel.games.dao.GameRepository;
import com.atdeniel.games.dto.Dev;

@Service
public class GameService {
    @Autowired
    GameRepository dao;
     
    public Dev save(Dev dev){
        return dao.saveAndFlush(dev);
    }


}
