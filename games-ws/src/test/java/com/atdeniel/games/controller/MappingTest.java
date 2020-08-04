package com.atdeniel.games.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.atdeniel.games.domain.GameRequest;
import org.dozer.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.atdeniel.games.dto.Dev;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MappingTest {
	
	@Autowired
	Mapper mapper;
	
	@Test
	public void fromRequestToEntity() {
		Dev c = new Dev(1L, "John", "Doe", "+57 312 222 3344", "john@sinbugs.com");
		GameRequest req = mapper.map(c, GameRequest.class);
		
		assertThat(req)
			.hasFieldOrPropertyWithValue("id", c.getId())
			.hasFieldOrPropertyWithValue("firstName", c.getFirstName())
			.hasFieldOrPropertyWithValue("lastName", c.getLastName())
			.hasFieldOrPropertyWithValue("phoneNumber", c.getPhoneNumber())
			.hasFieldOrPropertyWithValue("email", c.getEmail());
	}

}
