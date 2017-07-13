package sipchat.services;

import sipchat.manager.RequestTaskHolder;

import javax.sip.ClientTransaction;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.TransactionUnavailableException;
import javax.sip.message.Request;

/**
 * Created by leeshun on 2017/7/12.
 */
public class SendRequestThread implements Runnable {

    private SipProvider provider;
    private RequestTaskHolder holder;

    public SendRequestThread(SipProvider provider) {
        this.provider = provider;
        holder = RequestTaskHolder.getInstance();
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Request request = holder.getRequest();
                System.err.println(request.toString());
                ClientTransaction transaction = provider.getNewClientTransaction(request);
                transaction.sendRequest();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TransactionUnavailableException e) {
                e.printStackTrace();
            } catch (SipException e) {
                e.printStackTrace();
            }
        }
    }
}
