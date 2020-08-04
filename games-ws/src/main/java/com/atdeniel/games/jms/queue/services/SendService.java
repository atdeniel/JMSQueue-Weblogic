package com.atdeniel.games.jms.queue.services;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.NamingException;

public interface SendService {

    public void sendData(String data) throws JMSException, NamingException;
    public void init(Context ctx) throws NamingException, JMSException;
    public void close() throws JMSException;


}
