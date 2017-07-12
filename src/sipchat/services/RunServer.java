package sipchat.services;

import sipchat.handler.MessageHandler;
import sipchat.handler.impl.MessageImpl;
import sipchat.listener.ServerSipListener;
import sipchat.manager.SipManager;
import sipchat.manager.SipProfile;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by leeshun on 2017/7/12.
 */
public class RunServer {
    private ServerSipListener serverSipListener;
    private SendRequestThread sendRequestThread;
    private MessageHandler messageHandler;
    private SipManager sipManager;

    public RunServer() {
        SipProfile.getInstance("admin",5060);
        sipManager = new SipManager();
        messageHandler = new MessageImpl(sipManager);
        serverSipListener = new ServerSipListener("admin",5060,messageHandler,sipManager);
        sendRequestThread = new SendRequestThread(sipManager.getSipProvider());
    }

    public void start() {
        sendRequestThread.start();
    }

    public static void main(String[] args) throws UnknownHostException {
        RunServer server = new RunServer();
        server.start();
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }
}
