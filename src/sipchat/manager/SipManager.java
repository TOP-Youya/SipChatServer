package sipchat.manager;

import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import java.util.Properties;

/**
 * Created by leeshun on 2017/7/11.
 */
public class SipManager {
    private SipFactory sipFactory;
    private SipStack sipStack;

    private HeaderFactory headerFactory;
    private AddressFactory addressFactory;
    private MessageFactory messageFactory;

    private SipProvider sipProvider;

    private SipProfile sipProfile;

    public SipManager() {
        sipProfile = SipProfile.getInstance();

        try {
            initialize();
        } catch (PeerUnavailableException e) {
            System.err.println("SIPManager initialized error " + e.getMessage());
        }
    }

    private void initialize() throws PeerUnavailableException {
        sipFactory = sipFactory.getInstance();
        sipFactory.setPathName("gov.nist");

        headerFactory = sipFactory.createHeaderFactory();
        addressFactory = sipFactory.createAddressFactory();
        messageFactory = sipFactory.createMessageFactory();

        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME","android sip");
        properties.setProperty("javax.sip.IP_ADDRESS",sipProfile.getLocalHostAddress());
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
                "androidServer_log.txt");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
                "androidServer_debug.log");
        sipStack = sipFactory.createSipStack(properties);
    }

    public SipFactory getSipFactory() {
        return sipFactory;
    }

    public SipStack getSipStack() {
        return sipStack;
    }

    public HeaderFactory getHeaderFactory() {
        return headerFactory;
    }

    public AddressFactory getAddressFactory() {
        return addressFactory;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public SipProvider getSipProvider() {
        return sipProvider;
    }

    public SipProfile getSipProfile() {
        return sipProfile;
    }

    public void setSipProvider(SipProvider provider) {
        this.sipProvider = provider;
    }

}
