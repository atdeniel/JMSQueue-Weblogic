package com.atdeniel.games.jms.queue;

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
public class Send
{
    public final static String SERVER="t3://localhost:7001/";
	// Defines the JNDI context factory.
	public final static String JNDI_FACTORY="weblogic.jndi.WLInitialContextFactory";
	// Defines the JMS context factory.   JNDI name for connection factory we can give any name
	public final static String JMS_FACTORY="FabricaJNDI";
	// Defines the queue.  JNDI name for Queue . Queue is for one to one communication
	public final static String QUEUE="MiColaJNDI";

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
	}

	/**
	 * Sends a message to a JMS queue.
	 *
	 * @param message  message to be sent
	 * @exception JMSException if JMS fails to send message due to internal error
	 */
	public void send(String message) throws JMSException {
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
	/** main() method.
	 *
	 * @param args WebLogic Server URL
	 * @exception Exception if operation fails
	 */
	public static void main(String[] args) throws Exception {
		/*args[0] = new String();
		args[0] = "t3://localhost:7001/";
		if (args.length != 1) {
			System.out.println("Usage: java examples.jms.queue.QueueSend WebLogicURL");
			return;
		}*/
		//InitialContext ic = getInitialContext(args[0]);
		InitialContext ic = getInitialContext();
		Send qs = new Send();
		qs.init(ic);
		readAndSend(qs);
		qs.close();
	}

	private static void readAndSend(Send qs)
			throws IOException, JMSException
	{
		BufferedReader msgStream = new BufferedReader(new InputStreamReader(System.in));
		String line=null;
		boolean quitNow = false;
		do {
			System.out.print("Enter message (\"quit\" to quit): \n");
			line = msgStream.readLine();
			if (line != null && line.trim().length() != 0) {
				qs.send(line);
				System.out.println("JMS Message Sent: "+line+"\n");
				quitNow = line.equalsIgnoreCase("quit");
			}
		} while (! quitNow);

	}


	public static InitialContext getInitialContext()
			throws NamingException
	{
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, SERVER);
		return new InitialContext(env);
	}
}

/*Right click on this file and go to properties than click run-debug setting and give the address where weblogic is running  */