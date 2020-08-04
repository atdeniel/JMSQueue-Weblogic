package com.atdeniel.games.jms.queue.services.impl;
import com.atdeniel.games.dao.GameRepository;
import com.atdeniel.games.dto.Dev;
import com.atdeniel.games.jms.queue.services.GetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Hashtable;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Service
@PropertySource("classpath:/application.properties")
public class GetImpl implements MessageListener, GetService {

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

    private QueueConnectionFactory qconFactory;
    private QueueConnection qcon;
    private QueueSession qsession;
    private QueueReceiver qreceiver;
    private Queue queue;
    private boolean quit = false;
    private static ArrayList mensajes = new ArrayList<String>();
    private boolean queueUp = false;

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
     * Get Queue from WebLogic
     * @param qr
     * @return list of msgs
     * @throws JMSException
     * @throws NamingException
     */
    public ArrayList getQueue(GetImpl qr) throws JMSException, NamingException {

        if (!queueUp) {
            InitialContext ic = getInitialContext();
            init(ic);
        }

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

    /**
     * Return the InitialContext
     * @return InitialContext
     * @throws NamingException
     */
    public InitialContext getInitialContext()
            throws NamingException
    {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
        env.put(Context.PROVIDER_URL, SERVER);
        return new InitialContext(env);
    }


}
