package sipchat.handler.impl;

import sipchat.dao.GroupEvent;
import sipchat.dao.UserEvent;
import sipchat.enity.User;
import sipchat.handler.MessageHandler;
import sipchat.manager.*;
import sipchat.state.State;

import javax.sip.InvalidArgumentException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leeshun on 2017/7/11.
 */
public class MessageImpl implements MessageHandler {

    private CacheManager cacheManager;
    private RequestTaskHolder holder;
    private SipManager sipManager;
    private SipProfile sipProfile;

    private HeaderMaker maker;


    private AddressFactory addressFactory;
    private HeaderFactory headerFactory;
    private MessageFactory messageFactory;

    public MessageImpl(SipManager sipManager) {
        this.sipManager = sipManager;
        cacheManager = CacheManager.getInstance();
        holder = RequestTaskHolder.getInstance();
        sipProfile = SipProfile.getInstance();

        maker = new HeaderMaker(sipManager);
        initialize();
    }

    private void initialize() {
        addressFactory = sipManager.getAddressFactory();
        headerFactory = sipManager.getHeaderFactory();
        messageFactory = sipManager.getMessageFactory();
    }

    @Override
    public String onFriendList(String rawMessage) {
        if(rawMessage == null || rawMessage.equals("")) {
            throw new NullPointerException("message should contain user name");
        }
        List<String> list = cacheManager.getFriendList(rawMessage);
        String result = "";
        for(int i = 0;i < list.size();++i) {
            result += list.get(i);
            if(i != list.size()) {
                result += ";";
            }
        }
        return result;
    }

    @Override
    public String onGroupList(String rawMessage) {
        if(rawMessage == null || rawMessage.equals("")) {
            throw new NullPointerException("message should contain user name");
        }
        List<String> list = cacheManager.getGroupList(rawMessage);
        String result = "";
        for(int i = 0;i < list.size();++i) {
            result += list.get(i);
            if(i != list.size()) {
                result += ";";
            }
        }
        return result;
    }

    @Override
    public String onGroupMessage(FromHeader header, String rawMessage) {
        if(rawMessage == null || rawMessage.equals("")) {
            throw new NullPointerException();
        }
        String groupName = rawMessage.substring(0,rawMessage.indexOf('#'));
        List<String> list = cacheManager.getGroupMemberSipAddress(groupName);
        addRequest(header,list,State.GROUP_MESSAGE + rawMessage);
        return "true";
    }

    @Override
    public String onUpdatePassword(String username, String rawMessage) {
        if(rawMessage == null || rawMessage.equals("")) {
            throw new NullPointerException();
        }
        int state = UserEvent.updatePassword(username,rawMessage);
        if(state != 0) {
            return "true";
        }
        return "false";
    }

    @Override
    public String onJoinGroup(FromHeader username, String rawMessage) {
        if(rawMessage == null || rawMessage.equals("")) {
            throw new NullPointerException();
        }
        String groupName = rawMessage.substring(0,rawMessage.indexOf('#'));
        String memberName = rawMessage.substring(rawMessage.indexOf('#') + 1);

        List<String> list = cacheManager.getGroupMemberSipAddress(groupName);
        if(cacheManager.hasGroupMember(groupName,memberName)) {
            return "false";
        }
        int state = GroupEvent.addGroupMember(groupName,memberName);
        if(state != 0) {
            cacheManager.setGroupMembersChanged(true);
            addRequest(username,list,State.JOIN_GROUP + rawMessage);
        } else {
            return "false";
        }
        return "true";
    }

    @Override
    public String onExitGroup(FromHeader username, String rawMessage) {
        if(rawMessage == null || rawMessage.equals("")) {
            throw new NullPointerException();
        }
        rawMessage = State.EXIT_GROUP + rawMessage;
        String groupName = rawMessage.substring(0,rawMessage.indexOf('#'));
        String memberName = rawMessage.substring(rawMessage.indexOf('#') + 1);

        List<String> list = cacheManager.getGroupMemberSipAddress(groupName);

        int state = GroupEvent.deleteMember(groupName,memberName);
        if(state != 0) {
            cacheManager.setGroupMembersChanged(true);
            addRequest(username,list,State.EXIT_GROUP + rawMessage);
        } else {
            return "false";
        }
        return "true";
    }

