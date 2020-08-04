package com.atdeniel.games.controller;

import com.atdeniel.games.domain.ArtistRequest;
import com.atdeniel.games.domain.ArtistResponse;
import com.atdeniel.games.dto.Dev;
import com.atdeniel.games.service.ArtistService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/artists")
public class ArtistController {

    @Autowired
    Mapper mapper;

    @Autowired
    ArtistService artistService;

    @RequestMapping(value="/", method= RequestMethod.POST)
    public ArtistResponse updateOrSave(@RequestBody @Valid ArtistRequest artistRequest){
        Dev dev = mapper.map(artistRequest, Dev.class);
        Dev updatedDev = artistService.save(dev);
        ArtistResponse artistResponse = mapper.map(updatedDev, ArtistResponse.class);
        return artistResponse;
    }

}
