package sipchat.handler;


import sipchat.dao.FriendEvent;
import sipchat.dao.UserEvent;
import sipchat.manager.CacheManager;
import sipchat.manager.HeaderMaker;
import sipchat.manager.RequestTaskHolder;
import sipchat.manager.SipManager;
import sipchat.state.State;

import javax.sip.ServerTransaction;
import javax.sip.SipProvider;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leeshun on 2017/7/11.
 */
public class RequestHandler {
    private SipManager sipManager;
    private CacheManager cacheManager;

    private MessageHandler messageHandler;

    private HeaderMaker headerMaker;

    private RequestTaskHolder holder;


    public RequestHandler(MessageHandler messageHandler, SipManager sipManager) {
        this.messageHandler = messageHandler;
        this.sipManager = sipManager;
        this.cacheManager = CacheManager.getInstance();

        headerMaker = new HeaderMaker(sipManager);

        holder = RequestTaskHolder.getInstance();
    }



    public void onRegister(Request request) throws Throwable {
        String content = new String(request.getRawContent());
        FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);

        System.err.println(fromHeader.toString());

        String username = fromHeader.getAddress().getDisplayName();

        System.err.println(username);

        String uri =  fromHeader.getAddress().getURI().toString();

        System.err.println(uri);

        System.err.println(uri.indexOf('@'));
        System.err.println(uri.lastIndexOf(':'));

        String address = uri.substring(uri.indexOf('@') + 1,uri.lastIndexOf(':'));