    @Override
    public String onCreateGroup(String displayName, String rawMessage) {
        if(rawMessage == null || rawMessage.equals("")) {
            throw new NullPointerException();
        }
        String groupName = rawMessage.substring(0,rawMessage.indexOf('#'));
        String memberName = rawMessage.substring(rawMessage.indexOf('#') + 1);

        int state = GroupEvent.addGroupMember(groupName,memberName);
        if(state != 0) {
            cacheManager.setGroupMembersChanged(true);
        } else {
            return "false";
        }
        return "true";
    }

    @Override
    public String onLogin(String displayName, String rawMessage,String ipAddress) {
        if(rawMessage == null || rawMessage.equals("")) {
            throw new NullPointerException();
        }
        if(cacheManager.matchPassword(displayName,rawMessage)) {
            UserEvent.updateIPAddress(displayName,ipAddress);
            cacheManager.setUserChanged(true);
            return "true";
        }
        return "false";
    }

    @Override
    public String onAllFriends() {
        List<String> list = cacheManager.getAllFriends();
        String result = "";
        for(int i = 0;i < list.size();++i) {
            result += list.get(i);
            if(i != list.size()) {
                result += ";";
            }
        }
        return result;
    }

    @Override
    public String onAllGroups() {
        List<String> list = cacheManager.getAllGroups();
        String result = "";
        for(int i = 0;i < list.size();++i) {
            result += list.get(i);
            if(i != list.size()) {
                result += ";";
            }
        }
        return result;
    }


    private void addRequest(FromHeader fromHeader,List<String> list,String rawMessage) {
        Request request;
        String toName;
        for(String each : list) {
            try {
                toName = each.substring(each.indexOf(':') + 1,each.indexOf('@'));
                if(!fromHeader.getAddress().getDisplayName().equals(toName)) {
                    request = makeGroupMessage(fromHeader, each, rawMessage);
                    holder.addRequest(request);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private Request makeGroupMessage(FromHeader fromHeader, String to, String message) throws  Throwable {
        Request request;

        ToHeader toHeader = maker.makeToHeader(to);

        SipURI requestURI = addressFactory.createSipURI(maker.getAddress(to),maker.getAddress(to));
        requestURI.setTransportParam("udp");

        ArrayList viaHeaders = maker.makeViaHeader();

        CallIdHeader callIdHeader = sipManager.getSipProvider().getNewCallId();

        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L,Request.MESSAGE);

        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);

        request = messageFactory.createRequest(requestURI,
                Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwardsHeader);

        SipURI contactURI = addressFactory.createSipURI(sipProfile.getUsername(),sipProfile.getLocalHostAddress());
        contactURI.setPort(sipProfile.getLocalHostPort());
        Address contactAddress = addressFactory.createAddress(contactURI);
        contactAddress.setDisplayName(sipProfile.getUsername());
        ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
        request.addHeader(contactHeader);

        return request;
    }


   /* private String getUsername(String to) {
        return to.substring(to.indexOf(":") + 1, to.indexOf("@"));
    }

    private String getAddress(String to) {
        return to.substring(to.indexOf("@") + 1);
    }

    private ToHeader makeToHeader(String to) throws ParseException {
        String username = getUsername(to);
        String address = getAddress(to);
        SipURI toAddress = addressFactory.createSipURI(username, address);
        Address toNameAddress = addressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(username);
        ToHeader toHeader = headerFactory.createToHeader(toNameAddress,"toAddress");

        return toHeader;
    }

    private ArrayList makeViaHeader() throws ParseException, InvalidArgumentException {
        ArrayList viaHeaders = new ArrayList();
        ViaHeader viaHeader = headerFactory.createViaHeader(sipProfile.getLocalHostAddress(),
                sipProfile.getLocalHostPort(),"udp","branch");
        viaHeaders.add(viaHeader);

        return viaHeaders;
    }

    private ContactHeader makeContactHeader() throws ParseException {
        SipURI contactURI = addressFactory.createSipURI(sipProfile.getUsername(),sipProfile.getLocalHostAddress());
        contactURI.setPort(sipProfile.getLocalHostPort());
        Address contactAddress = addressFactory.createAddress(contactURI);
        contactAddress.setDisplayName(sipProfile.getUsername());

        ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);

        return contactHeader;
    }

    private ContentTypeHeader makeContentTypeHeader() throws ParseException {
        return headerFactory.createContentTypeHeader("text","plain");
    }*/
}
