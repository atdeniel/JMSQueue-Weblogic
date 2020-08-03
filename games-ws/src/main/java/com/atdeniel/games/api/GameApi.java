package com.atdeniel.games.api;

import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.Valid;

import com.atdeniel.games.service.GameService;
import com.atdeniel.games.jms.queue.Get;
import com.atdeniel.games.jms.queue.Send;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.atdeniel.games.dto.Dev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

@RestController
public class GameApi {
	
	@Autowired
	GameService gameService;

	@Autowired
	Get get;

	@Autowired
	Send send;

	@Autowired
	Mapper mapper;
	 
	@RequestMapping(value="/game", method=RequestMethod.POST)
	public GameResponse updateOrSave(@RequestBody @Valid GameRequest gameRequest){
		Dev dev = mapper.map(gameRequest, Dev.class);
		Dev updatedDev = gameService.save(dev);
	    GameResponse gameResponse = mapper.map(updatedDev, GameResponse.class);
	    
	    return gameResponse;
	}
	
	@RequestMapping(value="/send", method=RequestMethod.GET)
	public Dev getById() throws JMSException, IOException, NamingException {

		Dev dev = new Dev(1L,
				"Atdeniel",
				"Git",
				"+57 311 222 3344",
				"1@git.com");

		InitialContext ic = send.getInitialContext();
		send.init(ic);

		BufferedReader msgStream = new BufferedReader(new InputStreamReader(System.in));
		String line = dev.toString();
		if (line != null && line.trim().length() != 0) {
			send.send(line);
			send.send("123");
			send.send("ola k ase");
			send.send("quit");
		}


		return dev;
	}

	@RequestMapping(value="/get", method=RequestMethod.GET)
	public String getById2() throws JMSException, IOException, NamingException {

		//Get get=new Get();
		InitialContext ic = get.getInitialContext();
		get.init(ic);
		ArrayList<String> mensajes = get.obtenerCola(get);
		String pilaMensajes = "Mensaje recibido: ";

		for (String mensaje:mensajes){
			pilaMensajes = pilaMensajes + mensaje + " \n\t";
		}


		return pilaMensajes;
	}

}