        System.err.println(address);
        if(cacheManager.hasUser(username)) {
            sendResponse(request,Response.FORBIDDEN,"");
            return;
        }
        int state =  UserEvent.addUser(username,content,address);
        if(state != 0) {
            cacheManager.setUserChanged(true);
            sendResponse(request,Response.OK,"");
        } else {
            sendResponse(request,Response.FORBIDDEN,"");
        }
    }

    public void onInfo(Request request) throws Throwable {
        String content = new String(request.getRawContent());
        String address = UserEvent.getIpAddress(content);
        FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
        String owner = fromHeader.getAddress().getDisplayName();
        if(!cacheManager.hasFriend(owner,content)) {
            FriendEvent.addFriend(owner, content);
            FriendEvent.addFriend(content, owner);
            cacheManager.setFriendChanged(true);
        }
        sendResponse(request,Response.OK,content + "#" + address);

        //Request notifyRequest = makeNotifyMessage(fromHeader.getAddress().getURI().toString(),content + "#" + address);
        //holder.addRequest(notifyRequest);
    }

    public void onPublish(Request request) throws Throwable {
        FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
        String username = fromHeader.getAddress().getDisplayName();
        String uri =  fromHeader.getAddress().getURI().toString();
        String address = uri.substring(uri.indexOf('@'),uri.lastIndexOf(':'));

        int state = UserEvent.updateIPAddress(username,address);
        if(state != 0) {
            //update OK
            sendResponse(request,Response.OK,"");
            List<String> list = cacheManager.getFriendsSipAddress(username);
            cacheManager.setUserChanged(true);
            //Make notify message
            //contain address
            for(String each : list) {
                Request notifyRequest = makeNotifyMessage(each,username + "#" + address);
                holder.addRequest(notifyRequest);
            }
        } else {
            //update fail
            sendResponse(request,Response.FORBIDDEN,"");
        }
    }

    public void onMessage(Request request) throws Throwable {
        String content = new String(request.getRawContent());

        String type = content.substring(0,4);
        String rawMessage = content.substring(4);

        FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);

        String result = "";
        switch (type) {
            case State.GET_FRIEND_LIST:
                result = messageHandler.onFriendList(fromHeader.getAddress().getDisplayName());
                //response OK
                //contain result
                sendResponse(request,Response.OK,result);
                break;
            case State.GET_GROUP_LIST:
                result = messageHandler.onGroupList(fromHeader.getAddress().getDisplayName());
                //response OK
                //contain result
                sendResponse(request,Response.OK,result);
                break;
            case State.GROUP_MESSAGE:
                result = messageHandler.onGroupMessage(fromHeader,rawMessage);
                if(result.equals("true")) {
                    //send ok
                    sendResponse(request,Response.OK,"");
                } else {
                    //send error
                    sendResponse(request,Response.FORBIDDEN,"");
                }
                break;
            case State.UPDATE_PASSWORD:
                result = messageHandler.onUpdatePassword(fromHeader.getAddress().getDisplayName(),rawMessage);
                if(result.equals("true")) {
                    sendResponse(request,Response.OK,"");
                } else {
                    sendResponse(request,Response.FORBIDDEN,"");
                }
                break;
            case State.JOIN_GROUP:
                result = messageHandler.onJoinGroup(fromHeader,rawMessage);
                if(result.equals("true")) {
                    sendResponse(request,Response.OK,"");
                } else {
                    sendResponse(request,Response.FORBIDDEN,"");
                }
                break;
            case State.EXIT_GROUP:
                result = messageHandler.onExitGroup(fromHeader,rawMessage);
                if(result.equals("true")) {
                    sendResponse(request,Response.OK,"");
                } else {
                    sendResponse(request,Response.FORBIDDEN,"");
                }
                break;
            case State.CREATE_GROUP:
                result = messageHandler.onCreateGroup(fromHeader.getAddress().getDisplayName(),rawMessage);
                if(result.equals("true")) {
                    sendResponse(request,Response.OK,"");
                } else {
                    sendResponse(request,Response.FORBIDDEN,"");
                }
                break;
            case State.LOGIN:
                System.err.println(rawMessage);
                result = messageHandler.onLogin(fromHeader.getAddress().getDisplayName(),rawMessage);
                if(result.equals("true")) {
                    sendResponse(request,Response.OK,"");
                } else {
                    System.err.println("login error");
                    sendResponse(request,Response.FORBIDDEN,"");
                }
                break;
            case State.ALL_FRIEND:
                result = messageHandler.onAllFriends();
                sendResponse(request,Response.OK,result);
                break;
            case State.ALL_GROUP:
                result = messageHandler.onAllGroups();
                sendResponse(request,Response.OK,result);
                break;
        }
    }


    private void sendResponse(Request request, final int state, final String message) throws Throwable {
        Response response = sipManager.getMessageFactory().createResponse(state,request);
        if(request.getMethod().equals(Request.SUBSCRIBE)) {
            ExpiresHeader expiresHeader = sipManager.getHeaderFactory().createExpiresHeader(3400);
            response.addHeader(expiresHeader);
            SubscriptionStateHeader stateHeader = sipManager.getHeaderFactory().createSubscriptionStateHeader("hello");
            response.addHeader(stateHeader);
        }
        ContentTypeHeader contentTypeHeader = sipManager.getHeaderFactory().createContentTypeHeader("text","plain");
        response.setContent(message,contentTypeHeader);

        ServerTransaction transaction = sipManager.getSipProvider().getNewServerTransaction(request);
        System.err.println(response.toString());
        transaction.sendResponse(response);
    }

    private Request makeNotifyMessage(String to,String message) throws Throwable {
        FromHeader fromHeader = headerMaker.makeFromHeader();
        ToHeader toHeader = headerMaker.makeToHeader(to);

        SipURI requestURI = sipManager.getAddressFactory().createSipURI(headerMaker.getAddress(to),headerMaker.getAddress(to));
        requestURI.setTransportParam("udp");

        ArrayList viaHeaders = headerMaker.makeViaHeader();

        CallIdHeader callIdHeader = sipManager.getSipProvider().getNewCallId();

        CSeqHeader cSeqHeader = sipManager.getHeaderFactory().createCSeqHeader(1L,Request.NOTIFY);

        MaxForwardsHeader maxForwardsHeader = sipManager.getHeaderFactory().createMaxForwardsHeader(70);

        Request request = sipManager.getMessageFactory().createRequest(requestURI,
                Request.NOTIFY, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwardsHeader);

        ContentTypeHeader contentTypeHeader = headerMaker.makeContentTypeHeader();
        request.setContent(message,contentTypeHeader);

        return request;
    }

}
