package com.atdeniel.games.jms.queue.services.impl;

import com.atdeniel.games.jms.queue.services.SendService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/** This example shows how to establish a connection
 * and send messages to the JMS queue. The classes in this
 * package operate on the same JMS queue. Run the classes together to
 * witness messages being sent and received, and to browse the queue
 * for messages. The class is used to send messages to the queue.
 *
 * @author Copyright (c) 1999-2005 by BEA Systems, Inc. All Rights Reserved.
 */
@Service
@PropertySource("classpath:/application.properties")
public class SendImpl implements SendService
{
	// Defines Server location
	@Value("${server}")
	private String SERVER ;
	// Defines the JNDI context factory.
	@Value("${jndi.factory}")
	private String JNDI_FACTORY ;
	// Defines the JMS context factory.   JNDI name for connection factory we can give any name
	@Value("${jms.factory}")
	private String JMS_FACTORY;
	// Defines the queue.  JNDI name for Queue . Queue is for one to one communication
	@Value("${queue}")
	private String QUEUE;

	//Connection factory and Queue are within JMS module, sender and reciever have to go thorough Jms module.they cannot talk to jms server directly
	//JMS server uses connection factory in order to communicate jms server with file store.
	//one Jms module(ex queue) can target to many Jms server
	//subdeployement is a group of Jms server
	//Queue can only be target to JMS server

	private QueueConnectionFactory qconFactory;
	private QueueConnection qcon;
	private QueueSession qsession;
	private QueueSender qsender;
	private Queue queue;
	private TextMessage msg;
	private Boolean queueUp = false;

	/**
	 * Creates all the necessary objects for sending
	 * messages to a JMS queue.
	 *
	 * @param ctx JNDI initial context
	 * @exception NamingException if operation cannot be performed
	 * @exception JMSException if JMS fails to initialize due to internal error
	 */
	public void init(Context ctx)
			throws NamingException, JMSException
	{
		qconFactory = (QueueConnectionFactory) ctx.lookup(JMS_FACTORY);
		qcon = qconFactory.createQueueConnection();
		qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		queue = (Queue) ctx.lookup(QUEUE);
		qsender = qsession.createSender(queue);
		msg = qsession.createTextMessage();
		qcon.start();
		queueUp = true;
	}

	/**
	 * Sends a message to a JMS queue.
	 *
	 * @param message  message to be sent
	 * @exception JMSException if JMS fails to send message due to internal error
	 */
	public void sendData(String message) throws JMSException, NamingException {
		if (!queueUp) {
			InitialContext ic = getInitialContext();
			init(ic);
		}
		msg.setText(message);
		qsender.send(msg);
	}

	/**
	 * Closes JMS objects.
	 * @exception JMSException if JMS fails to close objects due to internal error
	 */
	public void close() throws JMSException {
		qsender.close();
		qsession.close();
		qcon.close();
	}

	/**
	 * Read and send to WebLogic Queue
	 *
	 * @param qs
	 * @throws IOException
	 * @throws JMSException
	 * @throws NamingException
	 */
	private static void readAndSend(SendImpl qs)
			throws IOException, JMSException, NamingException {
		BufferedReader msgStream = new BufferedReader(new InputStreamReader(System.in));
		String line=null;
		boolean quitNow = false;
		do {
			System.out.print("Enter message (\"quit\" to quit): \n");
			line = msgStream.readLine();
			if (line != null && line.trim().length() != 0) {
				qs.sendData(line);
				System.out.println("JMS Message Sent: "+line+"\n");
				quitNow = line.equalsIgnoreCase("quit");
			}
		} while (! quitNow);

	}

	/**
	 * Return the InitialContext
	 * @return InitialContext
	 * @throws NamingException
	 */

	public  InitialContext getInitialContext()
			throws NamingException
	{
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, SERVER);
		return new InitialContext(env);
	}
}
