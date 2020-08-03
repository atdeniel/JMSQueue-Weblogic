package com.atdeniel.games.jms.queue;
import com.atdeniel.games.dao.GameRepository;
import com.atdeniel.games.dto.Dev;
import com.atdeniel.games.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Hashtable;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Service
public class Get implements MessageListener{

    // Defines Server location
    public final static String SERVER="t3://localhost:7001/";
    // Defines the JNDI context factory.
    public final static String JNDI_FACTORY="weblogic.jndi.WLInitialContextFactory";
    // Defines the JMS context factory.   JNDI name for connection factory we can give any name
    public final static String JMS_FACTORY="FabricaJNDI";
    // Defines the queue.  JNDI name for Queue . Queue is for one to one communication
    public final static String QUEUE="MiColaJNDI";

    private QueueConnectionFactory qconFactory;
    private QueueConnection qcon;
    private QueueSession qsession;
    private QueueReceiver qreceiver;
    private Queue queue;
    private boolean quit = false;
    private static ArrayList mensajes = new ArrayList<String>();

    @Autowired
    GameRepository dao;

    /**
     * Message listener interface.
     * @param msg  message
     */
    public void onMessage(Message msg)
    {
        try {
            String msgText;
            if (msg instanceof TextMessage) {
                msgText = ((TextMessage)msg).getText();
            } else {
                msgText = msg.toString();
            }

            System.out.println("Message Received: "+ msgText );
            mensajes.add("Message Received: "+ msgText);

            Dev dev = new Dev();
            dev.setFirstName("GG");
            dao.saveAndFlush(dev);

            if (msgText.equalsIgnoreCase("quit")) {
                synchronized(this) {
                    quit = true;
                    this.notifyAll(); // Notify main thread to quit
                }
            }
        } catch (JMSException jmse) {
            System.err.println("An exception occurred: "+jmse.getMessage());
        }
    }
    /**
     * Creates all the necessary objects for receiving
     * messages from a JMS queue.
     *
     * @param   ctx    JNDI initial context
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
        qreceiver = qsession.createReceiver(queue);
        qreceiver.setMessageListener(this);
        qcon.start();
    }
    /**
     * Closes JMS objects.
     * @exception JMSException if JMS fails to close objects due to internal error
     */
    public void close()throws JMSException
    {
        qreceiver.close();
        qsession.close();
        qcon.close();
    }
    /**
     * main() method.
     *
     * @param args  WebLogic Server URL
     * @exception  Exception if execution fails
     */
    public static void main(String[] args) throws Exception {
        /*if (args.length != 1) {
            System.out.println("Usage: java examples.jms.queue.QueueReceive WebLogicURL");
            return;
        }*/
        InitialContext ic = getInitialContext();
        Get qr = new Get();
        qr.init(ic);
        System.out.println(
                "JMS Ready To Receive Messages (To quit, send a \"quit\" message).");
        // Wait until a "quit" message has been received.
        obtenerCola(qr);
    }

    public static ArrayList obtenerCola(Get qr) throws JMSException {
        synchronized(qr) {
            while (! qr.quit) {
                try {
                    qr.wait();
                } catch (InterruptedException ie) {}
            }
        }
        qr.close();
        return mensajes;
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
