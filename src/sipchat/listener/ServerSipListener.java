package sipchat.listener;

import sipchat.handler.MessageHandler;
import sipchat.handler.RequestHandler;
import sipchat.manager.SipManager;
import sipchat.manager.SipProfile;

import javax.sip.*;
import javax.sip.message.Request;
import java.util.TooManyListenersException;

/**
 * Created by leeshun on 2017/7/11.
 */
public class ServerSipListener implements SipListener {
    private SipManager sipManager;
    private SipProfile sipProfile;
    private SipProvider sipProvider;
    private RequestHandler requestHandler;

    public ServerSipListener(final String username, final int port, MessageHandler messageHandler, SipManager sipManager) {
        sipProfile = SipProfile.getInstance();
        this.sipManager = sipManager;
        requestHandler = new RequestHandler(messageHandler,sipManager);
        try {
            initialize();
        } catch (Throwable throwable) {
            System.err.println("SERVERSIPListener initialized error " + throwable.getMessage());
        }
    }

    private void initialize() throws
            InvalidArgumentException,
            TransportNotSupportedException,
            ObjectInUseException,
            TooManyListenersException {
        ListeningPoint udp = sipManager.getSipStack().createListeningPoint(sipProfile.getLocalHostAddress(),
                        sipProfile.getLocalHostPort(),
                        "udp");
        sipProvider = sipManager.getSipStack().createSipProvider(udp);
        sipProvider.addSipListener(this);

        sipManager.setSipProvider(sipProvider);
    }

    @Override
    public void processRequest(RequestEvent requestEvent) {
        Request request = requestEvent.getRequest();
        String method = request.getMethod();

        System.err.println(request.toString());

        switch (method) {
            case Request.REGISTER:
                try {
                    requestHandler.onRegister(request);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                break;
            case Request.SUBSCRIBE:
                try {
                    requestHandler.onSubscribe(request);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                break;
            case Request.PUBLISH:
                try {
                    requestHandler.onPublish(request);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                break;
            case Request.MESSAGE:
                try {
                    requestHandler.onMessage(request);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {

    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {

    }

    @Override
    public void processIOException(IOExceptionEvent ioExceptionEvent) {

    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {

    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {

    }
}
