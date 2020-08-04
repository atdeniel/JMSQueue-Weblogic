package com.atdeniel.games.jms.queue.services;

import com.atdeniel.games.jms.queue.services.impl.GetImpl;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.ArrayList;

public interface GetService {

    public ArrayList getQueue(GetImpl qr) throws JMSException, NamingException;
    public void init(Context ctx) throws NamingException, JMSException;
    public void close()throws JMSException;
}
