package com.atdeniel.games.service.impl;

import com.atdeniel.games.dao.GameRepository;
import com.atdeniel.games.dto.Dev;
import com.atdeniel.games.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    GameRepository dao;

    public Dev save(Dev dev){
        return dao.saveAndFlush(dev);
    }

}