package sipchat.manager;

import javax.sip.InvalidArgumentException;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by leeshun on 2017/7/12.
 */
public class HeaderMaker {
    private SipManager sipManager;
    private SipProfile sipProfile;

    private SipProvider sipProvider;
    private AddressFactory addressFactory;
    private MessageFactory messageFactory;
    private HeaderFactory headerFactory;

    public HeaderMaker(SipManager sipManager) {
        this.sipManager = sipManager;
        this.sipProfile = SipProfile.getInstance();

        addressFactory = sipManager.getAddressFactory();
        messageFactory = sipManager.getMessageFactory();
        headerFactory = sipManager.getHeaderFactory();
    }

    public FromHeader makeFromHeader() throws ParseException {
        SipURI sipURI = addressFactory.createSipURI(sipProfile.getUsername(),sipProfile.getLocalHostAddress());
        sipURI.setPort(sipProfile.getLocalHostPort());
        Address address = addressFactory.createAddress(sipURI);
        address.setDisplayName(sipProfile.getUsername());
        return headerFactory.createFromHeader(address,"textserver");
    }


    public String getUsername(String to) {
        return to.substring(to.indexOf(":") + 1, to.indexOf("@"));
    }

    public String getAddress(String to) {
        return to.substring(to.indexOf("@") + 1);
    }

    public ToHeader makeToHeader(String to) throws ParseException {
        String username = getUsername(to);
        String address = getAddress(to);
        SipURI toAddress = addressFactory.createSipURI(username, address);
        Address toNameAddress = addressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(username);
        ToHeader toHeader = headerFactory.createToHeader(toNameAddress,"toAddress");

        return toHeader;
    }

    public ArrayList makeViaHeader() throws ParseException, InvalidArgumentException {
        ArrayList viaHeaders = new ArrayList();
        ViaHeader viaHeader = headerFactory.createViaHeader(sipProfile.getLocalHostAddress(),
                sipProfile.getLocalHostPort(),"udp","branch");
        viaHeaders.add(viaHeader);

        return viaHeaders;
    }

    public ContactHeader makeContactHeader() throws ParseException {
        SipURI contactURI = addressFactory.createSipURI(sipProfile.getUsername(),sipProfile.getLocalHostAddress());
        contactURI.setPort(sipProfile.getLocalHostPort());
        Address contactAddress = addressFactory.createAddress(contactURI);
        contactAddress.setDisplayName(sipProfile.getUsername());

        ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);

        return contactHeader;
    }

    public ContentTypeHeader makeContentTypeHeader() throws ParseException {
        return headerFactory.createContentTypeHeader("text","plain");
    }
}
