package com.atdeniel.games.controller;

import javax.jms.JMSException;
import javax.naming.NamingException;

import com.atdeniel.games.jms.queue.services.SendService;
import com.atdeniel.games.service.ArtistService;
import com.atdeniel.games.jms.queue.services.impl.GetImpl;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

@RestController
@RequestMapping(value = "/developers")
public class DevController {
	
	@Autowired
	ArtistService artistService;

	@Autowired
	GetImpl getImpl;

	@Autowired
	SendService sendService;

	@Autowired
	Mapper mapper;

	//curl -X POST localhost:8080/developers/send -H 'Content-type:application/text' -d 'Name'
	// then and important send 'quit' when you stop sending msgs
	//curl -X POST localhost:8080/developers/send -H 'Content-type:application/text' -d 'quit'

	@RequestMapping(value="/", method=RequestMethod.POST)
	public String send(@RequestBody String data) throws JMSException, IOException, NamingException {

		/*Dev dev = new Dev(1L,
				"Atdeniel",
				"Git",
				"+57 311 222 3344",
				"1@git.com");*/

		BufferedReader msgStream = new BufferedReader(new InputStreamReader(System.in));
		String line = data;
		if (line != null && line.trim().length() != 0) {
			sendService.sendData(data);
		}
		return "Inserted to WL Queue.";
	}

	@RequestMapping(value="/", method=RequestMethod.GET)
	public String get() throws JMSException, IOException, NamingException {
		ArrayList<String> mensajes = getImpl.getQueue(getImpl);
		String pilaMensajes = "Mensaje recibido: ";

		for (String mensaje:mensajes){
			pilaMensajes = pilaMensajes + mensaje + " \n\t";
		}

		return pilaMensajes;
	}

}
