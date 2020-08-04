package com.atdeniel.games.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atdeniel.games.dto.Dev;

public interface ArtistRepository extends JpaRepository<Dev, Long> {

}
