package com.atdeniel.games.service.impl;

import com.atdeniel.games.dao.ArtistRepository;
import com.atdeniel.games.dto.Dev;
import com.atdeniel.games.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArtistServiceImpl implements ArtistService {

    @Autowired
    ArtistRepository dao;

    public Dev save(Dev dev){
        return dao.saveAndFlush(dev);
    }

}